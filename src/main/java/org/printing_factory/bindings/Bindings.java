package org.printing_factory.bindings;

import org.printing_factory.docu.DocFormat;

import de.gmuth.ipp.attributes.DocumentFormat;

public class Bindings {
    public static DocumentFormat into(DocFormat docFormat) {
        return switch (docFormat) {
            case PDF -> DocumentFormat.PDF;
            case TIFF -> DocumentFormat.TIFF;
            case PNG -> DocumentFormat.PNG;
            case JPG -> DocumentFormat.JPEG;
            default -> DocumentFormat.PDF;
        };
    }
}
