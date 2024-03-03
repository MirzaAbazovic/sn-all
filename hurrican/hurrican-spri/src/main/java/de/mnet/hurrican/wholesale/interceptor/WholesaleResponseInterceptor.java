package de.mnet.hurrican.wholesale.interceptor;

/**
 * Created by vergaragi on 06.02.2017.
 */

import java.io.*;
import java.util.logging.*;
import javax.xml.parsers.*;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.AbstractLoggingInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedWriter;
import org.apache.cxf.io.DelegatingInputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.InterfaceInfo;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

/**
 * Interceptor for storing incoming XML in WholesaleAudit
 */
@Component
public class WholesaleResponseInterceptor extends AbstractLoggingInterceptor {

    public static final String REQUEST_XML = "REQUEST_XML";
    private static final Logger LOG = LogUtils.getLogger(WholesaleResponseInterceptor.class);

    public WholesaleResponseInterceptor() {
        super(Phase.RECEIVE);
        setPrettyLogging(true);
    }


    public WholesaleResponseInterceptor(String phase) {
        super(phase);
    }

    public WholesaleResponseInterceptor(String id, String phase) {
        super(id, phase);
    }

    public WholesaleResponseInterceptor(int lim) {
        this();
        this.limit = lim;
    }

    public WholesaleResponseInterceptor(String id, int lim) {
        this(id, "receive");
        this.limit = lim;
    }

    public WholesaleResponseInterceptor(PrintWriter w) {
        this();
        this.writer = w;
    }

    public WholesaleResponseInterceptor(String id, PrintWriter w) {
        this(id, "receive");
        this.writer = w;
    }

    public void handleMessage(Message message) throws Fault {
        Logger messageLogger = getMessageLogger(message);
        if (this.writer != null || messageLogger.isLoggable(Level.INFO)) {
            this.logging(messageLogger, message);
        }

    }


    Logger getMessageLogger(Message message) {
        Endpoint ep = message.getExchange().getEndpoint();
        if (ep != null && ep.getEndpointInfo() != null) {
            EndpointInfo endpoint = ep.getEndpointInfo();
            if (endpoint.getService() == null) {
                return this.getLogger();
            }
            else {
                Logger messageLogger = (Logger) endpoint.getProperty("MessageLogger", Logger.class);
                if (messageLogger == null) {
                    String serviceName = endpoint.getService().getName().getLocalPart();
                    InterfaceInfo iface = endpoint.getService().getInterface();
                    String portName = endpoint.getName().getLocalPart();
                    String portTypeName = iface.getName().getLocalPart();
                    String logName = "org.apache.cxf.services." + serviceName + "." + portName + "." + portTypeName;
                    messageLogger = LogUtils.getL7dLogger(this.getClass(), (String) null, logName);
                    endpoint.setProperty("MessageLogger", messageLogger);
                }

                return messageLogger;
            }
        }
        else {
            return this.getLogger();
        }
    }


    protected void logging(java.util.logging.Logger messageLogger, Message message) throws Fault {
        if (!message.containsKey(LoggingMessage.ID_KEY)) {
            String id = (String) message.getExchange().get(LoggingMessage.ID_KEY);
            if (id == null) {
                id = LoggingMessage.nextId();
                message.getExchange().put(LoggingMessage.ID_KEY, id);
            }

            message.put(LoggingMessage.ID_KEY, id);
            LoggingMessage buffer = new LoggingMessage("Inbound Message\n----------------------------", id);

            addResponseCodeToBuffer(message, buffer);
            String encoding1 = extractAndAddEncodingToBuffer(message, buffer);
            addHttpMethodToBuffer(message, buffer);
            String ct = extractAndAddContentTypeToBuffer(message, buffer);
            addHeadersToBuffer(message, buffer);
            addUriToBuffer(message, buffer);

            if (!this.isShowBinaryContent() && this.isBinaryContent(ct)) {
                buffer.getMessage().append("--- Binary Content ---").append('\n');
                this.log(messageLogger, buffer.toString());
            }
            else {
                InputStream is1 = (InputStream) message.getContent(InputStream.class);
                if (is1 != null) {
                    this.logInputStream(message, is1, buffer, encoding1, ct);
                }
                else {
                    Reader reader = (Reader) message.getContent(Reader.class);
                    if (reader != null) {
                        this.logReader(message, reader, buffer);
                    }
                }

                this.log(messageLogger, this.formatLoggingMessage(buffer));
            }
        }
    }

    private void addUriToBuffer(Message message, LoggingMessage buffer) {
        String uri = (String) message.get("org.apache.cxf.request.url");
        String is;
        if (uri == null) {
            is = (String) message.get(Message.ENDPOINT_ADDRESS);
            uri = (String) message.get("org.apache.cxf.request.uri");
            if (uri != null && uri.startsWith("/")) {
                if (is != null && !is.startsWith(uri)) {
                    if (is.endsWith("/") && is.length() > 1) {
                        is = is.substring(0, is.length());
                    }

                    uri = is + uri;
                }
            }
            else {
                uri = is;
            }
        }

        if (uri != null) {
            buffer.getAddress().append(uri);
            is = (String) message.get(Message.QUERY_STRING);
            if (is != null) {
                buffer.getAddress().append("?").append(is);
            }
        }
    }

    private void addResponseCodeToBuffer(Message message, LoggingMessage buffer) {
        if (!Boolean.TRUE.equals(message.get("decoupled.channel.message"))) {
            Integer responseCode = (Integer) message.get(Message.RESPONSE_CODE);
            if (responseCode != null) {
                buffer.getResponseCode().append(responseCode);
            }
        }
    }

    private void addHeadersToBuffer(Message message, LoggingMessage buffer) {
        Object headers = message.get(Message.PROTOCOL_HEADERS);
        if (headers != null) {
            buffer.getHeader().append(headers);
        }
    }

    private String extractAndAddContentTypeToBuffer(Message message, LoggingMessage buffer) {
        String ct = (String) message.get("Content-Type");
        if (ct != null) {
            buffer.getContentType().append(ct);
        }
        return ct;
    }

    private void addHttpMethodToBuffer(Message message, LoggingMessage buffer) {
        String httpMethod = (String) message.get("org.apache.cxf.request.method");
        if (httpMethod != null) {
            buffer.getHttpMethod().append(httpMethod);
        }
    }

    private String extractAndAddEncodingToBuffer(Message message, LoggingMessage buffer) {
        String encoding1 = (String) message.get(Message.ENCODING);
        if (encoding1 != null) {
            buffer.getEncoding().append(encoding1);
        }
        return encoding1;
    }

    protected void logReader(Message message, Reader reader, LoggingMessage buffer) {
        try {
            CachedWriter e = new CachedWriter();
            IOUtils.copyAndCloseInput(reader, e);
            message.setContent(Reader.class, e.getReader());
            if (e.getTempFile() != null) {
                buffer.getMessage().append("\nMessage (saved to tmp file):\n");
                buffer.getMessage().append("Filename: " + e.getTempFile().getAbsolutePath() + "\n");
            }

            if (e.size() > (long) this.limit && this.limit != -1) {
                buffer.getMessage().append("(message truncated to " + this.limit + " bytes)\n");
            }

            StringBuilder payload = buffer.getPayload();
            e.writeCacheTo(payload, (long) this.limit);
            setXmlOut(message, payload.toString());
        }
        catch (Exception var5) {
            throw new Fault(var5);
        }
    }

    protected void logInputStream(Message message, InputStream is, LoggingMessage buffer, String encoding, String ct) {
        CachedOutputStream bos = new CachedOutputStream();
        if (this.threshold > 0L) {
            bos.setThreshold(this.threshold);
        }

        try {
            InputStream e = is instanceof DelegatingInputStream ? ((DelegatingInputStream) is).getInputStream() : is;
            IOUtils.copyAtLeast(e, bos, this.limit == -1 ? 2147483647 : this.limit);
            bos.flush();
            SequenceInputStream e1 = new SequenceInputStream(bos.getInputStream(), e);
            if (is instanceof DelegatingInputStream) {
                ((DelegatingInputStream) is).setInputStream(e1);
            }
            else {
                message.setContent(InputStream.class, e1);
            }

            if (bos.getTempFile() != null) {
                buffer.getMessage().append("\nMessage (saved to tmp file):\n");
                buffer.getMessage().append("Filename: " + bos.getTempFile().getAbsolutePath() + "\n");
            }

            if (bos.size() > (long) this.limit && this.limit != -1) {
                buffer.getMessage().append("(message truncated to " + this.limit + " bytes)\n");
            }

            StringBuilder payload = buffer.getPayload();
            this.writePayload(payload, bos, encoding, ct);
            setXmlOut(message, payload.toString());
            bos.close();
        }
        catch (Exception var8) {
            throw new Fault(var8);
        }
    }

    protected String formatLoggingMessage(LoggingMessage loggingMessage) {
        return loggingMessage.toString();
    }

    protected java.util.logging.Logger getLogger() {
        return LOG;
    }

    private void setXmlOut(Message message, String xmlMessage) throws IOException {
        String prettyPrintXml = prettyPrintXml(xmlMessage);
        message.put(REQUEST_XML, prettyPrintXml);
    }


    public String prettyPrintXml(String xml) {

        try {
            InputSource src = new InputSource(new StringReader(xml));
            Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
            Boolean keepDeclaration = Boolean.valueOf(xml.startsWith("<?xml"));

            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            LSSerializer writer = impl.createLSSerializer();

            writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
            writer.getDomConfig().setParameter("xml-declaration", keepDeclaration);

            return writer.writeToString(document);
        }
        catch (Exception e) {
            throw new Fault(e);
        }
    }
}

