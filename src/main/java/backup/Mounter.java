package backup;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.nio.file.Path;

@UtilityClass
public class Mounter {

	@SneakyThrows
	public int mount(Path partition, Path folder) {
		final Process process = Runtime.getRuntime().exec("sudo mount " + partition.toAbsolutePath() + " " + folder.toAbsolutePath());
		process.waitFor();
		return process.exitValue();
	}

	@SneakyThrows
	public int unmount(Path folder) {
		final Process process = Runtime.getRuntime().exec("sudo umount " + folder.toAbsolutePath());
		process.waitFor();
		return process.exitValue();
	}

}
