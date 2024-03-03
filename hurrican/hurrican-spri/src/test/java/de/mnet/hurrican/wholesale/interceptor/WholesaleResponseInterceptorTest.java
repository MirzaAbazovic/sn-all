package de.mnet.hurrican.wholesale.interceptor;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;
import org.apache.cxf.Bus;
import org.apache.cxf.binding.Binding;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.transport.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by wieran on 21.02.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class WholesaleResponseInterceptorTest {

    public static final String XML = "<soap:Envelope/>\n";
    @Mock
    Message message;

    @Before
    public void setUp() throws Exception {
        Exchange exchange = new TestExchange();
        when(message.getExchange()).thenReturn(exchange);
    }

    @Test
    public void interceptorShouldStoreXmlInMessageInputStream() {
        //given
        when(message.getContent(eq(InputStream.class))).thenReturn(new ByteArrayInputStream(XML.getBytes()));
        WholesaleResponseInterceptor interceptor = new WholesaleResponseInterceptor();

        //when
        interceptor.handleMessage(message);

        //then
        Mockito.verify(message).put(eq("org.apache.cxf.interceptor.LoggingMessage.ID"), Matchers.anyString());
        Mockito.verify(message).put(eq("REQUEST_XML"), Matchers.eq(XML));
    }

    @Test
    public void interceptorShouldStoreXmlInMessageReader() {
        //given
        Reader reader = new StringReader(XML);
        when(message.getContent(eq(Reader.class))).thenReturn(reader);

        WholesaleResponseInterceptor interceptor = new WholesaleResponseInterceptor();

        //when
        interceptor.handleMessage(message);

        //then
        Mockito.verify(message).put(eq("org.apache.cxf.interceptor.LoggingMessage.ID"), Matchers.anyString());
        Mockito.verify(message).put(eq("REQUEST_XML"), Matchers.eq(XML));
    }


    private static class TestExchange implements Exchange {
        public String string;

        @Override
        public Message getInMessage() {
            return null;
        }

        @Override
        public void setInMessage(Message message) {

        }

        @Override
        public Message getOutMessage() {
            return null;
        }

        @Override
        public void setOutMessage(Message message) {

        }

        @Override
        public Message getInFaultMessage() {
            return null;
        }

        @Override
        public void setInFaultMessage(Message message) {

        }

        @Override
        public Message getOutFaultMessage() {
            return null;
        }

        @Override
        public void setOutFaultMessage(Message message) {

        }

        @Override
        public Session getSession() {
            return null;
        }

        @Override
        public Destination getDestination() {
            return null;
        }

        @Override
        public void setDestination(Destination destination) {

        }

        @Override
        public Conduit getConduit(Message message) {
            return null;
        }

        @Override
        public void setConduit(Conduit conduit) {

        }

        @Override
        public boolean isOneWay() {
            return false;
        }

        @Override
        public boolean isSynchronous() {
            return false;
        }

        @Override
        public void setSynchronous(boolean b) {

        }

        @Override
        public void setOneWay(boolean b) {

        }

        @Override
        public void clear() {

        }

        @Override
        public Bus getBus() {
            return null;
        }

        @Override
        public Service getService() {
            return null;
        }

        @Override
        public Endpoint getEndpoint() {
            return null;
        }

        @Override
        public Binding getBinding() {
            return null;
        }

        @Override
        public BindingOperationInfo getBindingOperationInfo() {
            return null;
        }

        @Override
        public <T> T get(Class<T> aClass) {
            return null;
        }

        @Override
        public <T> void put(Class<T> aClass, T t) {

        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean containsKey(Object key) {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public Object get(Object key) {
            return string;
        }

        @Override
        public Object put(String key, Object value) {
            return string;
        }

        @Override
        public Object remove(Object key) {
            return null;
        }

        @Override
        public void putAll(Map<? extends String, ?> m) {

        }

        @Override
        public Set<String> keySet() {
            return null;
        }

        @Override
        public Collection<Object> values() {
            return null;
        }

        @Override
        public Set<Entry<String, Object>> entrySet() {
            return null;
        }
    }
}