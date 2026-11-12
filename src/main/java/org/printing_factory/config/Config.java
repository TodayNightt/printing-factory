package org.printing_factory.config;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;

/**
 * Config
 */
public class Config {

	public String printerUri;

	public String printDir;

	public Config() throws IllegalStateException {
		Map<String, String> envs = System.getenv();

		ArrayDeque<String> requiredEnvs = new ArrayDeque<>(List.of("PRINT_DIR", "PRINTER_IP"));

		requiredEnvs.removeIf(envs::containsKey);

		if (!requiredEnvs.isEmpty()) {
			throw new IllegalStateException("Missing required environment variables: " + requiredEnvs);
		}

		this.printerUri = envs.get("PRINTER_IP");
		this.printDir = envs.get("PRINT_DIR");
	}
}
