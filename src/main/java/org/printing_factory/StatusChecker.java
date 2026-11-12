package org.printing_factory;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.printing_factory.printing.JobDescriptor;
import org.printing_factory.printing.ipp.jobs.GetJobAttributes;

public class StatusChecker implements Runnable {
	private static final Logger logger = Logger.getLogger(StatusChecker.class.getName());
	private final CopyOnWriteArrayList<JobDescriptor> jobSent;

	public StatusChecker(CopyOnWriteArrayList<JobDescriptor> jobSent) {
		this.jobSent = jobSent;
	}

	@Override
	public void run() {
		while (true) {
			for (JobDescriptor jobDesc : jobSent) {
				GetJobAttributes job = jobDesc.job();
				switch (job.getState()) {
					case Optional<GetJobAttributes.State> opts when opts.isPresent():

						break;
					default:
						break;
				}
			}

			try {
				Thread.sleep(5000); // Check status every 5 seconds
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				logger.log(Level.SEVERE, "StatusChecker interrupted", e);
			}
		}
	}
}
