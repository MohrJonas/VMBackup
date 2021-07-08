package backup;

import org.joda.time.Hours;

import java.nio.file.Path;

public record Configuration(String vm1Name, String vm2Name, Path vm1Disk, Path vm2Disk, Path vm1BackupFolder,
                            Path vm2BackupFolder, Hours hoursBetweenBackups, MODE mode) {

	public static MODE parse(String s) {
		switch (s) {
			case "-n" -> {
				return MODE.REGULAR;
			}
			case "-s" -> {
				return MODE.SHUTDOWN;
			}
			default -> throw new IllegalArgumentException("Can't parse mode " + s);
		}
	}

	public enum MODE {
		SHUTDOWN,
		REGULAR
	}
}
