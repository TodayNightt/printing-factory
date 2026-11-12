package org.printing_factory.document;

import java.io.File;
import java.io.IOException;

import org.printing_factory.file.FileDescriptor;

public record DocumentDescriptor(FileDescriptor fileDescriptor, boolean isTempFile, File file) {
	public void cleanup() throws IOException {
		if (isTempFile) {
			this.file.delete();
		}
	}
};
