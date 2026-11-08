package org.printing_factory.printing.ipp.jobs;

import de.gmuth.ipp.core.IppAttributesGroup;
import de.gmuth.ipp.core.IppResponse;

public interface IppResponsePrinter {

    default String printResponse(IppResponse response) {
        if (response == null) {
            return "Response is null";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("IPP Response:\n");
        sb.append("Status: ").append(response.getStatus()).append("\n");

        for (IppAttributesGroup group : response.getAttributesGroups()) {
            sb.append("Group: ").append(group.getTag()).append("\n");
            group.getEntries().stream().forEach(entry -> {
                sb.append("  ").append(entry.getValue().getName()).append(": ").append(entry.getValue().getValue())
                        .append("\n");
            });
        }

        return sb.toString();
    }
}
