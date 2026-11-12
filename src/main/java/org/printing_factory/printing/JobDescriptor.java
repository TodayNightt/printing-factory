package org.printing_factory.printing;

import java.io.IOException;
import java.util.Optional;

import org.printing_factory.document.DocumentDescriptor;
import org.printing_factory.printing.IppConnection.FailedToConnectException;
import org.printing_factory.printing.ipp.jobs.CancelJob;
import org.printing_factory.printing.ipp.jobs.GetJobAttributes;

/**
 * JobDescriptor
 */
public record JobDescriptor(GetJobAttributes job, DocumentDescriptor documentDescriptor) {

	public void cleanup() throws IOException, FailedToCancelJobException {
		this.documentDescriptor.cleanup();
		Optional<CancelJob> cancelation = this.job.cancelJob();

		if (cancelation.isPresent()) {
			try {
				cancelation.get().send();
			} catch (FailedToConnectException e) {
				throw new FailedToCancelJobException(this.job.getJobId());
			}
		}

	}

	public class FailedToCancelJobException extends Exception {
		FailedToCancelJobException(Integer jobId) {
			super("failed to cancel a job jobId :" + jobId);
		}
	}
}
