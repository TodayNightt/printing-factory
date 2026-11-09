package org.printing_factory.watcher;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;

import org.printing_factory.printing.PathParser;
import org.printing_factory.watcher.path.WatchPath;

/**
 * Watcher
 */
public interface Watcher {
	void watch(WatchPath path, Function<Path, PathParser> eventHandler) throws IOException;
}
