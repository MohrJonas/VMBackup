package backup;

import lombok.experimental.UtilityClass;

import java.nio.file.Path;

@UtilityClass
public class Mounter {

	public void mount(Path partition, Path folder) {
		Main.runAndForward("sudo", "mount", "-o", "ro", partition.toAbsolutePath().toString(), folder.toAbsolutePath().toString());
	}

	public void unmount(Path folder) {
		Main.runAndForward("sudo", "umount", folder.toAbsolutePath().toString());
	}

}
