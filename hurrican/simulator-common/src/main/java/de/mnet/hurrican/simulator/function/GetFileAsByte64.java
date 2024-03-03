/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2015
 */
package de.mnet.hurrican.simulator.function;

import java.io.*;
import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.functions.Function;
import com.google.common.io.Resources;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;

/**
 * Citrus-Function, um ein File Byte64-kodierter String zurueck zu geben.
 * Das File muss sich im Classpath befinden!
 */
public class GetFileAsByte64 implements Function {

    @Override
    public String execute(List<String> parameterList, TestContext testContext) {
        String fileName;
        if (CollectionUtils.isNotEmpty(parameterList) && parameterList.size() == 1) {
            fileName = parameterList.get(0);
        }
        else {
            throw new CitrusRuntimeException("GetFileAsByte64 usage: define a valid file name in the classpath!");
        }

        byte[] fileData;
        try {
            fileData = Resources.toByteArray(Resources.getResource(fileName));
        }
        catch (IOException e) {
            fileData = new byte[0];
        }

        return Base64.encodeBase64String(fileData);
    }

}
