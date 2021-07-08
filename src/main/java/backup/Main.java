package backup;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.joda.time.DateTime;
import org.joda.time.Hours;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class Main {


	private static final Path LAST_BACKUP_FILE = Path.of("/home", System.getProperty("user.name"), ".vmbackup", "lastBackup.data");

	//VM1-name
	//VM2-name
	//VM1-disk
	//VM2-disk
	//Backup-Path-VM1
	//Backup-Path-VM2
	//Hours between backups
	//Backups to keep
	//Mode -s -n
	@SneakyThrows
	public static void main(String[] args) {
		if (args.length != 8) {
			System.err.println("Usage: MVBackup [VM1-name] [VM2-name] [VM1-disk-path] [VM2-disk-path] [Backup-VM1-path] [Backup-VM2-path] [hours btw. backups] [-s | -n]");
		}
		final Configuration backupConfig = new Configuration(args[0], args[1], Path.of(args[2]), Path.of(args[3]), Path.of(args[4]), Path.of(args[5]), Hours.hours(Integer.parseInt(args[6])), Configuration.parse(args[7]));
		ConfigValidator.validateConfig(backupConfig);

		switch (backupConfig.mode()) {
			case SHUTDOWN -> {

			}
			case REGULAR -> {

			}
		}
	}


	@SneakyThrows
	private static boolean shouldCreateBackup(String vmName, Path backupPath, int timeBetween) {
		if (VMInterface.isVMRunning(vmName)) return false;
		final Optional<DateTime> lastBackupTime = getLastBackupDate();
		if (lastBackupTime.isEmpty()) return true;
		return Hours.hoursBetween(DateTime.now(), lastBackupTime.get()).getHours() >= timeBetween;
	}

	private static void createBackup(Path diskPath, Path backupPath) {

	}

	private static void removeOldestBackups(Path backupPath, int toKeep) {

	}

	@SneakyThrows
	private static Optional<DateTime> getLastBackupDate() {
		if (!Files.exists(LAST_BACKUP_FILE)) return Optional.empty();
		@Cleanup final ObjectInputStream stream = new ObjectInputStream(Files.newInputStream(LAST_BACKUP_FILE));
		return Optional.of(((DateTime) stream.readObject()));
	}

	@SneakyThrows
	private static void setLastBackupDate(DateTime time) {
		@Cleanup final ObjectOutputStream stream = new ObjectOutputStream(Files.newOutputStream(LAST_BACKUP_FILE));
		stream.writeObject(time);
	}
}
