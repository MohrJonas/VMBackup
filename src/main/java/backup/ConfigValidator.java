package backup;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
public class ConfigValidator {

	@SneakyThrows
	public void validateConfig(Configuration config) {
		VMInterface.validateVMNames(new String[]{config.vm1Name(), config.vm2Name()});
		assertExistAndReadable(config.vm1Disk());
		assertExistAndReadable(config.vm2Disk());
		assertExistAndWritable(config.vm1BackupFolder());
		assertExistAndWritable(config.vm2BackupFolder());
		if (config.hoursBetweenBackups() <= 0)
			throw new IllegalArgumentException("Hours between backup have to be greater that 0");
	}

	private void assertExistAndReadable(Path p) {
		if (!Files.exists(p) || !Files.isReadable(p))
			throw new IllegalArgumentException("Path " + p + " either doesn't exist or isn't readable");
	}

	private void assertExistAndWritable(Path p) {
		if (!Files.exists(p) || !Files.isWritable(p))
			throw new IllegalArgumentException("Path " + p + " either doesn't exist or isn't readable");
	}
}
