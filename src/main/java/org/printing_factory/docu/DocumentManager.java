package org.printing_factory.docu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeUtils;
import org.jodconverter.local.JodConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.printing_factory.file.FileDescriptor;

public class DocumentManager {
	public record DocumentManagerProps(FileDescriptor fileDescriptor, File file) {
	};

	public static DocumentManagerProps manage(Path filePath)
			throws NotSupportedFileException, RetrieveFileHandleException, FileConvertionException {
		FileDescriptor fileDescriptor = new FileDescriptor(filePath);
		if (fileDescriptor.is_not_supported()) {
			throw new NotSupportedFileException(fileDescriptor.getFileName());
		}

		final LocalOfficeManager officeManager = LocalOfficeManager.install();

		File file = switch (fileDescriptor.getType()) {
			case DOCX -> {
				try (InputStream inputStream = convertToPdf(officeManager, fileDescriptor.getFile())) {
					Path temp = Files.createTempFile("tmp_" + fileDescriptor.getFileName(), ".pdf");

					Files.copy(inputStream, temp, StandardCopyOption.REPLACE_EXISTING);

					yield temp.toFile();
				} catch (OfficeException e) {
					throw new FileConvertionException(fileDescriptor.getFileName(), e.getMessage());
				} catch (IOException e) {
					throw new RetrieveFileHandleException(fileDescriptor.getFileName(), e.getMessage());
				}
			}
			default -> fileDescriptor.getFile();
		};

		return new DocumentManagerProps(fileDescriptor, file);
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

class NotSupportedFileException extends Exception {
	NotSupportedFileException(String filePath) {
		super("Not supported file format for :" + filePath);
	}
}

class RetrieveFileHandleException extends Exception {
	RetrieveFileHandleException(String filePath, String message) {
		super("Something went wrong for file : " + filePath + "\nmessage : " + message);
	}
}

class FileConvertionException extends Exception {
	FileConvertionException(String filePath, String message) {
		super("Failed to convert file : " + filePath + "\n message : " + message);
	}
}
