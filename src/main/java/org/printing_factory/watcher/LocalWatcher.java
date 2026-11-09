package org.printing_factory.watcher;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.io.monitor.FileEntry;
import org.printing_factory.printing.PathParser;
import org.printing_factory.util.Logging;
import org.printing_factory.watcher.path.WatchPath;

/**
 * LocalWatcher
 */
public class LocalWatcher implements Watcher {
	private ConcurrentLinkedQueue<PathParser> sender;

	public LocalWatcher(ConcurrentLinkedQueue<PathParser> sender) {
		this.sender = sender;
	}

	class ExceptionCapturer implements UncaughtExceptionHandler, Logging {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			logger.severe(e.getMessage());
		}

	}

	@Override
	public void watch(WatchPath path, java.util.function.Function<Path, PathParser> eventHandler) throws IOException {
		FileAlterationObserver observer = FileAlterationObserver.builder()
				.setRootEntry(new FileEntry(path.getPath().toFile()))
				.get();

		observer.addListener(new FileAlterationListenerAdaptor() {
			@Override
			public void onFileCreate(File file) {
				PathParser result = eventHandler.apply(file.toPath());
				setToQueue(result);
			}

			@Override
			public void onFileChange(File file) {
				System.out.println("File modified: " + file.getName());
			}

			@Override
			public void onFileDelete(File file) {
				System.out.println("File deleted: " + file.getName());
			}
		});

		FileAlterationMonitor monitor = new FileAlterationMonitor(5000); // Poll every 5 seconds
		monitor.addObserver(observer);

		var t = Thread.startVirtualThread(monitor);

		t.setUncaughtExceptionHandler(new ExceptionCapturer());
	}

	private void setToQueue(PathParser path) {
		this.sender.add(path);
	}

}
