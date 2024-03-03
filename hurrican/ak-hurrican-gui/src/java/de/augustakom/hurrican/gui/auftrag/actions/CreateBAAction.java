/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.11.2004 08:25:53
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.shared.BADefinitionDialog;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.PhysikUebernahme;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.CreateVerlaufParameter;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;

/**
 * Action, um einen Bauauftrag fuer den aktuell gewaehlten Auftrag zu erstellen.
 *
 *
 */
public class CreateBAAction extends AbstractAuftragAction {

    private static final Logger LOGGER = Logger.getLogger(CreateBAAction.class);
    private static final long serialVersionUID = 1432694403111953884L;

    private AuftragDaten auftragDaten = null;
    private AuftragTechnik auftragTechnik = null;
    private Verlauf verlProjektierung = null;

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (hasChanges()) {
                getAuftragDataFrame().saveModel();
            }

            auftragDaten = findModelByType(AuftragDaten.class);
            auftragTechnik = findModelByType(AuftragTechnik.class);
            if ((auftragTechnik != null) && (auftragDaten != null)) {
                createVerlauf();
            }
            else {
                LOGGER.error("Die AuftragDaten und AuftragTechnik konnten nicht ermittelt werden!");
                MessageHelper.showMessageDialog(getMainFrame(), "Der Verlauf konnte nicht erstellt werden, da die\n"
                        + "Auftragsdaten nicht ermittelt werden konnten!", "Abbruch", JOptionPane.WARNING_MESSAGE);
            }
        }
        finally {
            auftragDaten = null;
            auftragTechnik = null;
            verlProjektierung = null;
        }
    }

    /**
     * Prueft, ob fuer den aktuellen Auftrag ein Bauauftrag erstellt werden kann.
     */
    private boolean canCreateVerlauf() {
        // Status 'in Betrieb' >> Info-Message und return false
        if (NumberTools.equal(auftragDaten.getStatusId(), AuftragStatus.IN_BETRIEB)) {
            MessageHelper.showInfoDialog(getMainFrame(),
                    "Bauauftrag konnte nicht erstellt werden. Bitte setzen Sie den Auftrag auf 'Aenderung'.",
                    "BA erstellen nicht möglich", null, true);
            return false;
        }

        if ((auftragTechnik.getProjektierung() != null) && auftragTechnik.getProjektierung()) {
            if (!hasProjektierung()) {
                String msg = "Für den Auftrag ist eine Projektierung vorgesehen.\n" +
                        "Wollen Sie trotzdem einen Bauauftrag verschicken?";

                int selection = MessageHelper.showConfirmDialog(getMainFrame(),
                        msg, "Bauauftrag erstellen?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (selection != JOptionPane.YES_OPTION) {
                    return false;
                }
            }
            else if ((verlProjektierung.getAkt() != null) && verlProjektierung.getAkt()) {
                MessageHelper.showInfoDialog(getMainFrame(),
                        "Für den Auftrag existiert noch eine aktive Projektierung.\n" +
                                "Der Bauauftrag kann erst erstellt werden, wenn die Projektierung " +
                                "abgeschlossen ist.",
                        "Projektierung nicht abgeschlossen", null, true
                );
                return false;
            }
        }
        return true;
    }

    /**
     * Erzeugt den Verlauf fuer den aktuellen Auftrag.
     */
    private void createVerlauf() {
        try {
            if (!canCreateVerlauf()) {
                return;
            }

            // wahrscheinliches Realisierungsdatum ermitteln
            Date defaultDate;
            if (auftragDaten.getKuendigung() != null) {
                defaultDate = auftragDaten.getKuendigung();
            }
            else {
                defaultDate = auftragDaten.getVorgabeSCV();
            }

            // falls Datum in Vergangenheit, lasse es leer
            if (DateTools.isDateBefore(defaultDate, new Date())) {
                defaultDate = null;
            }

            // Bauauftrags-Anlass auswaehlen, wenn Auftrag in Aenderung oder
            // Produkt in Hurrican angelegt werden kann (also keine Taifun-Referenz besitzt)
            boolean showBAAnlass = auftragDaten.isInAenderung();
            if (!showBAAnlass) {
                ProduktService ps = getCCService(ProduktService.class);
                Produkt produkt = ps.findProdukt(auftragDaten.getProdId());
                showBAAnlass = BooleanTools.nullToFalse(produkt.getAuftragserstellung());
            }

            BADefinitionDialog dlg = new BADefinitionDialog(
                    defaultDate, showBAAnlass, auftragDaten.getProdId(),
                    auftragDaten.getAuftragId(), auftragDaten.getAuftragNoOrig());
            Object value = DialogHelper.showDialog(getMainFrame(), dlg, true, true);

            Long installType = dlg.getInstallationType();
            Long baAnlass = dlg.getBAAnlass();
            if ((baAnlass == null) && !auftragDaten.isInAenderung()) {
                baAnlass = getBaAnlass();
            }
            Set<Long> subOrders = dlg.getSelectedSubOrders();

            if (value instanceof Date) {
                if (!checkSchaltungstag((Date) value)) {
                    return;
                }

                // BA erstellen
                BAService bas = getCCService(BAService.class);
                Pair<Verlauf, AKWarnings> result = bas.createVerlauf(new CreateVerlaufParameter(
                        auftragTechnik.getAuftragId(),
                        (Date) value,
                        baAnlass,
                        installType,
                        getBaZentraleDispo(),
                        HurricanSystemRegistry.instance().getSessionId(),
                        subOrders));

                Verlauf verlauf = result.getFirst();
                if (verlauf != null) {
                    addAmBemerkung2BA(dlg.getBemerkung());
                    checkAuftragStatus(auftragTechnik.getAuftragId());

                    AKWarnings warnings = result.getSecond();
                    if (warnings.isEmpty()) {
                        MessageHelper.showMessageDialog(getMainFrame(),
                                "Bauauftrag wurde erstellt.", "Abgeschlossen", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else {
                        MessageHelper.showMessageDialog(getMainFrame(),
                                "Bauauftrag wurde mit folgenden Warnungen erstellt:\n"
                                        + warnings.getWarningsAsText(),
                                "Abgeschlossen", JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
                else {
                    MessageHelper.showMessageDialog(getMainFrame(),
                            "Bauauftrag wurde NICHT erstellt!", "Abbruch", JOptionPane.INFORMATION_MESSAGE);
                }

                refreshFrame();
            }
            else {
                MessageHelper.showMessageDialog(getMainFrame(),
                        "Der Bauauftrag wurde NICHT erstellt.", "Abbruch", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(getMainFrame(), ex);
        }
    }

    private Long getBaAnlass() throws ServiceNotFoundException, FindException {
        if (BAVerlaufAnlass.NEUSCHALTUNG.equals(auftragTechnik.getAuftragsart())) {
            // pruefen ob Anbieterwechsel nach TKG46
            Endstelle endstelle = getCCService(EndstellenService.class).findEndstelle4Auftrag(
                    auftragTechnik.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
            List<CBVorgang> cbVorgaenge4CB = null;
            if (endstelle != null) {
                List<Carrierbestellung> cBs = getCCService(CarrierService.class).findCBs4Endstelle(endstelle.getId());
                if (!cBs.isEmpty()) {
                    cbVorgaenge4CB = getCCService(CarrierElTALService.class).findCBVorgaenge4CB(cBs.get(0).getId());
                }
            }
            if ((cbVorgaenge4CB != null) && !cbVorgaenge4CB.isEmpty()
                    && Boolean.TRUE.equals(cbVorgaenge4CB.get(0).getAnbieterwechselTkg46())) {
                return BAVerlaufAnlass.ABW_TKG46_NEUSCHALTUNG;
            }
        }
        return auftragTechnik.getAuftragsart();
    }

    /**
     * Sucht nach dem erstellten Bauauftrag fuer die Abteilung AM und setzt die Bemerkung.
     *
     * @param bemerkung
     */
    private void addAmBemerkung2BA(String bemerkung) {
        if (StringUtils.isNotBlank(bemerkung)) {
            try {
                BAService bas = getCCService(BAService.class);
                Verlauf actVerlauf = bas.findActVerlauf4Auftrag(auftragDaten.getAuftragId(), false);
                if (actVerlauf == null) {
                    throw new HurricanGUIException("Aktiver Verlauf konnte nicht ermittelt werden!");
                }

                VerlaufAbteilung verlAbtAm = bas.findVerlaufAbteilung(actVerlauf.getId(), Abteilung.AM);
                if (verlAbtAm == null) {
                    throw new HurricanGUIException("AM-Datensatz fuer Bauauftrag konnte nicht ermittelt werden!");
                }

                verlAbtAm.setBemerkung(bemerkung);
                bas.saveVerlaufAbteilung(verlAbtAm);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showInfoDialog(getMainFrame(),
                        "Die Bemerkung konnte dem Bauauftrag nicht zugeordnet werden.\nGrund: " + e.getMessage(), null,
                        true);
            }
        }
    }

    /**
     * Prueft, ob der Auftrags-Status korrekt umgesetzt wurde. Ist dies nicht der Fall, wird der Status je nach
     * vorherigem Status auf 4000, 6200 oder 9105 gesetzt. <br> <br> Dieser Check sollte eigentlich nicht notwendig
     * sein. Allerdings wird der AuftragsStatus nach dem Erstellen des BAs nicht immer korrekt aktualisiert. Deshalb
     * wird hier im Anschluss an den BA der aktuelle Auftrags-Status nochmal geprueft und wenn notwendig, umgesetzt.
     */
    private void checkAuftragStatus(Long auftragId) {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragDaten ad = as.findAuftragDatenByAuftragId(auftragId);
            if ((ad != null)
                    && !NumberTools.isIn(ad.getStatusId(), new Number[] { AuftragStatus.TECHNISCHE_REALISIERUNG,
                    AuftragStatus.AENDERUNG_IM_UMLAUF, AuftragStatus.KUENDIGUNG_TECHN_REAL })) {
                Long status2Set = null;
                if (NumberTools.isLess(ad.getStatusId(), AuftragStatus.IN_BETRIEB)) {
                    status2Set = AuftragStatus.TECHNISCHE_REALISIERUNG;
                }
                else if (NumberTools.isLess(ad.getStatusId(), AuftragStatus.KUENDIGUNG)) {
                    status2Set = AuftragStatus.AENDERUNG_IM_UMLAUF;
                }
                else if (NumberTools.isGreaterOrEqual(ad.getStatusId(), AuftragStatus.KUENDIGUNG)) {
                    status2Set = AuftragStatus.KUENDIGUNG_TECHN_REAL;
                }

                if (status2Set != null) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("Status-ID von Auftrag wird umgesetzt - Auftrag/Status: " +
                                auftragId + "/" + status2Set);
                    }
                    ad.setStatusId(status2Set);
                    as.saveAuftragDaten(ad, false);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Ueberprueft, ob es fuer den Auftrag bereits eine Projektierung gibt.
     */
    private boolean hasProjektierung() {
        try {
            BAService bas = getCCService(BAService.class);
            List<Verlauf> result = bas.findVerlaeufe4Auftrag(auftragTechnik.getAuftragId());
            if (result != null) {
                for (Verlauf v : result) {
                    if ((v.getProjektierung() != null) && v.getProjektierung()) {
                        verlProjektierung = v;
                        return true;
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }


    boolean checkSchaltungstag(Date realDate) throws FindException {
        try {
            Endstelle endstelleB = getCCService(EndstellenService.class)
                    .findEndstelle4Auftrag(auftragDaten.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
            if (endstelleB == null || endstelleB.getHvtIdStandort() == null) {
                return true;
            }

            HVTService hvtService = getCCService(HVTService.class);
            HVTStandort hvtStandort = hvtService.findHVTStandort(endstelleB.getHvtIdStandort());

            if (hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_HVT)
                    || hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ)
                    || hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_KVZ)) {

                HVTGruppe hvtGruppe = hvtService.findHVTGruppe4Standort(endstelleB.getHvtIdStandort());

                PhysikService ps = getCCService(PhysikService.class);
                PhysikUebernahme pu = ps.findLastPhysikUebernahme(auftragDaten.getAuftragId());
                if ((pu != null)
                        && (pu.getVerlaufId() == null)
                        && NumberTools.isIn(pu.getAenderungstyp(), new Number[] { PhysikaenderungsTyp.STRATEGY_DSL_KREUZUNG,
                        PhysikaenderungsTyp.STRATEGY_WANDEL_ANALOG_ISDN, PhysikaenderungsTyp.STRATEGY_WANDEL_ISDN_ANALOG })
                        // HVT-Schaltungstag ueberpruefen
                        && !hvtGruppe.isRealDatePossible(realDate)) {

                    String dayOfWeek = DateTools.formatDate(realDate, "EEEE");
                    String possibleDays = hvtGruppe.getSchaltungstage();

                    ResourceReader rr = new ResourceReader("de.augustakom.hurrican.gui.auftrag.resources.Messages");
                    String msg = rr.getValue("confirm.hvt.day", new Object[] { dayOfWeek, possibleDays });
                    String title = rr.getValue("confirm.hvt.day.title");

                    int selection = MessageHelper.showConfirmDialog(getMainFrame(), msg, title,
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (selection != JOptionPane.YES_OPTION) {
                        return false;
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Bei der Überprüfung der Schaltungstage ist ein Fehler aufgetreten.", e);
        }
        return true;
    }


    /**
     * Definiert, ob der Bauauftrag an die zentrale Dispo erstellt werden soll.
     */
    public boolean getBaZentraleDispo() {
        return false;
    }

}
