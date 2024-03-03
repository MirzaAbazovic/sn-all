package de.mnet.migration.common.util.csv;

import java.lang.reflect.*;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.util.CsvContext;

public class ParseEnum extends CellProcessorAdaptor implements StringCellProcessor {

    private final Field field;

    public ParseEnum(Field field) {
        super();
        this.field = field;
    }

    @Override
    public Object execute(final Object value, final CsvContext context) throws SuperCsvException {
        if (value == null) {
            throw new SuperCsvConstraintViolationException("Input cannot be null on line "
                    + context.getLineNumber() + " at column " + context.getColumnNumber(), context, this);
        }
        try {
            if (value instanceof Integer) {
                return createEnumFromInt((Integer) value);
            }
            else if (value instanceof String) {
                return createEnumFromInt(Integer.parseInt((String) value));
            }
        }
        catch (Exception e) {
            throw new SuperCsvException("Parser error", context, e);
        }
        throw new SuperCsvException("Can't convert \"" + value + "\" to Integer or String."
                + value.getClass().getName(), context);
    }

    public Object createEnumFromInt(Integer intValue) throws Exception {
        if (Enum.class.isAssignableFrom(field.getType())) {
            Method valueOf = field.getType().getDeclaredMethod("valueOf", Integer.class);
            return valueOf.invoke(null, intValue);
        }
        return null;
    }
}
