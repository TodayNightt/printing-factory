package org.printing_factory.printing;

import java.io.InputStream;

import org.printing_factory.printing.ipp.jobs.SendDocument;

public class JobHolder {
    private SendDocument job;

    public SendDocument getJob() {
        return job;
    }

    private InputStream file;

    public InputStream getFile() {
        return file;
    }

    public JobHolder(SendDocument job, InputStream file) {
        this.job = job;
        this.file = file;
    }
}
