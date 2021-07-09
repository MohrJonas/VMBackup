package backup;

import java.nio.file.Path;

public record Configuration(String vm1Name, String vm2Name, Path vm1Disk, Path vm2Disk, Path vm1BackupFolder,
                            Path vm2BackupFolder, int hoursBetweenBackups) {
}
