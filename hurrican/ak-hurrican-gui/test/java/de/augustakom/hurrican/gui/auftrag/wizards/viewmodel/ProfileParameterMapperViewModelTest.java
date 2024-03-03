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
import de.augustakom.hurrican.model.cc.ProfileParameterMapper;

@Test(groups = BaseTest.UNIT)
public class ProfileParameterMapperViewModelTest {

    private ProfileParameterMapperViewModel generateMapper(String name, String guiName, String description) {
        ProfileParameterMapper gfastMapper = new ProfileParameterMapper();
        gfastMapper.setParameterName(name);
        gfastMapper.setParameterGuiName(guiName);
        gfastMapper.setParameterGuiDescription(description);
        return new ProfileParameterMapperViewModel(gfastMapper);
    }

    public void findNameByGUIName_CollectionContainsName_ReturnsName() {
        ProfileParameterMapperViewModel mapper = generateMapper("name", "guiName", "");

        String result = ProfileParameterMapperViewModel.findNameByGUIName(Collections.singletonList(mapper), "guiName");

        Assert.assertEquals(result, "name");
    }

    public void findNameByGUIName_CollectionNotContainsName_ReturnsNull() {
        ProfileParameterMapperViewModel mapper = generateMapper("", "", "");

        String result = ProfileParameterMapperViewModel.findNameByGUIName(Collections.singletonList(mapper), "guiName");

        Assert.assertNull(result);
    }

    public void findGUIDescriptionByName_GUIDescriptionExists_ReturnsDescription() {
        ProfileParameterMapperViewModel mapper = generateMapper("name", "", "description");

        String result = ProfileParameterMapperViewModel.findGUIDescriptionByName(Collections.singletonList(mapper), "name");

        Assert.assertEquals(result, "description");
    }

    public void findGUIDescriptionByName_GUIDescriptionNotExists_ReturnsNull() {
        ProfileParameterMapperViewModel mapper = generateMapper("name", "", null);

        String result = ProfileParameterMapperViewModel.findGUIDescriptionByName(Collections.singletonList(mapper), "name");

        Assert.assertNull(result);
    }

    public void findGUIDescriptionByName_ElementNotFound_ReturnsNull() {
        ProfileParameterMapperViewModel mapper = generateMapper("name", "", null);

        String result = ProfileParameterMapperViewModel.findGUIDescriptionByName(Collections.singletonList(mapper), "notName");

        Assert.assertNull(result);
    }
}