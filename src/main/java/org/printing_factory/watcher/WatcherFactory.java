package org.printing_factory.watcher;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.printing_factory.path.WatchPath;
import org.printing_factory.printing.PathParser;

/**
 * WatchFactory
 */
public class WatcherFactory {

	public static Watcher create(WatchPath path, ConcurrentLinkedQueue<PathParser> sender) {
		if (path.isSmb()) {
			return new LocalWatcher(sender);
		} else {
			return new LocalWatcher(sender);
		}
	}
}
