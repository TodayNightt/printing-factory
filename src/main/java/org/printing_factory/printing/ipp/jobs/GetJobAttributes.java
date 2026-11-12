package org.printing_factory.printing.ipp.jobs;

import de.gmuth.ipp.attributes.JobState;
import de.gmuth.ipp.core.IppAttributesGroup;
import de.gmuth.ipp.core.IppRequest;
import de.gmuth.ipp.core.IppResponse;
import de.gmuth.ipp.core.IppOperation;
import de.gmuth.ipp.core.IppTag;

import java.util.Arrays;
import java.util.Optional;

import org.printing_factory.printing.IppConnection;
import org.printing_factory.printing.IppConnection.FailedToConnectException;

public class GetJobAttributes
		extends OperationSender<IppResponse, GetJobAttributes.GetJobAttributesProps> {

	protected record GetJobAttributesProps(Integer jobId, String requestingUser) {
	}

	public GetJobAttributes(IppConnection connection, GetJobAttributes.GetJobAttributesProps props) {
		this.connection = connection;
		this.props = props;
	}

	public Optional<State> getState() {
		try {
			IppResponse response = send();
			// Check the response
			if (!response.isSuccessful())
				return Optional.empty();

			System.out.println("Job attributes retrieved successfully");
			JobState state = response.getJobGroup().getValue("job-state");

			return Optional.of(State.fromJobState(state));
		} catch (FailedToConnectException e) {
			return Optional.of(State.Cancel);
		}
	}

	@Override
	public IppResponse send() throws FailedToConnectException {
		IppRequest request = generateRequest();
		return connection.sendRequest(request);
	}

	@Override
	protected IppRequest generateRequest() {
		IppRequest request = connection.createRequest(IppOperation.GetJobAttributes,
				Arrays.asList("job-state", "job-state-reasons"));
		// Operation group
		IppAttributesGroup operationGroup = request.getOperationGroup();
		operationGroup.attribute("requesting-name", IppTag.NameWithoutLanguage,
				props.requestingUser);
		
		return request;
	}

	public enum State {
		Cancel, Done, Pending;

		static State fromJobState(JobState s) {
			return switch (s) {
				case Completed -> Done;
				case Canceled, Aborted -> Cancel;
				default -> Pending;
			};
		}
	}
}
