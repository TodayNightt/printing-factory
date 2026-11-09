package org.printing_factory.docu;

import de.gmuth.ipp.attributes.DocumentFormat;

public enum DocFormat {
	PDF, TIFF, PNG, JPG;

	public static DocumentFormat toDocumentFormat(DocFormat format) {
		return switch (format) {
			case PDF -> DocumentFormat.PDF;
			case TIFF -> DocumentFormat.TIFF;
			case PNG -> DocumentFormat.PNG;
			case JPG -> DocumentFormat.JPEG;
			default -> DocumentFormat.PDF;
		};
	}
}
