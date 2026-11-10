package org.printing_factory.watcher;

import java.io.IOException;
import org.printing_factory.watcher.path.WatchPath;

/**
 * Watcher
 */
public interface Watcher {
	void watch(WatchPath path) throws IOException;
}
