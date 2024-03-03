/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.2014
 */
package de.mnet.hurrican.acceptance.actions;

import static org.testng.Assert.*;

import java.util.*;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import org.testng.Assert;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.hurrican.acceptance.utils.HurricanUtils;
import de.mnet.hurrican.acceptance.utils.TestUtils;

public class VerifyOltChildTestAction extends AbstractTestAction {

    private final HurricanUtils hurricanUtils;
    private Date gueltigBisBeforeOrEqual;
    private Date gueltigBisEqual;
    private final String oltChildBezeichungVariableName;
    private final Class<? extends HWOltChild> clazz;

    public VerifyOltChildTestAction(HurricanUtils hurricanUtils, String oltChildBezeichungVariableName, Class<? extends HWOltChild> clazz) {
        setName("verifyOltChild");
        this.hurricanUtils = hurricanUtils;
        this.oltChildBezeichungVariableName = oltChildBezeichungVariableName;
        this.clazz = clazz;
    }


    public VerifyOltChildTestAction withGueltigBisBeforeOrEqual(Date gueltigBisBeforeOrEqual) {
        this.gueltigBisBeforeOrEqual = gueltigBisBeforeOrEqual;
        return this;
    }

    public VerifyOltChildTestAction withGueltigBisEqual(Date gueltigBisEqual) {
        this.gueltigBisEqual = gueltigBisEqual;
        return this;
    }

    @Override
    public void doExecute(TestContext context) {
        final String oltChildBezeichnung = TestUtils.readMandatoryVariableFromTestContext(context, oltChildBezeichungVariableName);
        final String oltBezeichnung = TestUtils.getOltBezeichnung(context);

        try {
            HWOltChild createdOltChild = hurricanUtils.findCreatedOltChild(oltBezeichnung, oltChildBezeichnung, clazz);
            if (gueltigBisBeforeOrEqual != null) {
                assertTrue(DateTools.isDateBeforeOrEqual(createdOltChild.getGueltigBis(), gueltigBisBeforeOrEqual));
            }
            if (gueltigBisEqual != null) {
                assertTrue(DateTools.isDateEqual(createdOltChild.getGueltigBis(), gueltigBisEqual));
            }
        }
        catch (FindException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage(), e);
        }

    }

}
