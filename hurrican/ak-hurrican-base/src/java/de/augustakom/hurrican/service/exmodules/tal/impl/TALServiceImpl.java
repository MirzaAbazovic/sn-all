/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2007 08:28:13
 */
package de.augustakom.hurrican.service.exmodules.tal.impl;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.hurrican.annotation.TalTxRequired;
import de.augustakom.hurrican.dao.exmodules.tal.TALBestellungDAO;
import de.augustakom.hurrican.model.exmodules.tal.TALBestellung;
import de.augustakom.hurrican.model.exmodules.tal.TALSegment;
import de.augustakom.hurrican.model.exmodules.tal.TALVorfall;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.exmodules.tal.TALService;


/**
 * Service-Implementierung fuer TAL-Funktionen. <br> Es handelt sich hierbei um die Anbindung der DTAG TAL-Schnittstelle
 * aus MNETCALL.
 *
 *
 */
@TalTxRequired
public class TALServiceImpl extends DefaultTALService implements TALService {

    private static final Logger LOGGER = Logger.getLogger(TALServiceImpl.class);

    /**
     * Auftrag (Preselection)
     */
    private List<String> d01 = null;

    /**
     * Storno (Preslection)
     */
    private List<String> d02 = null;

    /**
     * Rufnummernmitnahme
     */
    private List<String> d07 = null;

    /**
     * TAL-Bestellung als reine Neuschaltung
     */
    private List<String> d08 = null;

    /**
     * TAL-Bestellung ohne Rufnummernmitnahme aber mit Kündigung
     */
    private List<String> d09 = null;

    /**
     * TAL-Bestellung mit Rufnummernmitnahme
     */
    private List<String> d10 = null;

    /**
     * TAL-Bestellung als reine Neuschaltung
     */
    private List<String> d12 = null;

    /**
     * TAL-Bestellung ohne Rufnummernmitnahme aber mit Kündigung
     */
    private List<String> d13 = null;

    /**
     * TAL-Bestellung mit Rufnummernmitnahme
     */
    private List<String> d14 = null;

    /**
     * Rufnummernmitnahme
     */
    private List<String> d15 = null;

    /**
     * TAL-Bestellung als reine Neuschaltung Fälle Klassifizierung: B1+B2
     */
    private List<String> d16 = null;

    /**
     * TAL-Bestellung als reine Neuschaltung Fälle Klassifizierung: B3+B4
     */
    private List<String> d17 = null;

    /**
     * TAL-Bestellung ohne Rufnummernmitnahme aber mit Kündigung Fälle Klassifizierung: B1+B2
     */
    private List<String> d18 = null;

    /**
     * TAL-Bestellung ohne Rufnummernmitnahme aber mit Kündigung Fälle Klassifizierung: B3+B4
     */
    private List<String> d19 = null;

    /**
     * TAL-Bestellung mit Rufnummernmitnahme Fälle Klassifizirung: B1 + B2
     */
    private List<String> d20 = null;

    /**
     * TAL-Bestellung mit Rufnummernmitnahme Fälle Klassifizierung: B3+B4
     */
    private List<String> d21 = null;

    /**
     * TAL-Bestellung als reine Neuschaltung
     */
    private List<String> d23 = null;

    /**
     * TAL-Bestellung ohne Rufnummernmitnahme aber mit Kündigung
     */
    private List<String> d24 = null;

    /**
     * TAL-Bestellung mit Rufnummernmitnahme
     */
    private List<String> d25 = null;

    /**
     * TAL-Bestellung als reine Neuschaltung
     */
    private List<String> d26 = null;

    /**
     * TAL-Bestellung mit Rufnummernmitnahme
     */
    private List<String> d28 = null;

    /**
     * Kündigung der TAL Auftrag
     */
    private List<String> d41 = null;

    /**
     * Kündigung der TAL-Rückmeldung
     */
    private List<String> d42 = null;

    /**
     * Kündigung durch ANE (TAL ohne Rufnummer)
     */
    private List<String> d44 = null;

    /**
     * Bestellung der Nutzungsänderung CuDA
     */
    private List<String> d54 = null;

    /**
     * Bestellung der Nutzungsänderung CuDA
     */
    private List<String> d55 = null;

    /**
     * Bestellung der Nutzungsänderung CuDA
     */
    private List<String> d56 = null;

    /**
     * Bestellung der Nutzungsänderung CuDA
     */
    private List<String> d57 = null;

    /**
     * Bestellung der Nutzungsänderung CuDA
     */
    private List<String> d58 = null;

    /**
     * Storno Kündigung TAL
     */
    private List<String> d60 = null;

    /**
     * Stornorückmeldung Kündigung TAL
     */
    private List<String> d61 = null;

    @Override
    public <T> List<T> findAll(Class<T> clazz) throws Exception {
        try {
            return ((FindAllDAO) getDAO()).findAll(clazz);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public <T> List<T> findByExample(Object example, Class<T> clazz) throws Exception {
        try {
            return ((ByExampleDAO) getDAO()).queryByExample(example, clazz);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public <T> T findById(Serializable id, Class<T> clazz) throws Exception {
        try {
            return ((FindDAO) getDAO()).findById(id, clazz);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public SortedMap<Number, SortedMap<String, List<TALSegment>>> findAllSegmentsForTBSFirstId(Number id) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        SortedMap<Number, SortedMap<String, List<TALSegment>>> segmentMap = new TreeMap<>();
        TALBestellungDAO dao = (TALBestellungDAO) getDAO();
        List<TALBestellung> talOrders = dao.findTALBestellungenByFirstId((Long) id);

        for (TALBestellung talOrder : talOrders) {
            SortedMap<String, List<TALSegment>> talSegments = new TreeMap<String, List<TALSegment>>();

            for (String seg : getSegmentDefinition(talOrder.getVorfallId(), dao)) {
                List<TALSegment> talSegment = dao.findTALSegment(seg, talOrder.getTalBestellungId());
                talSegments.put(seg, talSegment);
            }
            segmentMap.put(talOrder.getVorfallId(), talSegments);
        }
        return segmentMap;
    }

    private List<String> getSegmentDefinition(Long eventId, TALBestellungDAO talBestellungDAO) throws  NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<Long> eventIds = new Vector<Long>();
        eventIds.add(eventId);

        List<TALVorfall> talEventList = talBestellungDAO.findTALVorfaelleByIds(eventIds);

        if (CollectionTools.isNotEmpty(talEventList)) {
            TALVorfall talEvent = talEventList.get(0);

            if ((talEvent != null) && (talEvent.getName() != null)) {
                return (List<String>)
                        ((Class<?>) this.getClass())
                                .getDeclaredMethod("get" + talEvent.getName(), (Class<?>[]) null).invoke(this, (Object[]) null);
            }
        }
        return null;
    }

    /**
     * @return the d01
     */
    public List<String> getD01() {
        return d01;
    }

    /**
     * @param d01 the d01 to set
     */
    public void setD01(List<String> d01) {
        this.d01 = d01;
    }

    /**
     * @return the d02
     */
    public List<String> getD02() {
        return d02;
    }

    /**
     * @param d02 the d02 to set
     */
    public void setD02(List<String> d02) {
        this.d02 = d02;
    }

    /**
     * @return the d07
     */
    public List<String> getD07() {
        return d07;
    }

    /**
     * @param d07 the d07 to set
     */
    public void setD07(List<String> d07) {
        this.d07 = d07;
    }

    /**
     * @return the d08
     */
    public List<String> getD08() {
        return d08;
    }

    /**
     * @param d08 the d08 to set
     */
    public void setD08(List<String> d08) {
        this.d08 = d08;
    }

    /**
     * @return the d09
     */
    public List<String> getD09() {
        return d09;
    }

    /**
     * @param d09 the d09 to set
     */
    public void setD09(List<String> d09) {
        this.d09 = d09;
    }

    /**
     * @return the d10
     */
    public List<String> getD10() {
        return d10;
    }

    /**
     * @param d10 the d10 to set
     */
    public void setD10(List<String> d10) {
        this.d10 = d10;
    }

    /**
     * @return the d12
     */
    public List<String> getD12() {
        return d12;
    }

    /**
     * @param d12 the d12 to set
     */
    public void setD12(List<String> d12) {
        this.d12 = d12;
    }

    /**
     * @return the d13
     */
    public List<String> getD13() {
        return d13;
    }

    /**
     * @param d13 the d13 to set
     */
    public void setD13(List<String> d13) {
        this.d13 = d13;
    }

    /**
     * @return the d14
     */
    public List<String> getD14() {
        return d14;
    }

    /**
     * @param d14 the d14 to set
     */
    public void setD14(List<String> d14) {
        this.d14 = d14;
    }

    /**
     * @return the d15
     */
    public List<String> getD15() {
        return d15;
    }

    /**
     * @param d15 the d15 to set
     */
    public void setD15(List<String> d15) {
        this.d15 = d15;
    }

    /**
     * @return the d16
     */
    public List<String> getD16() {
        return d16;
    }

    /**
     * @param d16 the d16 to set
     */
    public void setD16(List<String> d16) {
        this.d16 = d16;
    }

    /**
     * @return the d17
     */
    public List<String> getD17() {
        return d17;
    }

    /**
     * @param d17 the d17 to set
     */
    public void setD17(List<String> d17) {
        this.d17 = d17;
    }

    /**
     * @return the d18
     */
    public List<String> getD18() {
        return d18;
    }

    /**
     * @param d18 the d18 to set
     */
    public void setD18(List<String> d18) {
        this.d18 = d18;
    }

    /**
     * @return the d19
     */
    public List<String> getD19() {
        return d19;
    }

    /**
     * @param d19 the d19 to set
     */
    public void setD19(List<String> d19) {
        this.d19 = d19;
    }

    /**
     * @return the d20
     */
    public List<String> getD20() {
        return d20;
    }

    /**
     * @param d20 the d20 to set
     */
    public void setD20(List<String> d20) {
        this.d20 = d20;
    }

    /**
     * @return the d21
     */
    public List<String> getD21() {
        return d21;
    }

    /**
     * @param d21 the d21 to set
     */
    public void setD21(List<String> d21) {
        this.d21 = d21;
    }

    /**
     * @return the d23
     */
    public List<String> getD23() {
        return d23;
    }

    /**
     * @param d23 the d23 to set
     */
    public void setD23(List<String> d23) {
        this.d23 = d23;
    }

    /**
     * @return the d24
     */
    public List<String> getD24() {
        return d24;
    }

    /**
     * @param d24 the d24 to set
     */
    public void setD24(List<String> d24) {
        this.d24 = d24;
    }

    /**
     * @return the d25
     */
    public List<String> getD25() {
        return d25;
    }

    /**
     * @param d25 the d25 to set
     */
    public void setD25(List<String> d25) {
        this.d25 = d25;
    }

    /**
     * @return the d26
     */
    public List<String> getD26() {
        return d26;
    }

    /**
     * @param d26 the d26 to set
     */
    public void setD26(List<String> d26) {
        this.d26 = d26;
    }

    /**
     * @return the d28
     */
    public List<String> getD28() {
        return d28;
    }

    /**
     * @param d28 the d28 to set
     */
    public void setD28(List<String> d28) {
        this.d28 = d28;
    }

    /**
     * @return the d41
     */
    public List<String> getD41() {
        return d41;
    }

    /**
     * @param d41 the d41 to set
     */
    public void setD41(List<String> d41) {
        this.d41 = d41;
    }

    /**
     * @return the d42
     */
    public List<String> getD42() {
        return d42;
    }

    /**
     * @param d42 the d42 to set
     */
    public void setD42(List<String> d42) {
        this.d42 = d42;
    }

    /**
     * @return the d44
     */
    public List<String> getD44() {
        return d44;
    }

    /**
     * @param d44 the d44 to set
     */
    public void setD44(List<String> d44) {
        this.d44 = d44;
    }

    /**
     * @return the d54
     */
    public List<String> getD54() {
        return d54;
    }

    /**
     * @param d54 the d54 to set
     */
    public void setD54(List<String> d54) {
        this.d54 = d54;
    }

    /**
     * @return the d55
     */
    public List<String> getD55() {
        return d55;
    }

    /**
     * @param d55 the d55 to set
     */
    public void setD55(List<String> d55) {
        this.d55 = d55;
    }

    /**
     * @return the d56
     */
    public List<String> getD56() {
        return d56;
    }

    /**
     * @param d56 the d56 to set
     */
    public void setD56(List<String> d56) {
        this.d56 = d56;
    }

    /**
     * @return the d57
     */
    public List<String> getD57() {
        return d57;
    }

    /**
     * @param d57 the d57 to set
     */
    public void setD57(List<String> d57) {
        this.d57 = d57;
    }

    /**
     * @return the d58
     */
    public List<String> getD58() {
        return d58;
    }

    /**
     * @param d58 the d58 to set
     */
    public void setD58(List<String> d58) {
        this.d58 = d58;
    }

    /**
     * @return the d60
     */
    public List<String> getD60() {
        return d60;
    }

    /**
     * @param d60 the d60 to set
     */
    public void setD60(List<String> d60) {
        this.d60 = d60;
    }

    /**
     * @return the d61
     */
    public List<String> getD61() {
        return d61;
    }

    /**
     * @param d61 the d61 to set
     */
    public void setD61(List<String> d61) {
        this.d61 = d61;
    }

}

