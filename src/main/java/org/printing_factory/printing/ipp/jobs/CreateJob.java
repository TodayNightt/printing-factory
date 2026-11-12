package org.printing_factory.printing.ipp.jobs;

import org.printing_factory.file.FileDescriptor;
import org.printing_factory.printing.IppConnection;
import org.printing_factory.printing.IppConnection.FailedToConnectException;
import org.printing_factory.printing.PathParser;
import org.printing_factory.printing.ipp.jobs.SendDocument.SendDocumentProps;

import de.gmuth.ipp.core.IppAttributesGroup;
import de.gmuth.ipp.core.IppCollection;
import de.gmuth.ipp.core.IppRequest;
import de.gmuth.ipp.core.IppResponse;
import de.gmuth.ipp.core.IppTag;
import de.gmuth.ipp.core.IppOperation;

public class CreateJob extends OperationSender<SendDocument, FileDescriptor> implements IppResponsePrinter {

	public record CreateJobProps(FileDescriptor fileDescriptor) {
	}

	public CreateJob(IppConnection connection, FileDescriptor props) {
		this.connection = connection;
		this.props = props;
	}

	@Override
	public SendDocument send() throws FailedToConnectException {
		IppRequest request = generateRequest();
		IppResponse res = this.connection.sendRequest(request);

		System.out.println(
				"SendDocument send() for jobName :" +
						request.getOperationGroup().getValueAsString("job-name"));
		printResponse(res);

		// Check the response
		if (res.isSuccessful()) {
			System.out.println("Job created successfully jobId: " +
					res.getJobGroup().getValue("job-id"));
		} else {
			System.out.println("Failed to create job");
		}

		return new SendDocument(this.connection, new SendDocumentProps(res.getJobGroup().getValue("job-id"), null));
	}

	@Override
	public IppRequest generateRequest() {
		IppRequest req = connection.createRequest(IppOperation.CreateJob, null);

		// operation group
		var operationGroup = req.getOperationGroup();
		operationGroup.attribute("requesting-user-name", IppTag.NameWithoutLanguage,
				connection.getUserName());
		operationGroup.attribute("job-name", IppTag.NameWithoutLanguage,
				props.getFileName());

		PathParser printOptions = props.getPrintOptions();

		// Job group
		IppAttributesGroup jobGroup = req.createAttributesGroup(IppTag.Job);
		var sidesAttr = printOptions.getSides().buildIppAttribute(connection.getPrinterAttributes());

		var mediaColAttr = new IppCollection();
		mediaColAttr
				.add(printOptions.getTray().mediaSource().buildIppAttribute(connection.getPrinterAttributes()));

		jobGroup.attribute("copies", IppTag.Integer,
				printOptions.getQuantity());
		jobGroup.put(sidesAttr);
		jobGroup.attribute("media-col", IppTag.BegCollection, mediaColAttr);

		return req;
	}

}
