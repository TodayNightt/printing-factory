package org.printing_factory.printing;

import org.printing_factory.document.DocumentDescriptor;
import org.printing_factory.printing.ipp.jobs.GetJobAttributes;

/**
 * JobDescriptor
 */
public record JobDescriptor(GetJobAttributes job, DocumentDescriptor documentDescriptor) {
}
