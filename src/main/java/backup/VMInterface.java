package backup;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.io.IOUtils;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class VMInterface {

	@SneakyThrows
	private boolean isShutdown(String name) {
		final Process process = Runtime.getRuntime().exec("sudo virsh -q list --all");
		process.waitFor();
		final List<String> vms =
				Arrays.stream(IOUtils.toString(process.getInputStream(), Charset.defaultCharset()).split("\n"))
						.map(String::trim)
						.filter(s -> s.contains("shut off"))
						.map(s -> s.split("\\ +")[1])
						.toList();
		return vms.stream().anyMatch(s -> s.equals(name));
	}

	@SneakyThrows
	public void validateVMNames(String[] names) {
		val process = Runtime.getRuntime().exec("sudo virsh -q list --all");
		process.waitFor();
		final List<String> vms =
				Arrays.stream(IOUtils.toString(process.getInputStream(), Charset.defaultCharset()).split("\n"))
						.map(String::trim)
						.map(s -> s.split("\\ +")[1])
						.toList();
		if (!Arrays.stream(names).allMatch(vms::contains)) throw new IllegalArgumentException("Invalid vm name(s)");
	}

	@SneakyThrows
	public boolean isVMRunning(String name) {
		val process = Runtime.getRuntime().exec("sudo virsh -q list");
		process.waitFor();
		final List<String> vms =
				Arrays.stream(IOUtils.toString(process.getInputStream(), Charset.defaultCharset()).split("\n"))
						.map(String::trim)
						.map(s -> s.split("\\ +")[1])
						.toList();
		return vms.contains(name);
	}

}
