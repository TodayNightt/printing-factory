package org.printing_factory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.printing_factory.printing.ipp.jobs.GetJobAttributes;
import org.printing_factory.printing.ipp.jobs.SendDocument;

public class JobsSender implements Runnable {
    private static final Logger logger = Logger.getLogger(JobsSender.class.getName());
    private final ConcurrentLinkedQueue<SendDocument> jobQueue;

    private final CopyOnWriteArrayList<GetJobAttributes> jobSent;

    public JobsSender(ConcurrentLinkedQueue<SendDocument> jobQueue, CopyOnWriteArrayList<GetJobAttributes> jobSent) {
        this.jobQueue = jobQueue;
        this.jobSent = jobSent;
    }

    @Override
    public void run() {
        while (true) {
            if (!jobQueue.isEmpty()) {
                SendDocument job = jobQueue.peek();

                try {
                    GetJobAttributes sent = job.send();

                    jobQueue.poll();

                    jobSent.add(sent);
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
        // send the job to the printer
        // jobHolder.getJob().send(jobHolder.getFile());
    }

}
