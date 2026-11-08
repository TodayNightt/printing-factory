package org.printing_factory.printing.ipp.jobs;

import de.gmuth.ipp.attributes.JobState;
import de.gmuth.ipp.core.IppAttributesGroup;
import de.gmuth.ipp.core.IppRequest;
import de.gmuth.ipp.core.IppResponse;
import de.gmuth.ipp.core.IppOperation;
import de.gmuth.ipp.core.IppTag;

import java.util.Arrays;
import org.printing_factory.printing.IppConnection;

public class GetJobAttributes extends OperationSender<Void, GetJobAttributes.GetJobAttributesProps> {
    private IppResponse response;

    protected record GetJobAttributesProps(Integer jobId, String requestingUser) {
    }

    public GetJobAttributes(IppConnection connection, GetJobAttributes.GetJobAttributesProps props) throws Exception {
        this.connection = connection;
        this.request = generateRequest(props);

        send();
    }

    public boolean isCompleted() throws Exception {
        // Check the response
        if (response.isSuccessful()) {
            System.out.println("Job attributes retrieved successfully");
            JobState state = response.getJobGroup().getValue("job-state");

            return state == JobState.Completed;
        } else {
            // send();
            // return isCompleted();
            return false;
        }
    }

    @Override
    public Void send() throws Exception {
        response = connection.sendRequest(request);
        return null;
    }

    @Override
    protected IppRequest generateRequest(GetJobAttributesProps props) {
        IppRequest request = connection.createRequest(IppOperation.GetJobAttributes,
                Arrays.asList("job-state", "job-state-reasons"));
        // Operation group
        IppAttributesGroup operationGroup = request.getOperationGroup();
        operationGroup.attribute("requesting-name", IppTag.NameWithoutLanguage, props.requestingUser);
        operationGroup.attribute("job-id", IppTag.Integer, props.jobId);

        return request;
    }
}