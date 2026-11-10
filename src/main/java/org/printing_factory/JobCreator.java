package org.printing_factory;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import org.printing_factory.docu.DocumentManager;
import org.printing_factory.docu.DocumentManager.DocumentManagerProps;
import org.printing_factory.file.FileDescriptor;
import org.printing_factory.printing.Printer;
import org.printing_factory.printing.ipp.jobs.CreateJob;
import org.printing_factory.printing.ipp.jobs.SendDocument;

import de.gmuth.ipp.attributes.DocumentFormat;
import de.gmuth.ipp.attributes.MediaSource;

public class JobCreator implements Runnable {
	private static final Logger logger = Logger.getLogger(JobCreator.class.getName());
	private ConcurrentLinkedQueue<Path> filePathQueue;
	private ConcurrentLinkedQueue<SendDocument> printJobsQueue;
	private Printer printer;

	public JobCreator(ConcurrentLinkedQueue<Path> filePathQueue,
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
				Path path = filePathQueue.poll();
				try {
					DocumentManagerProps props = DocumentManager.manage(path);
					var job = createPrintJob(props);

					this.printJobsQueue.add(job);
				} catch (Exception e) {
					logger.severe(e.getMessage());
				}
			}

		}
	}

	SendDocument createPrintJob(
			@Nonnull DocumentManager.DocumentManagerProps docProps) throws InvalidDocumentException {
		FileDescriptor fileDes = docProps.fileDescriptor();
		try {
			var jobProps = new CreateJob.CreateJobProps(
					String.format("Printing %s", fileDes.getFileName()),
					DocumentFormat.PDF,
					fileDes.getPrintOptions().getSides(),
					fileDes.getPrintOptions().getTray().mediaSource(),
					fileDes.getPrintOptions().getQuantity());

			return this.printer.createJob(jobProps)
					.send()
					.setFile(docProps.file());

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
