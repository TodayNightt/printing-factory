package org.printing_factory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.printing_factory.config.Config;
import org.printing_factory.document.DocumentDescriptor;
import org.printing_factory.printing.JobDescriptor;
import org.printing_factory.printing.Printer;
import org.printing_factory.watcher.Watcher;
import org.printing_factory.watcher.WatcherFactory;
import org.printing_factory.watcher.path.WatchPath;

import com.sun.star.uno.RuntimeException;

/**
 * Application
 */
public class Application {
	private static final Logger logger = Logger.getLogger(Application.class.getName());

	public static void run() {
		logger.info("Starting printing factory");

		Config config;
		try {
			config = new Config();
		} catch (IllegalStateException e) {
			logger.severe(e.getMessage());
			throw new RuntimeException(e);
		}

		WatchPath watchPath = new WatchPath(config.printDir);
		var printer = new Printer(config.printerUri);
		var filePathQueue = new ConcurrentLinkedQueue<Path>();
		var printJobQueue = new ConcurrentLinkedQueue<DocumentDescriptor>();
		var fileSent = new CopyOnWriteArrayList<JobDescriptor>();

		Thread.startVirtualThread(new JobCreator(filePathQueue, printJobQueue));

		Thread.startVirtualThread(new JobSender(printJobQueue, fileSent, printer));

		Thread.startVirtualThread(new StatusChecker(fileSent));

		Watcher watcher = WatcherFactory.create(watchPath, filePathQueue);

		try {
			watcher.watch(watchPath);
		} catch (IOException e) {
			logger.severe("Failed to start watcher :" + e.getMessage());
		}
	}

}
