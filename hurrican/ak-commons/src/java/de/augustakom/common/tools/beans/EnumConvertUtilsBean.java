package de.augustakom.common.tools.beans;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;

/**
 *
 */
public final class EnumConvertUtilsBean extends ConvertUtilsBean {

    private final EnumConverter enumConverter = new EnumConverter();

    @Override
    public Converter lookup(Class clazz) {
        final Converter converter = super.lookup(clazz);
        // no specific converter for this class, so it's neither a String, (which has a default converter),
        // nor any known object that has a custom converter for it. It might be an enum !
        if ((converter == null) && clazz.isEnum()) {
            return enumConverter;
        }
        else {
            return converter;
        }
    }

    static class EnumConverter implements Converter {
        public Object convert(Class type, Object value) {
            return Enum.valueOf(type, (String) value);
        }
    }
}
