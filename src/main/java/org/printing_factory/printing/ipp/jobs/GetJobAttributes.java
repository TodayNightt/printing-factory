package org.printing_factory.printing.ipp.jobs;

import de.gmuth.ipp.attributes.JobState;
import de.gmuth.ipp.core.IppAttributesGroup;
import de.gmuth.ipp.core.IppRequest;
import de.gmuth.ipp.core.IppResponse;
import de.gmuth.ipp.core.IppOperation;
import de.gmuth.ipp.core.IppTag;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import org.printing_factory.printing.IppConnection;
import org.printing_factory.printing.IppConnection.FailedToConnectException;

public class GetJobAttributes
		extends OperationSender<IppResponse, GetJobAttributes.GetJobAttributesProps> {
	private State latestState;

	protected record GetJobAttributesProps(Integer jobId, String requestingUser, Instant created_at) {
	}

	public GetJobAttributes(IppConnection connection, GetJobAttributes.GetJobAttributesProps props) {
		this.connection = connection;
		this.props = props;
	}

	public Optional<State> getState() {
		Duration elapsed = Duration.between(this.props.created_at(), Instant.now());
		boolean aLongTime = elapsed.compareTo(Duration.ofMinutes(5)) > 0;
		try {
			IppResponse response = send();
			// Check the response
			if (!response.isSuccessful() && aLongTime) {
				this.latestState = State.Timeout;
				return Optional.of(this.latestState);
			}
			if (!response.isSuccessful())
				return Optional.empty();

			JobState state = response.getJobGroup().getValue("job-state");

			if ((state == JobState.Pending || state == JobState.ProcessingStopped)
					&& aLongTime) {
				this.latestState = State.TooLong;
				return Optional.of(this.latestState);
			}

			this.latestState = State.fromJobState(state);

			return Optional.of(this.latestState);

		} catch (FailedToConnectException e) {
			this.latestState = State.Cancel;
			return Optional.of(this.latestState);
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

	public Optional<CancelJob> cancelJob() {
		if (this.latestState == State.Done) {
			return Optional.empty();
		}

		return Optional.of(new CancelJob(this.connection,
				new CancelJob.CancelJobProps(this.props.jobId(), this.props.requestingUser())));
	}

	public Integer getJobId() {
		return this.props.jobId();
	}

	public enum State {
		Cancel, Done, Pending, Timeout, TooLong;

		static State fromJobState(JobState s) {
			return switch (s) {
				case Completed -> Done;
				case Canceled, Aborted, PendingHeld -> Cancel;
				default -> Pending;
			};
		}
	}
}
