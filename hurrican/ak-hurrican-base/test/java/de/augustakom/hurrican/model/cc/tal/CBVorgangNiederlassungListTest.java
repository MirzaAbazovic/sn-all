/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.11.2011 11:26:26
 */
package de.augustakom.hurrican.model.cc.tal;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.CBVorgangBuilder;

@Test(groups = UNIT)
public class CBVorgangNiederlassungListTest extends BaseTest {

    public void testSortOpenCbVorgaenge() {
        Date vorgabeMnet1 = DateTools.createDate(2011, 1, 1);
        Date vorgabeMnet2 = DateTools.createDate(2011, 1, 2);
        Date vorgabeMnet3 = DateTools.createDate(2011, 1, 3);
        Date vorgabeMnet4 = DateTools.createDate(2011, 1, 4);

        CBVorgangNiederlassung cbvn1 = new CBVorgangNiederlassung(new CBVorgangBuilder().withRandomId()
                .withVorgabeMnet(vorgabeMnet1).setPersist(false).build(), "NL1");
        CBVorgangNiederlassung cbvn2 = new CBVorgangNiederlassung(new CBVorgangBuilder().withRandomId()
                .withVorgabeMnet(vorgabeMnet2).setPersist(false).build(), "NL2");
        cbvn2.setPrio(Boolean.TRUE);
        CBVorgangNiederlassung cbvn3 = new CBVorgangNiederlassung(new CBVorgangBuilder().withRandomId()
                .withVorgabeMnet(vorgabeMnet3).setPersist(false).build(), "NL3");
        cbvn3.setPrio(Boolean.FALSE);
        CBVorgangNiederlassung cbvn4 = new CBVorgangNiederlassung(new CBVorgangBuilder().withRandomId()
                .withVorgabeMnet(vorgabeMnet4).setPersist(false).build(), "NL4");
        CBVorgangNiederlassung cbvn5 = new CBVorgangNiederlassung(new CBVorgangBuilder().withRandomId()
                .withVorgabeMnet(vorgabeMnet1).setPersist(false).build(), "NL5");

        CBVorgangNiederlassungList list = new CBVorgangNiederlassungList(Arrays.asList(cbvn1, cbvn2, cbvn3, cbvn4,
                cbvn5));

        list.sortOpenCbVorgaenge();

        Iterator<CBVorgangNiederlassung> it = list.getList().iterator();

        assertThat("CBV with SecondAbmReceived not sorted first", it.next(), equalTo(cbvn2));
        assertThat(it.next(), equalTo(cbvn1));
        assertThat(it.next(), equalTo(cbvn5));
        assertThat(it.next(), equalTo(cbvn3));
        assertThat(it.next(), equalTo(cbvn4));
        assertFalse(it.hasNext());
    }
}
