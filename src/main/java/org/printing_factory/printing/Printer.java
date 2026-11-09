package org.printing_factory.printing;

import java.net.URI;

import org.printing_factory.printing.ipp.jobs.CreateJob;

public class Printer {
	private IppConnection connection;

	public Printer(URI printerUri) {
		this.connection = new IppConnection(printerUri);
	}

	public CreateJob createJob(CreateJob.CreateJobProps props) {
		return new CreateJob(connection, props);
	}
}
