package backup;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.Hours;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Optional;

public class Main {


	private static final Path LAST_BACKUP_FILE_VM_1 = Path.of("/home", System.getProperty("user.name"), ".vmbackup", "lastBackup1.data");
	private static final Path LAST_BACKUP_FILE_VM_2 = Path.of("/home", System.getProperty("user.name"), ".vmbackup", "lastBackup2.data");

	//VM1-name
	//VM2-name
	//VM1-disk
	//VM2-disk
	//Backup-Path-VM1
	//Backup-Path-VM2
	//Hours between backups
	//Backups to keep
	@SneakyThrows
	public static void main(String[] args) {
		if (args.length != 7) {
			System.err.println("Usage: MVBackup [VM1-name] [VM2-name] [VM1-disk-path] [VM2-disk-path] [Backup-VM1-path] [Backup-VM2-path] [hours btw. backups]");
			System.exit(-1);
		}
		final Configuration backupConfig = new Configuration(args[0], args[1], Path.of(args[2]), Path.of(args[3]), Path.of(args[4]), Path.of(args[5]), Integer.parseInt(args[6]));
		System.out.println("Got config " + backupConfig);
		ConfigValidator.validateConfig(backupConfig);
		System.out.println("Config seems valid");
		if (shouldCreateBackup(backupConfig.vm1Name(), backupConfig.hoursBetweenBackups(), 1)) {
			System.out.println("Backup of VM 1 should be created");
			System.out.println("Mounting disk " + backupConfig.vm1Disk().toAbsolutePath() + " to /mnt/vm1");
			Mounter.mount(backupConfig.vm1Disk(), Path.of("/mnt/vm1"));
			System.out.println("Creating Backup of VM 1");
			createBackup(Path.of("/mnt/vm1"), backupConfig.vm1BackupFolder());
			System.out.println("Unmounting disk of VM 1");
			Mounter.unmount(Path.of("/mnt/vm1"));
			System.out.println("Setting last backup date of VM1");
			setLastBackupDate(DateTime.now(), 1);
		} else System.out.println("Backup of VM 1 shouldn't be created");
		if (shouldCreateBackup(backupConfig.vm2Name(), backupConfig.hoursBetweenBackups(), 2)) {
			System.out.println("Backup of VM 2 should be created");
			System.out.println("Mounting disk " + backupConfig.vm2Disk().toAbsolutePath() + " to /mnt/vm1");
			Mounter.mount(backupConfig.vm2Disk(), Path.of("/mnt/vm2"));
			System.out.println("Creating Backup of VM 2");
			createBackup(Path.of("/mnt/vm2"), backupConfig.vm2BackupFolder());
			System.out.println("Unmounting disk of VM 2");
			Mounter.unmount(Path.of("/mnt/vm2"));
			System.out.println("Setting last backup date of VM 2");
			setLastBackupDate(DateTime.now(), 2);
		} else System.out.println("Backup of VM 2 shouldn't be created");
	}


	@SneakyThrows
	private static boolean shouldCreateBackup(String vmName, int timeBetween, int vmID) {
		if (VMInterface.isVMRunning(vmName)) {
			System.out.println("VM " + vmID + " is currently running. Skipping backup");
			return false;
		}
		final Optional<DateTime> lastBackupTime = getLastBackupDate(vmID);
		if (lastBackupTime.isEmpty()) {
			System.out.println("No previous backup for VM " + vmID + " found. Creating one now");
			return true;
		}
		return Hours.hoursBetween(DateTime.now(), lastBackupTime.get()).getHours() >= timeBetween;
	}

	@SneakyThrows
	private static void createBackup(Path diskPath, Path backupPath) {
		runAndForward("sudo", "rsync", "-a", "-v", "--delete", diskPath.toAbsolutePath().toString(), backupPath.toAbsolutePath().toString());
	}


	@SneakyThrows
	private static Optional<DateTime> getLastBackupDate(int vmID) {
		switch (vmID) {
			case 1 -> {
				if (!Files.exists(LAST_BACKUP_FILE_VM_1)) return Optional.empty();
				@Cleanup final ObjectInputStream stream1 = new ObjectInputStream(Files.newInputStream(LAST_BACKUP_FILE_VM_1));
				return Optional.of(((DateTime) stream1.readObject()));
			}
			case 2 -> {
				if (!Files.exists(LAST_BACKUP_FILE_VM_2)) return Optional.empty();
				@Cleanup final ObjectInputStream stream2 = new ObjectInputStream(Files.newInputStream(LAST_BACKUP_FILE_VM_2));
				return Optional.of(((DateTime) stream2.readObject()));
			}
			default -> throw new IllegalStateException("Unexpected value: " + vmID);
		}
	}

	@SneakyThrows
	private static void setLastBackupDate(DateTime time, int vmID) {
		switch (vmID) {
			case 1 -> {
				@Cleanup final ObjectOutputStream stream1 = new ObjectOutputStream(Files.newOutputStream(LAST_BACKUP_FILE_VM_1));
				stream1.writeObject(time);
			}
			case 2 -> {
				@Cleanup final ObjectOutputStream stream2 = new ObjectOutputStream(Files.newOutputStream(LAST_BACKUP_FILE_VM_2));
				stream2.writeObject(time);
			}
			default -> throw new IllegalStateException("Unexpected value: " + vmID);
		}
	}

	@SneakyThrows
	public static SimpleImmutableEntry<String, String> runAndGet(String... params) {
		final Process process = new ProcessBuilder(params).start();
		process.waitFor();
		return new SimpleImmutableEntry<>(IOUtils.toString(process.getInputStream(), Charset.defaultCharset()), IOUtils.toString(process.getErrorStream(), Charset.defaultCharset()));
	}

	@SneakyThrows
	public static void runAndForward(String... params) {
		new ProcessBuilder(params).inheritIO().start().waitFor();
	}
}
