package org.printing_factory;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.file.Path;
import java.util.function.Consumer;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.io.monitor.FileEntry;
import org.printing_factory.util.Logging;
import org.printing_factory.util.ParentFileFilter;

public class WatchFolder {

    WatchFolder(Path directory, Consumer<Path> eventHandler) throws IOException {

        FileAlterationObserver observer = FileAlterationObserver.builder()
                .setRootEntry(new FileEntry(directory.toFile()))
                .setFileFilter(
                        new ParentFileFilter("#recycle"))
                .get();

        observer.addListener(new FileAlterationListenerAdaptor() {
            @Override
            public void onFileCreate(File file) {
                eventHandler.accept(file.toPath());
            }

            @Override
            public void onFileChange(File file) {
                System.out.println("File modified: " + file.getName());
            }

            @Override
            public void onFileDelete(File file) {
                System.out.println("File deleted: " + file.getName());
            }
        });

        FileAlterationMonitor monitor = new FileAlterationMonitor(5000); // Poll every 5 seconds
        monitor.addObserver(observer);

        var t = Thread.startVirtualThread(monitor);

        t.setUncaughtExceptionHandler(new ExceptionCapturer());

    }

    class ExceptionCapturer implements UncaughtExceptionHandler, Logging {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            logger.severe(e.getMessage());
        }

    }

}
