package org.printing_factory.printing.ipp.jobs;

import org.printing_factory.printing.IppConnection;
import org.printing_factory.printing.IppConnection.FailedToConnectException;
import org.printing_factory.printing.ipp.jobs.GetJobAttributes.GetJobAttributesProps;

import de.gmuth.ipp.core.IppAttributesGroup;
import de.gmuth.ipp.core.IppRequest;
import de.gmuth.ipp.core.IppResponse;
import de.gmuth.ipp.core.IppTag;
import de.gmuth.ipp.core.IppOperation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class SendDocument
		extends OperationSender<GetJobAttributes, SendDocument.SendDocumentProps> {
	protected File file;

	protected record SendDocumentProps(Integer jobId, String requestingUser) {
	}

	public SendDocument(IppConnection connection, SendDocumentProps props) {
		this.connection = connection;
		this.props = props;
	}

	public SendDocument setFile(File file) {
		this.file = file;
		return this;
	}

	@Override
	public GetJobAttributes send() throws IOException, FailedToConnectException {
		try (InputStream stream = Files.newInputStream(this.file.toPath(),
				StandardOpenOption.READ)) {
			IppRequest request = generateRequest();
			request.setDocumentInputStream(stream);

			IppResponse res = connection.sendRequest(request);

			if (res.isSuccessful()) {
				System.out.println("Document sent successfully");
			} else {
				System.out.println("Failed to send document");
			}

			return new GetJobAttributes(connection, new GetJobAttributesProps(props.jobId, props.requestingUser));
		} catch (IOException e) {
			throw e;
		}
	}

	@Override
	protected IppRequest generateRequest() {
		IppRequest req = connection.createRequest(IppOperation.SendDocument, null);

		// Operation group
		IppAttributesGroup operationGroup = req.getOperationGroup();
		operationGroup.attribute("job-id", IppTag.Integer, props.jobId);
		operationGroup.attribute("requesting-user-name", IppTag.NameWithoutLanguage,
				props.requestingUser);
		operationGroup.attribute("last-document", IppTag.Boolean, true);

		return req;
	}
}
