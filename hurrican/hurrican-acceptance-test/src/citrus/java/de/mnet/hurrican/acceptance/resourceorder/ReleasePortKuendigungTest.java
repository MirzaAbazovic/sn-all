/*

 * Copyright (c) 2016 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 14.04.2016

 */

package de.mnet.hurrican.acceptance.resourceorder;


import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 * Created by vergarag on 04.05.2016.
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class ReleasePortKuendigungTest extends AbstractPortTestBuilder {

    /**
     * Test f&uuml;r Portfreigabe. Test f&uuml;r den Fall: Der Port wird korrekt freigegeben.
     *
     * @throws Exception
     */
    @CitrusTest
    public void ResourceOrder_ReleasePortKunedigung_Success_Test() throws Exception {
        releasePortKuendigung();
    }

}