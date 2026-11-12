package org.printing_factory;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import org.printing_factory.document.DocumentDescriptor;
import org.printing_factory.document.DocumentManager;

public class JobCreator implements Runnable {
	private static final Logger logger = Logger.getLogger(JobCreator.class.getName());
	private ConcurrentLinkedQueue<Path> filePathQueue;
	private ConcurrentLinkedQueue<DocumentDescriptor> printJobsQueue;

	public JobCreator(ConcurrentLinkedQueue<Path> filePathQueue,
			ConcurrentLinkedQueue<DocumentDescriptor> printJobsQueue) {
		this.filePathQueue = filePathQueue;
		this.printJobsQueue = printJobsQueue;
	}

	@Override
	public void run() {
		while (true) {
			if (!filePathQueue.isEmpty()) {
				Path path = filePathQueue.poll();
				try {
					DocumentDescriptor props = DocumentManager.manage(path);

					this.printJobsQueue.add(props);
				} catch (Exception e) {
					logger.severe(e.getMessage());
				}
			}

		}
	}

}
