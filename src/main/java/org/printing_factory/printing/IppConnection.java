package org.printing_factory.printing;

import java.net.URI;
import java.util.List;

import javax.annotation.Nullable;

import de.gmuth.ipp.client.IppClient;
import de.gmuth.ipp.client.IppConfig;
import de.gmuth.ipp.core.IppAttributesGroup;
import de.gmuth.ipp.core.IppOperation;
import de.gmuth.ipp.core.IppRequest;
import de.gmuth.ipp.core.IppResponse;

/**
 * This class is responsible for establishing a connection with the printer and
 * send whatever requests are needed
 * This is more robust than {@link de.gmuth.ipp.client.IppPrinter}
 * class when the
 * connection cannot be
 * established it will throw an exception
 * and the program will stop. This class will keep trying to establish a
 * connection with the printer`
 */
public class IppConnection {
    private URI address;
    protected IppClient connection;
    protected IppAttributesGroup printerAttributes;

    protected IppConfig config;

    public IppAttributesGroup getPrinterAttributes() {
        return printerAttributes;
    }

    public String getUserName() {
        return config.getUserName();
    }

    public String getIppVersion() {
        return config.getIppVersion();
    }

    public IppConnection(URI address) {
        this.address = address;
        this.config = new IppConfig();
        config.trustAnyCertificateAndSSLHostname();
        this.connection = new IppClient(config);

        var req = connection.ippRequest(IppOperation.GetPrinterAttributes, address, null, null);
        IppResponse res;
        try {
            res = sendRequest(req);

            printerAttributes = res.getPrinterGroup();
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to printer");
        }
    }

    public IppResponse sendRequest(IppRequest request) throws Exception {
        // try establish the connection then send the request
        boolean isConnected = false;
        while (!isConnected) {
            var req = connection.ippRequest(IppOperation.IdentifyPrinter, address, null, null);
            try {
                connection.exchange(req);
                isConnected = true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("Failed to connect to printer, retrying in 5 seconds");
                Thread.sleep(5000);
            }
        }

        return connection.exchange(request);
    }

    public IppRequest createRequest(IppOperation operation, @Nullable List<String> requestingAttr) {
        return connection.ippRequest(operation, address, requestingAttr, config.getUserName());
    }

}
