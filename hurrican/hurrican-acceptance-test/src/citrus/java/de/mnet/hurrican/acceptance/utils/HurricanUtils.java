/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.2014
 */
package de.mnet.hurrican.acceptance.utils;

import static org.testng.Assert.assertNotNull;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HWService;

@Component
public class HurricanUtils {

    @Autowired
    private HWService hwService;

    public <T extends HWOltChild> T findCreatedOltChild(String oltBezeichnung, String oltChildBezeichnung, Class<T> clazz) throws FindException {
        HWRack oltRack = hwService.findActiveRackByBezeichnung(oltBezeichnung);
        List<T> oltChildList = hwService.findHWOltChildByOlt(oltRack.getId(), clazz);
        assertNotNull(oltChildList);

        // reorder list from oldest-to-newest (using ID, since last created should be biggest)
        Collections.sort(oltChildList, new Comparator<T>(){
            @Override
            public int compare(T o1, T o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });

        T createdOltChild = null;
        for (T oltChild : oltChildList) {
            if (oltChild.getGeraeteBez().equals(oltChildBezeichnung)) {
                createdOltChild = oltChild;
            }
        }
        return createdOltChild;
    }
}
