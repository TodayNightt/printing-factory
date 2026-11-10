package org.printing_factory.file;

import java.io.File;
import java.nio.file.Path;

import org.printing_factory.printing.PathParser;

/**
 * FileDesciptor
 */
public class FileDescriptor {

	private File file;
	private FileType type;
	private PathParser printOptions;

	public FileDescriptor(Path filePath) {
		this.file = filePath.toFile();

		String fileName = file.getName();
		String extension = fileName.substring(fileName.indexOf('.') + 1);

		this.type = switch (extension) {
			case "docx", "doc" -> FileType.DOCX;
			case "tiff" -> FileType.TIFF;
			case "jpg", "jpeg" -> FileType.JPG;
			case "png" -> FileType.PNG;
			case "pdf" -> FileType.PDF;
			default -> FileType.NOT_SUPPORTED;
		};

		this.printOptions = new PathParser(fileName);
	}

	public boolean is_not_supported() {
		return this.type == FileType.NOT_SUPPORTED;
	}

	public File getFile() {
		return this.file;
	}

	public String getFileName() {
		return this.file.getAbsolutePath();
	}

	public PathParser getPrintOptions() {
		return this.printOptions;
	}

	public FileType getType() {
		return this.type;
	}

	public enum FileType {
		PDF, TIFF, PNG, JPG, DOCX, NOT_SUPPORTED;
	}

	class PrintOptions {
	}
}
