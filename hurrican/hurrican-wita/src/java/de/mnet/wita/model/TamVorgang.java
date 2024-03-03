/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.09.2011 11:56:13
 */
package de.mnet.wita.model;

import java.time.*;
import java.util.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.config.WitaConstants;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.model.TamUserTask.TamBearbeitungsStatus;

/**
 * DTO zur Anzeige der offenen TAMs
 */
@SuppressWarnings("unused")
public class TamVorgang extends WitaCBVorgang implements Vorgang {

    private static final long serialVersionUID = 8159007898373203487L;
    private static final Logger LOGGER = Logger.getLogger(TamVorgang.class);
    private Long auftragNoOrig;
    private String meldungsText;
    private long tamCount;
    private String meldungsCode;
    private int restFristInTagen;
    private String niederlassung;
    private TerminAnforderungsMeldung tam;
    private String leitungsBezeichnung;
    private boolean tv60Sent;
    private boolean mahnTam;

    /**
     * Original cbVorgang muss aus GUI heraus gespeichert werden können.
     */
    private WitaCBVorgang cbVorgang;

    public TamVorgang(WitaCBVorgang cbVorgang, String meldungsText, long tamCount, TerminAnforderungsMeldung tam,
            String niederlassung, Long auftragNoOrig, String leitungsBezeichnung, String meldungsCode) {
        super();
        this.cbVorgang = cbVorgang;
        this.meldungsText = meldungsText;
        this.tamCount = tamCount;
        this.meldungsCode = meldungsCode;
        this.setTam(tam);
        this.niederlassung = niederlassung;
        this.auftragNoOrig = auftragNoOrig;
        this.leitungsBezeichnung = leitungsBezeichnung;
        initCBVorgangFields(cbVorgang);
    }

    public TamVorgang(WitaCBVorgang cbVorgang, TerminAnforderungsMeldung tam, long tamCount, String niederlassung,
            Long auftragNoOrig, String leitungsBezeichnung, boolean tv60Sent, boolean mahnTam) {
        this(cbVorgang, tam.getMeldungsPositionen().iterator().next().getMeldungsText(), tamCount, tam, niederlassung,
                auftragNoOrig, leitungsBezeichnung, tam.getMeldungsPositionen().iterator().next().getMeldungsCode());
        this.tv60Sent = tv60Sent;
        this.mahnTam = mahnTam;
    }


    public TamVorgang() {
        // empty constructor for testing
    }

    private void initCBVorgangFields(WitaCBVorgang cbVorgang) {
        try {
            PropertyUtils.copyProperties(this, cbVorgang);
        }
        catch (Exception e) {
            LOGGER.error("initCBVorgangFields() - copy properties of cbVorgang failed with message " + e.getMessage(),
                    e);
        }
    }

    /**
     * Aktualisiert die Daten des TamVorgangs mit dem uebergebenen WitaCBVorgang.
     */
    public void refreshWithCbVorgang(WitaCBVorgang cbVorgangNeu) {
        cbVorgang = cbVorgangNeu;
        initCBVorgangFields(cbVorgangNeu);
    }

    @Override
    public boolean isImportant() {
        return mahnTam;
    }

    public String getBearbeitungStatusAsString() {
        if (getTamUserTask() == null) {
            return null;
        }
        TamUserTask tamUserTask = getTamUserTask();
        TamBearbeitungsStatus tamStatus = tamUserTask.getTamBearbeitungsStatus();
        return (tamStatus != null) ? tamStatus.getDisplay() : null;
    }

    @Override
    public Date getLetzteAenderung() {
        if ((getTamUserTask() == null)) {
            return null;
        }
        final LocalDateTime lastChange = DateConverterUtils.asLocalDateTime(getTamUserTask().getLetzteAenderung());
        return (lastChange != null) ? Date.from(lastChange.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }

    @Override
    public String getTaskBearbeiter() {
        if ((getTamUserTask() == null) || (getTamUserTask().getBearbeiter() == null)) {
            return null;
        }
        return getTamUserTask().getBearbeiter().getLoginName();
    }

    @Override
    public String getTaskBearbeiterTeam() {
        if ((getTamUserTask() == null) || (getTamUserTask().getBearbeiter() == null)
                || (getTamUserTask().getBearbeiter().getTeam() == null)) {
            return null;
        }
        return getTamUserTask().getBearbeiter().getTeam().getName();
    }

    @Override
    public String getAuftragBearbeiter() {
        if ((cbVorgang != null) && (cbVorgang.getBearbeiter() != null)) {
            return cbVorgang.getBearbeiter().getLoginName();
        }
        return null;
    }

    @Override
    public String getAuftragBearbeiterTeam() {
        if ((cbVorgang != null) && (cbVorgang.getBearbeiter() != null) && (cbVorgang.getBearbeiter().getTeam() != null)) {
            return cbVorgang.getBearbeiter().getTeam().getName();
        }
        return null;
    }

    @Override
    public UserTask getUserTask() {
        return getTamUserTask();
    }

    public WitaCBVorgang getCbVorgang() {
        return cbVorgang;
    }

    public void setCbVorgang(WitaCBVorgang cbVorgang) {
        this.cbVorgang = cbVorgang;
    }

    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    public String getMeldungsText() {
        return meldungsText;
    }

    public void setMeldungsText(String meldungText) {
        this.meldungsText = meldungText;
    }

    public long getTamCount() {
        return tamCount;
    }

    public void setTamCount(long tamCount) {
        this.tamCount = tamCount;
    }

    public int getRestFristInTagen() {
        return restFristInTagen;
    }

    public void setRestFristInTagen(int restFristInTagen) {
        this.restFristInTagen = restFristInTagen;
    }

    public String getNiederlassung() {
        return niederlassung;
    }

    public void setNiederlassung(String niederlassung) {
        this.niederlassung = niederlassung;
    }

    public TerminAnforderungsMeldung getTam() {
        return tam;
    }

    public void setTam(TerminAnforderungsMeldung tam) {
        this.tam = tam;
    }

    public String getLeitungsBezeichnung() {
        return leitungsBezeichnung;
    }

    public void setLeitungsBezeichnung(String leitungsBezeichnung) {
        this.leitungsBezeichnung = leitungsBezeichnung;
    }

    public boolean isMahnTam() {
        return mahnTam;
    }

    public void setMahnTam(boolean mahnTam) {
        this.mahnTam = mahnTam;
    }

    public boolean isTv60Sent() {
        return tv60Sent;
    }

    public void setTv60Sent(boolean tv60Sent) {
        this.tv60Sent = tv60Sent;
    }

    @Override
    public boolean isKlaerfallSet() {
        return cbVorgang != null && cbVorgang.isKlaerfall() != null && cbVorgang.isKlaerfall();
    }

    public String getMeldungsCode() {
        return meldungsCode;
    }

    public TamArt getTamArt() {
        return WitaConstants.MELDUNGSCODE_6012.equals(meldungsCode) ? TamArt.TA : TamArt.NOT_TA;
    }
}
