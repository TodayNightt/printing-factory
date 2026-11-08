package org.printing_factory.printing;

import org.printing_factory.printing.ipp.jobs.CreateJob;

public class Printer {
    private IppConnection connection;

    public Printer(IppConnection connection) {
        this.connection = connection;
    }

    public CreateJob createJob(CreateJob.CreateJobProps props) {
        return new CreateJob(connection, props);
    }
}
