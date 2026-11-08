package org.printing_factory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.jodconverter.core.office.OfficeException;
import org.printing_factory.bindings.Bindings;
import org.printing_factory.docu.DocumentManager;
import org.printing_factory.printing.IppConnection;
import org.printing_factory.printing.PathParser;
import org.printing_factory.printing.Printer;
import org.printing_factory.printing.ipp.jobs.CreateJob;
import org.printing_factory.printing.ipp.jobs.GetJobAttributes;
import org.printing_factory.printing.ipp.jobs.SendDocument;
import org.printing_factory.util.PrintPath;

public class Main {
	private static final Logger logger = Logger.getLogger(Main.class.getName());

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
					path.getPaperType(),
					path.getCopies());

			return printer.createJob(jobProps)
					.send()
					.setFile((ByteArrayInputStream) docProps.fileStream());

		} catch (Exception e) {
			logger.severe("Failed to create print job: " + e.getMessage());
			return null;
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException, OfficeException {
		Logger logger = Logger.getLogger("Main");
		logger.info("Starting printing factory");
		Path dir = Paths.get(System.getenv("PRINT_DIR"));
		var filePathQueue = new ConcurrentLinkedQueue<PathParser>();
		var printJobQueue = new ConcurrentLinkedQueue<SendDocument>();
		var fileSent = new CopyOnWriteArrayList<GetJobAttributes>();

		var printerUri = URI.create(String.format("ipps://%s:443/ipp/print",
				System.getenv("PRINTER_IP")));
		var ippConnection = new IppConnection(printerUri);
		var printer = new Printer(ippConnection);

		Thread.startVirtualThread(new JobsSender(printJobQueue, fileSent));

		Thread.startVirtualThread(new StatusChecker(fileSent));

		new WatchFolder(dir, (path) -> {
			Logger wfl = Logger.getLogger("WatchFolders");
			wfl.info("File created: " + path.toString());
			PathParser parsedPath = PathParser.from(PrintPath.into(path, dir).getParts(), path.toAbsolutePath());

			filePathQueue.add(parsedPath);

		});

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
