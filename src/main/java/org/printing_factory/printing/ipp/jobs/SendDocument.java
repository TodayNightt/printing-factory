package org.printing_factory.printing.ipp.jobs;

import org.printing_factory.printing.IppConnection;
import org.printing_factory.printing.ipp.jobs.GetJobAttributes.GetJobAttributesProps;

import de.gmuth.ipp.core.IppAttributesGroup;
import de.gmuth.ipp.core.IppRequest;
import de.gmuth.ipp.core.IppResponse;
import de.gmuth.ipp.core.IppTag;
import de.gmuth.ipp.core.IppOperation;

import java.io.ByteArrayInputStream;

public class SendDocument extends OperationSender<GetJobAttributes, SendDocument.SendDocumentProps> {
    protected ByteArrayInputStream fileSrc;
    protected SendDocumentProps props;

    protected record SendDocumentProps(Integer jobId, String requestingUser) {
    }

    public SendDocument(IppConnection connection, SendDocumentProps props) {
        this.connection = connection;
        this.props = props;
    }

    public SendDocument setFile(ByteArrayInputStream fileSrc) {
        this.fileSrc = fileSrc;
        return this;
    }

    @Override
    public GetJobAttributes send() throws Exception {
        this.request = generateRequest(props);
        IppResponse res = connection.sendRequest(request);

        if (res.isSuccessful()) {
            System.out.println("Document sent successfully");
        } else {
            System.out.println("Failed to send document");
        }

        return new GetJobAttributes(connection, new GetJobAttributesProps(props.jobId, props.requestingUser));
    }

    @Override
    protected IppRequest generateRequest(SendDocumentProps props) {
        IppRequest req = connection.createRequest(IppOperation.SendDocument, null);

        // Operation group
        IppAttributesGroup operationGroup = req.getOperationGroup();
        operationGroup.attribute("job-id", IppTag.Integer, props.jobId);
        operationGroup.attribute("requesting-user-name", IppTag.NameWithoutLanguage, props.requestingUser);
        operationGroup.attribute("last-document", IppTag.Boolean, true);

        req.setDocumentInputStream(fileSrc);

        return req;
    }
}
