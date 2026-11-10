package org.printing_factory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.printing_factory.config.Config;
import org.printing_factory.printing.Printer;
import org.printing_factory.printing.ipp.jobs.GetJobAttributes;
import org.printing_factory.printing.ipp.jobs.SendDocument;
import org.printing_factory.watcher.Watcher;
import org.printing_factory.watcher.WatcherFactory;
import org.printing_factory.watcher.path.WatchPath;

/**
 * Application
 */
public class Application {
	private static final Logger logger = Logger.getLogger(Application.class.getName());

	public static void run() throws IOException, IllegalStateException {
		logger.info("Starting printing factory");

		Config config = new Config();

		WatchPath watchPath = new WatchPath(config.printDir);
		var filePathQueue = new ConcurrentLinkedQueue<Path>();
		var printJobQueue = new ConcurrentLinkedQueue<SendDocument>();
		var fileSent = new CopyOnWriteArrayList<GetJobAttributes>();

		var printer = new Printer(URI.create(config.printerUri));

		Thread.startVirtualThread(new JobsSender(printJobQueue, fileSent));

		Thread.startVirtualThread(new StatusChecker(fileSent));

		Watcher watcher = WatcherFactory.create(watchPath, filePathQueue);

		watcher.watch(watchPath);

		Thread.startVirtualThread(new JobCreator(filePathQueue, printJobQueue, printer));
	}

}
