package backup;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;

import java.io.Serializable;

@ToString
@EqualsAndHashCode(callSuper = false)
public record BackupTime(String vmName, DateTime time) implements Serializable {}
