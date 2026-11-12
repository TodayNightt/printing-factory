package org.printing_factory.printing.ipp.jobs;

import org.printing_factory.printing.IppConnection;
import org.printing_factory.printing.IppConnection.FailedToConnectException;

import de.gmuth.ipp.core.IppAttributesGroup;
import de.gmuth.ipp.core.IppOperation;
import de.gmuth.ipp.core.IppRequest;
import de.gmuth.ipp.core.IppTag;

/**
 * CancelJob
 */
public class CancelJob extends OperationSender<Void, CancelJob.CancelJobProps> {

	protected record CancelJobProps(Integer jobId,
			String requestingUser) {
	}

	public CancelJob(IppConnection connection, CancelJobProps props) {
		this.connection = connection;
		this.props = props;
	}

	@Override
	public Void send() throws FailedToConnectException {

		IppRequest request = this.generateRequest();

		connection.sendRequest(request);

		return null;
	}

	@Override
	protected IppRequest generateRequest() {
		IppRequest req = connection.createRequest(IppOperation.CancelJob, null);

		IppAttributesGroup operationGroup = req.getOperationGroup();
		operationGroup.attribute("job-id", IppTag.Integer, props.jobId);
		operationGroup.attribute("requesting-user-name", IppTag.NameWithoutLanguage,
				props.requestingUser);

		return req;
	}

}
