/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2012 12:41:48
 */
package de.mnet.wita.common.converter;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = UNIT)
public class AbstractConverterFunctionTest extends BaseTest {
    private static class ConcreteConverterFunction extends AbstractConverterFunction<Number, Long, String> {

        @Override
        public String apply(Long source) {
            return source.toString();
        }

    }

    public void classShouldBeSetCorrectly() {
        ConcreteConverterFunction concreteConverterFunction = new ConcreteConverterFunction();
        assertEquals(concreteConverterFunction.getTypeToConvert(), Long.class);
    }

    public void convertShouldWorkForBaseType() {
        ConcreteConverterFunction concreteConverterFunction = new ConcreteConverterFunction();

        Number number = Long.valueOf(3);

        String result = concreteConverterFunction.convert(number);

        assertEquals(result, number.toString());
    }
}


