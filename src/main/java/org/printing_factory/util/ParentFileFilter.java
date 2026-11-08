package org.printing_factory.util;

import java.io.File;

import org.apache.commons.io.filefilter.AbstractFileFilter;

public class ParentFileFilter extends AbstractFileFilter {
    private final String excludedDirName;

    public ParentFileFilter(String excludedDirName) {
        this.excludedDirName = excludedDirName;
    }

    @Override
    public boolean accept(File file) {
        // Exclude files from the #recycle directory but include all other files
        if (file.isDirectory() && file.getName().contains(excludedDirName)) {
            return false;
        }
        return true; // Accept all other files and directories
    }
}