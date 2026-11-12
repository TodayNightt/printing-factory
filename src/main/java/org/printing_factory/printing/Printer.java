package org.printing_factory.printing;

import java.net.URI;

import org.printing_factory.file.FileDescriptor;
import org.printing_factory.printing.ipp.jobs.CreateJob;

public class Printer {
	private IppConnection connection;

	public Printer(String printerUri) {
		this.connection = new IppConnection(URI.create(printerUri));
	}

	public CreateJob createJob(FileDescriptor fileDescriptor) {
		return new CreateJob(this.connection, fileDescriptor);
	}
}
