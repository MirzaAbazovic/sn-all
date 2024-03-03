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
import org.apache.commons.collections.CollectionUtils;

/**
 * Created by glinkjo on 24.08.2015.
 */
public class GetFileSize implements Function {

    @Override
    public String execute(List<String> parameterList, TestContext testContext) {
        String fileName;
        if (CollectionUtils.isNotEmpty(parameterList) && parameterList.size() == 1) {
            fileName = parameterList.get(0);
        }
        else {
            throw new CitrusRuntimeException("GetFileSize usage: define a valid file name in the classpath!");
        }

        byte[] fileData;
        try {
            fileData = Resources.toByteArray(Resources.getResource(fileName));
        }
        catch (IOException e) {
            fileData = new byte[0];
        }

        return String.format("%s", fileData.length);
    }

}
