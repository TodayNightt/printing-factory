package org.printing_factory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import org.jodconverter.core.office.OfficeException;
import org.printing_factory.docu.DocFormat;
import org.printing_factory.docu.DocumentManager;
import org.printing_factory.printing.PathParser;
import org.printing_factory.printing.Printer;
import org.printing_factory.printing.ipp.jobs.CreateJob;
import org.printing_factory.printing.ipp.jobs.SendDocument;

import de.gmuth.ipp.attributes.MediaSource;

public class JobCreator implements Runnable {
	private static final Logger logger = Logger.getLogger(JobCreator.class.getName());
	private ConcurrentLinkedQueue<PathParser> filePathQueue;
	private ConcurrentLinkedQueue<SendDocument> printJobsQueue;
	private Printer printer;

	public JobCreator(ConcurrentLinkedQueue<PathParser> filePathQueue,
			ConcurrentLinkedQueue<SendDocument> printJobsQueue,
			Printer printer) {
		this.filePathQueue = filePathQueue;
		this.printJobsQueue = printJobsQueue;
		this.printer = printer;
	}

	@Override
	public void run() {
		while (true) {
			if (!filePathQueue.isEmpty()) {
				PathParser path = filePathQueue.poll();
				try {
					var docProps = DocumentManager.manage(new File(path.getPath().toString()));
					var job = createPrintJob(path, docProps);

					this.printJobsQueue.add(job);
				} catch (OfficeException e) {
					logger.severe("Document management failed: " + e.getMessage());
				} catch (Exception e) {
					logger.severe(e.getMessage());
				}

			}
		}
	}

	SendDocument createPrintJob(PathParser path,
			DocumentManager.DocumentManagerProps docProps) throws InvalidDocumentException {
		if (docProps == null || docProps.fileStream() == null) {
			throw new InvalidDocumentException(path.getFileName());
		}

		try {
			var jobProps = new CreateJob.CreateJobProps(
					String.format("Printing %s", path.getFileName()),
					DocFormat.toDocumentFormat(docProps.docFormat()),
					path.getSides(),
					MediaSource.Tray1,
					path.getCopies());

			return this.printer.createJob(jobProps)
					.send()
					.setFile((ByteArrayInputStream) docProps.fileStream());

		} catch (Exception e) {
			logger.severe("Failed to create print job: " + e.getMessage());
			return null;
		}
	}

	class InvalidDocumentException extends Exception {
		InvalidDocumentException(String fileName) {
			super("Invalid document properties for file : " + fileName);
		}
	}

}
