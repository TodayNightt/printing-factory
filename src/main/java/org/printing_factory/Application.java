package org.printing_factory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.jodconverter.core.office.OfficeException;
import org.printing_factory.bindings.Bindings;
import org.printing_factory.config.Config;
import org.printing_factory.docu.DocumentManager;
import org.printing_factory.printing.PathParser;
import org.printing_factory.printing.Printer;
import org.printing_factory.printing.ipp.jobs.CreateJob;
import org.printing_factory.printing.ipp.jobs.GetJobAttributes;
import org.printing_factory.printing.ipp.jobs.SendDocument;
import org.printing_factory.watcher.Watcher;
import org.printing_factory.watcher.WatcherFactory;
import org.printing_factory.watcher.path.WatchPath;

import de.gmuth.ipp.attributes.MediaSource;

/**
 * Application
 */
public class Application {
	private static final Logger logger = Logger.getLogger(Application.class.getName());

	private static SendDocument createPrintJob(Printer printer, PathParser path,
			DocumentManager.DocumentManagerProps docProps) {
		if (docProps == null || docProps.fileStream() == null) {
			logger.severe("Invalid document properties");
			return null;
		}

		try {
			var jobProps = new CreateJob.CreateJobProps(
					String.format("Printing %s", path.getFileName()),
					Bindings.into(docProps.docFormat()),
					path.getSides(),
					MediaSource.Tray1,
					path.getCopies());

			return printer.createJob(jobProps)
					.send()
					.setFile((ByteArrayInputStream) docProps.fileStream());

		} catch (Exception e) {
			logger.severe("Failed to create print job: " + e.getMessage());
			return null;
		}
	}

	public static void run() throws IOException, IllegalStateException {
		Logger logger = Logger.getLogger("Main");
		logger.info("Starting printing factory");

		Config config = new Config();

		WatchPath watchPath = new WatchPath(config.printDir);
		var filePathQueue = new ConcurrentLinkedQueue<PathParser>();
		var printJobQueue = new ConcurrentLinkedQueue<SendDocument>();
		var fileSent = new CopyOnWriteArrayList<GetJobAttributes>();

		var printer = new Printer(URI.create(config.printerUri));

		Thread.startVirtualThread(new JobsSender(printJobQueue, fileSent));

		Thread.startVirtualThread(new StatusChecker(fileSent));

		Watcher watcher = WatcherFactory.create(watchPath, filePathQueue);

		watcher.watch(watchPath, (path) -> new PathParser(path));

		Thread.startVirtualThread(new JobCreator(filePathQueue, printJobQueue, (path) -> {
			try {
				var docProps = DocumentManager.manage(new File(path.getPath().toString()));
				return createPrintJob(printer, path, docProps);
			} catch (OfficeException e) {
				logger.severe("Document management failed: " + e.getMessage());
				return null;
			}
		}));
	}

}
