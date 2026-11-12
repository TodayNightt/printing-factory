package org.printing_factory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.printing_factory.document.DocumentDescriptor;
import org.printing_factory.printing.JobDescriptor;
import org.printing_factory.printing.Printer;
import org.printing_factory.printing.ipp.jobs.CreateJob;
import org.printing_factory.printing.ipp.jobs.GetJobAttributes;
import org.printing_factory.printing.ipp.jobs.SendDocument;

public class JobSender implements Runnable {
	private static final Logger logger = Logger.getLogger(JobSender.class.getName());
	private final ConcurrentLinkedQueue<DocumentDescriptor> jobQueue;
	private Printer printer;

	private final CopyOnWriteArrayList<JobDescriptor> jobSent;

	public JobSender(ConcurrentLinkedQueue<DocumentDescriptor> jobQueue,
			CopyOnWriteArrayList<JobDescriptor> jobSent,
			Printer printer) {
		this.jobQueue = jobQueue;
		this.jobSent = jobSent;
		this.printer = printer;
	}

	@Override
	public void run() {
		while (true) {
			if (!jobQueue.isEmpty()) {
				DocumentDescriptor docDesc = jobQueue.peek();

				try {
					// create job
					CreateJob job = this.printer.createJob(docDesc.fileDescriptor());
					SendDocument documentSender = job.send();
					// send document
					documentSender.setFile(docDesc.file());
					GetJobAttributes sent = documentSender.send();

					// remove document from queue
					jobQueue.poll();

					// add to tracker
					jobSent.add(new JobDescriptor(sent, docDesc));

				} catch (Exception e) {
					// Log the error
					logger.log(Level.SEVERE, "Failed to send job", e);

					// Optionally, retry the job or handle it in a specific way
					// For example, you could move the job to a retry queue or mark it as failed
					// retryQueue.add(job);
					// or
					// failedJobs.add(job);

					// Remove the job from the queue to avoid infinite retry loop
					jobQueue.poll();
				}

			}
		}
	}
}
