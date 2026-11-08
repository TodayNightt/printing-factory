package org.printing_factory.docu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeUtils;
import org.jodconverter.local.JodConverter;
import org.jodconverter.local.office.LocalOfficeManager;

public class DocumentManager {
    public record DocumentManagerProps(DocFormat docFormat, InputStream fileStream) {
    };

    public static DocumentManagerProps manage(File file) throws OfficeException {
        final LocalOfficeManager officeManager = LocalOfficeManager.install();
        String fileName = file.getName();
        String extension = fileName.substring(fileName.indexOf(".") + 1);

        // Get the file extension for the file
        DocFormat docFormat = getFormat(extension);

        // If the file is a docx
        // Change its background color to white

        // Finally onvert it to pdf
        InputStream inputStream = convertToPdf(officeManager, file);

        return new DocumentManagerProps(docFormat, inputStream);
    }

    private static DocFormat getFormat(String extension) {
        return switch (extension) {
            case "docx", "doc" -> DocFormat.PDF;
            case "tiff" -> DocFormat.TIFF;
            case "jpg", "jpeg" -> DocFormat.JPG;
            case "png" -> DocFormat.PNG;
            default -> DocFormat.PDF;
        };
    }

    private static InputStream convertToPdf(LocalOfficeManager officeManager, File file) throws OfficeException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            officeManager.start();

            JodConverter.convert(file).to(outputStream).as(DefaultDocumentFormatRegistry.PDF);
        } finally {
            OfficeUtils.stopQuietly(officeManager);
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

}
