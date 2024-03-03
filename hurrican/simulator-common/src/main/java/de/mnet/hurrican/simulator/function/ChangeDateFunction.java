package de.mnet.hurrican.simulator.function;

import java.text.*;
import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.functions.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeDateFunction implements Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeDateFunction.class);

    public String execute(List<String> parameterList, TestContext context) {
        Calendar calendar;
        SimpleDateFormat dateFormat;
        String result = "";

        if (parameterList != null && !parameterList.isEmpty()) {
            dateFormat = new SimpleDateFormat(parameterList.get(0));
        }
        else {
            throw new CitrusRuntimeException("DateFormat parameter missing in ChangeDateFunction");
        }

        if (parameterList != null && parameterList.size() > 1) {
            try {
                calendar = Calendar.getInstance();
                calendar.setTime(dateFormat.parse(parameterList.get(1)));
            }
            catch (ParseException e) {
                throw new CitrusRuntimeException(e);
            }
        }
        else {
            throw new CitrusRuntimeException("Date parameter missing in ChangeDateFunction");
        }

        if (parameterList != null && parameterList.size() > 2) {
            String offsetString = parameterList.get(2);
            calendar.add(Calendar.YEAR, getDateValueOffset(offsetString, 'y'));
            calendar.add(Calendar.MONTH, getDateValueOffset(offsetString, 'M'));
            calendar.add(Calendar.DAY_OF_YEAR, getDateValueOffset(offsetString, 'd'));
            calendar.add(Calendar.HOUR, getDateValueOffset(offsetString, 'h'));
            calendar.add(Calendar.MINUTE, getDateValueOffset(offsetString, 'm'));
            calendar.add(Calendar.SECOND, getDateValueOffset(offsetString, 's'));
        }

        try {
            result = dateFormat.format(calendar.getTime());
        }
        catch (RuntimeException e) {
            LOGGER.error("Error while formatting dateParameter value ", e);
            throw new CitrusRuntimeException(e);
        }

        return result;
    }

    /**
     * Parse offset string and add or subtract date offset value.
     *
     * @param offsetString
     * @param c
     * @return
     */
    private int getDateValueOffset(String offsetString, char c) {
        List<Character> charList = new ArrayList<>();

        int index = offsetString.indexOf(c);
        if (index != -1) {
            for (int i = index - 1; i >= 0; i--) {
                if (Character.isDigit(offsetString.charAt(i))) {
                    charList.add(0, offsetString.charAt(i));
                }
                else {

                    StringBuilder offsetValue = new StringBuilder();
                    offsetValue.append("0");
                    for (int j = 0; j < charList.size(); j++) {
                        offsetValue.append(charList.get(j));
                    }

                    if (offsetString.charAt(i) == '-') {
                        return Integer.parseInt("-" + offsetValue.toString());
                    }
                    else {
                        return Integer.parseInt(offsetValue.toString());
                    }
                }
            }
        }

        return 0;
    }
}
