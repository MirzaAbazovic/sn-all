/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.2016
 */
package de.augustakom.hurrican.gui.auftrag.wizards.viewmodel;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.ProfileParameter;

@Test(groups = BaseTest.UNIT)
public class ProfileParameterViewModelTest {

    private ProfileParameterViewModel generateProfileDefault(String name, String value) {
        ProfileParameter param = new ProfileParameter();
        param.setParameterName(name);
        param.setParameterValue(value);
        return new ProfileParameterViewModel(param);
    }

    public void groupProfileDefaultsByName_CollectionNotEmpty_ReturnsGroup() {
        ProfileParameterViewModel default1 = generateProfileDefault("name", "value1");
        ProfileParameterViewModel default2 = generateProfileDefault("name", "value2");

        Map<String, List<String>> result = ProfileParameterViewModel.
                groupProfileDefaultsByName(Arrays.asList(default1, default2));

        Map<String, List<String>> expected = new HashMap<String, List<String>>() {{
            put("name", Arrays.asList("value1", "value2"));
        }};

        Assert.assertEquals(result, expected);
    }

    public void groupProfileDefaultsByName_CollectionIsEmpty_ReturnsEmptyMap() {
        Map<String, List<String>> result = ProfileParameterViewModel.
                groupProfileDefaultsByName(Collections.emptyList());

        Assert.assertEquals(result.isEmpty(), true);
    }


}