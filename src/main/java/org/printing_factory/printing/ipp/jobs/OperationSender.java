package org.printing_factory.printing.ipp.jobs;

import org.printing_factory.printing.IppConnection;

import de.gmuth.ipp.core.IppRequest;

public abstract class OperationSender<Ret, ReqProps> {
    protected IppConnection connection;
    protected IppRequest request;

    public abstract Ret send() throws Exception;

    protected abstract IppRequest generateRequest(ReqProps props);
}
