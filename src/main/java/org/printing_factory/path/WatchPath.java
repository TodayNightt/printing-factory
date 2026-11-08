package org.printing_factory.path;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * WatchPath
 */
public class WatchPath {
	private PathType type;
	private Path path;

	enum PathType {
		Smb, Local;
	}

	public WatchPath(String path) {
		this.type = switch (path.toString()) {
			case String s when s.startsWith("smb") -> PathType.Smb;
			default -> PathType.Local;
		};

		this.path = switch (this.type) {
			case Smb ->
				Paths.get(URI.create(path));
			case Local -> Path.of(path);
		};
	}

	public boolean isSmb() {
		return this.type == PathType.Smb;
	}

	public Path getPath() {
		return this.path;
	}
}
