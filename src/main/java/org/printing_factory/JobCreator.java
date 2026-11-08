package org.printing_factory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

import org.printing_factory.printing.PathParser;
import org.printing_factory.printing.ipp.jobs.SendDocument;

public class JobCreator implements Runnable {
    private ConcurrentLinkedQueue<PathParser> filePathQueue;
    private ConcurrentLinkedQueue<SendDocument> printJobsQueue;
    private Function<PathParser, SendDocument> handler;

    public JobCreator(ConcurrentLinkedQueue<PathParser> filePathQueue,
            ConcurrentLinkedQueue<SendDocument> printJobsQueue,
            Function<PathParser, SendDocument> handler) {
        this.filePathQueue = filePathQueue;
        this.printJobsQueue = printJobsQueue;
        this.handler = handler;
    }

    @Override
    public void run() {
        while (true) {
            if (!filePathQueue.isEmpty()) {
                var job = this.handler.apply(filePathQueue.poll());

                printJobsQueue.add(job);
            }
        }
    }

}
