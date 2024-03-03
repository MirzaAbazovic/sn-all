/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.2011 14:07:01
 */
package de.mnet.wita.acceptance.common.function;

import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.Ordering;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

import de.mnet.wita.IOArchiveProperties.IOType;
import de.mnet.wita.dao.IoArchiveDao;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.model.AbstractWitaModel;
import de.mnet.wita.model.IoArchive;
import de.mnet.wita.model.WitaCBVorgang;

public class WaitForIoArchiveEntryFunction extends AbstractWaitForMessageFunction<IoArchive> {
    private static final Logger LOGGER = Logger.getLogger(WaitForIoArchiveEntryFunction.class);
    private final MeldungsType messageType;
    private final IOType ioType;
    protected final String externeAuftragsnummer;
    protected final String vertragsnummer;

    @Qualifier("txIoArchiveDao")
    @Autowired
    private IoArchiveDao ioArchiveDao;

    public WaitForIoArchiveEntryFunction(WitaCBVorgang cbVorgang, MeldungsType messageType, ApplicationContext applicationContext) {
        this(cbVorgang.getCarrierRefNr(), null, messageType, IOType.IN, 1, applicationContext);
    }

    public WaitForIoArchiveEntryFunction(WitaCBVorgang cbVorgang, IOType ioType, int expectedNumber, ApplicationContext applicationContext) {
        this(cbVorgang.getCarrierRefNr(), null, null, ioType, expectedNumber, applicationContext);
    }

    public WaitForIoArchiveEntryFunction(String externeAuftragsnummer, IOType ioType, ApplicationContext applicationContext) {
        this(externeAuftragsnummer, null, null, ioType, 1, applicationContext);
    }

    public WaitForIoArchiveEntryFunction(String vertragsnummer, MeldungsType messageType, IOType ioType, ApplicationContext applicationContext) {
        this(null, vertragsnummer, messageType, ioType, 1, applicationContext);
    }

    private WaitForIoArchiveEntryFunction(String externeAuftragsnummer, String vertragsnummer,
            MeldungsType messageType, IOType ioType, int expectedNumber, ApplicationContext applicationContext) {
        super(expectedNumber, Ordering.<Long>natural().onResultOf(AbstractWitaModel.GET_ID_FUNCTION), applicationContext);
        this.externeAuftragsnummer = externeAuftragsnummer;
        this.vertragsnummer = vertragsnummer;
        this.messageType = messageType;
        this.ioType = ioType;
    }

    @Override
    protected List<IoArchive> getEntities() {
        return getIOArchiveEntities(messageType, ioType);
    }

    protected final List<IoArchive> getIOArchiveEntities(MeldungsType messageType, IOType ioType) {
        LOGGER.debug("Check ioarchive with parameters messageType= " + messageType + " iotype=" + ioType
                + " extAuftragsnummer=" + externeAuftragsnummer);

        List<IoArchive> result = new ArrayList<IoArchive>();
        List<IoArchive> entities;
        if (StringUtils.isNotBlank(externeAuftragsnummer)) {
            entities = ioArchiveDao.findIoArchivesForExtOrderNo(externeAuftragsnummer);
        }
        else if (StringUtils.isNotBlank(vertragsnummer)) {
            entities = ioArchiveDao.findIoArchivesForVertragsnummer(vertragsnummer);
        }
        else {
            fail("either extAuftragsnr or Vertragsnummer must be set");
            return null; // fool compiler
        }
        for (IoArchive entry : entities) {
            if (((messageType == null) || messageType.toString().equals(entry.getRequestMeldungstyp()))
                    && (entry.getIoType() == ioType)) {
                result.add(entry);
            }
        }
        return result;
    }

}
