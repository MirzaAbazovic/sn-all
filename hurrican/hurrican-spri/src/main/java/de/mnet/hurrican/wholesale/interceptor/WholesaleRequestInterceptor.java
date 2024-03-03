package de.mnet.hurrican.wholesale.interceptor;

import java.io.*;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mnet.hurrican.wholesale.model.RequestXml;

/**
 * Intercepts the XML that is going to be sent out and sets it on a {@link RequestXml} object on the message context.
 * <p>
 * Created by vergaragi on 06.02.2017.
 */
public class WholesaleRequestInterceptor extends LoggingOutInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(WholesaleRequestInterceptor.class);
    public static final String REQUEST_XML_OUT = "REQUEST_XML_OUT";

    public WholesaleRequestInterceptor() {
        super(Phase.PRE_STREAM);
        setPrettyLogging(true);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        final OutputStream os = message.getContent(OutputStream.class);
        final Writer iowriter = message.getContent(Writer.class);

        if (os == null && iowriter == null) {
            return;
        }
        if (os != null) {
            final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(os);
            if (threshold > 0) {
                newOut.setThreshold(threshold);
            }
            if (limit > 0) {
                newOut.setCacheLimit(limit);
            }
            message.setContent(OutputStream.class, newOut);
            newOut.registerCallback(new WholesaleRequestInterceptor.LoggingCallback(message, os));
        }
        else {
            message.setContent(Writer.class, new LogWriter(message, iowriter));
        }
    }

    class LoggingCallback implements CachedOutputStreamCallback {

        private final Message message;
        private final OutputStream origStream;

        public LoggingCallback(final Message msg, final OutputStream os) {
            this.message = msg;
            this.origStream = os;
        }

        @SuppressWarnings("squid:S1186")
        public void onFlush(CachedOutputStream cos) {

        }

        public void onClose(CachedOutputStream cos) {
            String ct = (String) message.get(Message.CONTENT_TYPE);
            StringBuilder builder = new StringBuilder();
            String encoding = (String) message.get(Message.ENCODING);
            try {
                writePayload(builder, cos, encoding, ct);
            }
            catch (Exception ex) {
                //ignore
            }
            try {
                //empty out the cache
                cos.lockOutputStream();
                cos.resetOut(null, false);
            }
            catch (Exception ex) {
                //ignore
            }
            message.setContent(OutputStream.class, origStream);
            setXmlOut(message, builder.toString());
        }
    }

    private class LogWriter extends FilterWriter {

        private StringWriter out2;
        private int count;
        private Message message;
        private final int lim;

        public LogWriter(Message message, Writer writer) {
            super(writer);
            this.message = message;
            if (!(writer instanceof StringWriter)) {
                out2 = new StringWriter();
            }
            lim = limit == -1 ? Integer.MAX_VALUE : limit;
        }

        public void write(int c) throws IOException {
            super.write(c);
            if (out2 != null && count < lim) {
                out2.write(c);
            }
            count++;
        }

        public void write(char[] cbuf, int off, int len) throws IOException {
            super.write(cbuf, off, len);
            if (out2 != null && count < lim) {
                out2.write(cbuf, off, len);
            }
            count += len;
        }

        public void write(String str, int off, int len) throws IOException {
            super.write(str, off, len);
            if (out2 != null && count < lim) {
                out2.write(str, off, len);
            }
            count += len;
        }

        public void close() throws IOException {
            StringWriter w2 = out2;
            if (w2 == null) {
                w2 = (StringWriter) out;
            }
            String ct = (String) message.get(Message.CONTENT_TYPE);
            StringBuilder builder = new StringBuilder();
            try {
                writePayload(builder, w2, ct);
            }
            catch (Exception e) {
                LOG.error("writePayload: ", e);
            }
            message.setContent(Writer.class, out);
            setXmlOut(message, builder.toString());
            super.close();
        }
    }

    private void setXmlOut(Message message, String xmlMessage) {
        RequestXml requestXml = (RequestXml) message.get(REQUEST_XML_OUT);
        if (requestXml != null) {
            requestXml.setXml(xmlMessage);
        }
    }
}