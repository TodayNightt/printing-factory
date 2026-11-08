package org.printing_factory;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.printing_factory.printing.ipp.jobs.GetJobAttributes;

public class StatusChecker implements Runnable {
    private static final Logger logger = Logger.getLogger(StatusChecker.class.getName());
    private final CopyOnWriteArrayList<GetJobAttributes> jobSent;

    public StatusChecker(CopyOnWriteArrayList<GetJobAttributes> jobSent) {
        this.jobSent = jobSent;
    }

    @Override
    public void run() {
        while (true) {
            for (GetJobAttributes job : jobSent) {
                try {
                    if (job.isCompleted()) {
                        logger.info("Job completed: " + job);
                        jobSent.remove(job);
                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Failed to check job status", e);
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
