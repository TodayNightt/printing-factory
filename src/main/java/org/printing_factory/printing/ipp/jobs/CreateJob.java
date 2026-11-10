package org.printing_factory.printing.ipp.jobs;

import javax.annotation.Nullable;
import org.printing_factory.printing.IppConnection;
import org.printing_factory.printing.ipp.jobs.SendDocument.SendDocumentProps;

import de.gmuth.ipp.attributes.DocumentFormat;
import de.gmuth.ipp.attributes.MediaSource;
import de.gmuth.ipp.attributes.Sides;
import de.gmuth.ipp.core.IppAttributesGroup;
import de.gmuth.ipp.core.IppCollection;
import de.gmuth.ipp.core.IppRequest;
import de.gmuth.ipp.core.IppResponse;
import de.gmuth.ipp.core.IppTag;
import de.gmuth.ipp.core.IppOperation;

public class CreateJob extends OperationSender<SendDocument, CreateJob.CreateJobProps> implements IppResponsePrinter {
	public record CreateJobProps(String jobName, DocumentFormat documentFormat, Sides sides, MediaSource mediaSource,
			@Nullable Integer copies) {
	}

	public CreateJob(IppConnection connection, CreateJob.CreateJobProps props) {
		this.connection = connection;
	}

	@Override
	public SendDocument send() throws Exception {
		IppRequest request = generateRequest(props);
		IppResponse res = connection.sendRequest(request);

		System.out.println(
				"SendDocument send() for jobName :" + request.getOperationGroup().getValueAsString("job-name"));
		printResponse(res);

		// Check the response
		if (res.isSuccessful()) {
			System.out.println("Job created successfully jobId: " + res.getJobGroup().getValue("job-id"));
		} else {
			System.out.println("Failed to create job");
		}

		return new SendDocument(connection, new SendDocumentProps(res.getJobGroup().getValue("job-id"), null));
	}

	@Override
	public IppRequest generateRequest(CreateJobProps props) {
		IppRequest req = connection.createRequest(IppOperation.CreateJob, null);

		// operation group
		var operationGroup = req.getOperationGroup();
		operationGroup.attribute("requesting-user-name", IppTag.NameWithoutLanguage, connection.getUserName());
		operationGroup.attribute("job-name", IppTag.NameWithoutLanguage, props.jobName);

		// Job group
		IppAttributesGroup jobGroup = req.createAttributesGroup(IppTag.Job);
		var sidesAttr = props.sides.buildIppAttribute(connection.getPrinterAttributes());

		var mediaColAttr = new IppCollection();
		mediaColAttr
				.add(props.mediaSource.buildIppAttribute(connection.getPrinterAttributes()));
		jobGroup.attribute("copies", IppTag.Integer,
				props.copies == null ? 1 : props.copies);
		jobGroup.put(sidesAttr);
		jobGroup.attribute("media-col", IppTag.BegCollection, mediaColAttr);

		return req;
	}

}
