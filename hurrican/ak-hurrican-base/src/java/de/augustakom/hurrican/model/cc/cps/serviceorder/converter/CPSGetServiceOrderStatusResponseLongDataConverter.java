package de.augustakom.hurrican.model.cc.cps.serviceorder.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Custom converter for CPSGetServiceOrderStatusResponseData. Because no value ("") don't match with number type
 * (java.lang.Long)
 * <p/>
 * Error parsing CPS ServiceResponse.SOData: For input string: "" : For input string: "" ---- Debugging information ----
 * message             : For input string: "" cause-exception     : java.lang.NumberFormatException cause-message : For
 * input string: "" class               : de.augustakom.hurrican.model.cc.cps.serviceorder.CPSGetServiceOrderStatusResponseData
 * required-type       : java.lang.Long path                : /GETSOSTATUS_RESPONSE/SO_STATUS line number         : 2
 */
public class CPSGetServiceOrderStatusResponseLongDataConverter implements Converter {

    private static final String UNKNOWN = "Unknown";

    @SuppressWarnings("unchecked")
    @Override
    public boolean canConvert(Class type) {
        return type.equals(Long.class);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {

    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Long returnValue;
        String value = reader.getValue();

        if (((null != value) && value.equals("")) || UNKNOWN.equalsIgnoreCase(value)) {
            returnValue = 0L;
        }
        else {
            try {
                returnValue = Long.valueOf(value);
            }
            catch (NumberFormatException e) {
                returnValue = 0L;
            }
        }
        return returnValue;
    }
}
