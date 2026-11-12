package org.printing_factory.document;

import java.io.File;

import org.printing_factory.file.FileDescriptor;

public record DocumentDescriptor(FileDescriptor fileDescriptor, File file) {
};
