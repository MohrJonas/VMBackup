package backup;

import org.joda.time.DateTime;

import java.io.Serializable;

public record BackupTime(String vmName, DateTime time) implements Serializable {
}
