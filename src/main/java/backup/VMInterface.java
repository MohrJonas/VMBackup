package backup;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class VMInterface {

	@SneakyThrows
	private boolean isShutdown(String name) {
		final SimpleImmutableEntry<String, String> entry = Main.runAndGet("sudo", "virsh", "-q", "list", "--all");
		final List<String> vms =
				Arrays.stream(entry.getKey().split("\n"))
						.map(String::trim)
						.filter(s -> s.contains("shut off"))
						.map(s -> s.split("\\ +")[1])
						.toList();
		return vms.stream().anyMatch(s -> s.equals(name));
	}

	@SneakyThrows
	public void validateVMNames(String[] names) {
		final SimpleImmutableEntry<String, String> entry = Main.runAndGet("sudo", "virsh", "-q", "list", "--all");
		final List<String> vms =
				Arrays.stream(entry.getKey().split("\n"))
						.map(String::trim)
						.map(s -> s.split("\\ +")[1])
						.toList();
		if (!Arrays.stream(names).allMatch(vms::contains)) throw new IllegalArgumentException("Invalid vm name(s)");
	}

	@SneakyThrows
	public boolean isVMRunning(String name) {
		final SimpleImmutableEntry<String, String> entry = Main.runAndGet("sudo", "virsh", "-q", "list");
		final List<String> vms =
				Arrays.stream(entry.getKey().split("\n"))
						.map(String::trim)
						.map(s -> s.split("\\ +")[1])
						.toList();
		return vms.contains(name);
	}

}
