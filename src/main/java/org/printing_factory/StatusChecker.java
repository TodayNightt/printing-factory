package org.printing_factory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.printing_factory.printing.JobDescriptor;
import org.printing_factory.printing.ipp.jobs.GetJobAttributes;

public class StatusChecker implements Runnable {
	private static final Logger logger = Logger.getLogger(StatusChecker.class.getName());
	private final ConcurrentLinkedQueue<JobDescriptor> jobSent;

	public StatusChecker(ConcurrentLinkedQueue<JobDescriptor> jobSent) {
		this.jobSent = jobSent;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			Iterator<JobDescriptor> iterator = jobSent.iterator();

			while (iterator.hasNext()) {
				JobDescriptor jobDesc = iterator.next();
				GetJobAttributes job = jobDesc.job();
				Optional<GetJobAttributes.State> state = job.getState();

				if (state.isEmpty()) {
					continue;
				}

				switch (state.get()) {
					case Done:
						logger.log(Level.INFO, "Print job done\nfile: "
								+ jobDesc.documentDescriptor().fileDescriptor().getFileName());
						cleanup(jobDesc);
						jobSent.remove(jobDesc);
						break;
					case Timeout:
						logger.log(Level.WARNING, "Print job timeout\nfile: "
								+ jobDesc.documentDescriptor().fileDescriptor().getFileName());
						cleanup(jobDesc);

						jobSent.remove(jobDesc);
						break;
					case Cancel:
						logger.log(Level.WARNING, "Print job cancelled\nfile: "
								+ jobDesc.documentDescriptor().fileDescriptor().getFileName());
						cleanup(jobDesc);

						jobSent.remove(jobDesc);
						break;
					case TooLong:
						logger.log(Level.WARNING, "Print job taking too long... Cancelling\nfile: "
								+ jobDesc.documentDescriptor().fileDescriptor().getFileName());
						cleanup(jobDesc);

						jobSent.remove(jobDesc);
						break;
					default:
						continue;
				}
			}

			try {
				Thread.sleep(5000); // Check status every 5 seconds
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private void cleanup(JobDescriptor jobDescriptor) {
		try {
			jobDescriptor.cleanup();
		} catch (IOException e) {
			logger.severe(
					"failed to cleanup file : " + jobDescriptor.documentDescriptor().fileDescriptor().getFileName());
		} catch (Exception e) {
			logger.severe(e.getMessage());
		}
	}
}
