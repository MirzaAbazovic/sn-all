/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.02.14
 */
package de.augustakom.hurrican.gui.tools.wbci.helper;

import static de.augustakom.common.gui.swing.MessageHelper.*;
import static de.augustakom.hurrican.gui.tools.tal.wizard.CreateElTALVorgangWizard.*;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.tools.tal.wizard.CreateElTALVorgangWizard;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;

public final class HurricanAuftragHelper {
    private static final Logger LOGGER = Logger.getLogger(HurricanAuftragHelper.class);

    private HurricanAuftragHelper() {
    }

    public static void openWitaElektronischerVorgangWizard(String preselectedVaId, CBVorgangType cbVorgangType) {
        AbstractMDIMainFrame mainFrame = HurricanSystemRegistry.instance().getMainFrame();
        try {
            CreateElTALVorgangWizard wizzard = createWitaWizardWithPreselectedVaId(preselectedVaId, cbVorgangType.getId());
            DialogHelper.showDialog(mainFrame, wizzard, true, true);
        }
        catch (Exception e) {
            LOGGER.warn("Die Leitungsbestellung konnte nicht automatisch erzeugt werden.", e);
            showInfoDialog(
                    mainFrame,
                    "Die Leitungsbestellung konnte nicht automatisch erzeugt werden. Die Leitungsbestellung muss nun " +
                            "manuel über den Elektronischen-Vorgang-Wizard in der " +
                            "Carrier-Bestellung ausgelöst werden!"
            );
        }
    }

    public static void openWbciElektronischerVorgangWizard(Long auftragId, String preselectedVaId) {
        AbstractMDIMainFrame mainFrame = HurricanSystemRegistry.instance().getMainFrame();
        try {
            CreateElTALVorgangWizard wizzard = createWbciWizardWithPreselectedVaId(auftragId, preselectedVaId);
            DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), wizzard, true, true);
        }
        catch (Exception e) {
            LOGGER.warn("Die neue Vorabstimmung konnte nicht automatisch erzeugt werden.", e);
            showInfoDialog(mainFrame, "Die neue Vorabstimmung konnte nicht automatisch erzeugt werden. Die " +
                    "Vorastimmung muss nun manuel über den Elektronischen-Vorgang-Wizard in der Carrier-Bestellung " +
                    "ausgelöst werden!");
        }
    }

    public static void openHurricanAuftrag(Long auftragId) {
        AuftragIdWrapper auftragIdWrapper = new AuftragIdWrapper(auftragId);
        AuftragDataFrame.openFrame(auftragIdWrapper);
    }

    private static final class AuftragIdWrapper extends Observable implements CCAuftragModel {
        private Long auftragId;

        private AuftragIdWrapper(Long auftragId) {
            this.auftragId = auftragId;
        }

        @Override
        public void setAuftragId(Long auftragId) {
            this.auftragId = auftragId;
        }

        @Override
        public Long getAuftragId() {
            return auftragId;
        }
    }

    public enum CBVorgangType {
        NONE(null),
        ANBIETER_WECHSEL(CBVorgang.TYP_ANBIETERWECHSEL),
        KUENDIGUNG(CBVorgang.TYP_KUENDIGUNG),
        NEUBESTELLUNG(CBVorgang.TYP_NEU);
        private final Long id;

        CBVorgangType(Long id) {
            this.id = id;
        }

        private Long getId() {
            return this.id;
        }
    }
}
