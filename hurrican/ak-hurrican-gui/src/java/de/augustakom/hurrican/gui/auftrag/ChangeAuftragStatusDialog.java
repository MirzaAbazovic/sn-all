/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2007 11:58:20
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Dialog, um den Status eines Auftrags zu veraendern. <br> Dieser Dialog dient nur voruebergehend als
 * 'quick-and-dirty-hack'. <br> <br> Hintergrund: Im Connect-Bereich werden in Hurrican mittlerweile auch Auftraege von
 * MUC/NBG abgerechnet. Die techn. Daten sind jedoch in anderen Systemen gehalten. Um diese Auftraege auf 'in Betrieb'
 * bzw. auf 'gekuendigt' setzen zu koennen, soll kein Bauauftrag notwendig sein (da Realisierung nicht in AGB erfolgt).
 * <br> Der Dialog laesst die Aenderung nur bei IPSec-Client-2-Site-Aufträgen zu! <br> Die Status-Aenderung wird in der
 * Verlaufstabelle protokolliert. <br> Sobald die Connect-Abrechnung in Taifun erfolgt, sollte dieser Dialog wieder
 * entfernt werden!
 *
 *
 */
public class ChangeAuftragStatusDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(ChangeAuftragStatusDialog.class);

    // GUI-Objekte
    private AKReferenceField rfStatus = null;
    private AKJDateComponent dcInbetriebnahme = null;
    private AKJDateComponent dcKuendigung = null;

    // Modelle
    private AuftragDaten auftragDaten = null;

    /**
     * Konstruktor fuer den Dialog.
     *
     * @param auftragDaten
     */
    public ChangeAuftragStatusDialog(AuftragDaten auftragDaten) {
        super("de/augustakom/hurrican/gui/auftrag/resources/ChangeAuftragStatusDialog.xml");
        this.auftragDaten = auftragDaten;
        if (auftragDaten == null) {
            throw new IllegalArgumentException("Necessary arguments not defined!");
        }
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel lblStatus = getSwingFactory().createLabel("auftrag.status");
        AKJLabel lblInbetriebnahme = getSwingFactory().createLabel("inbetriebnahme");
        AKJLabel lblKuendigung = getSwingFactory().createLabel("kuendigung");

        rfStatus = getSwingFactory().createReferenceField("auftrag.status");
        dcInbetriebnahme = getSwingFactory().createDateComponent("inbetriebnahme");
        dcKuendigung = getSwingFactory().createDateComponent("kuendigung");

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(new AKJLabel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblStatus, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        child.add(rfStatus, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblInbetriebnahme, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(dcInbetriebnahme, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblKuendigung, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(dcKuendigung, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 4, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            ISimpleFindService sfs = getCCService(QueryCCService.class);
            rfStatus.setFindService(sfs);

            dcInbetriebnahme.setDate(auftragDaten.getInbetriebnahme());
            dcKuendigung.setDate(auftragDaten.getKuendigung());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            if (!(Produkt.PROD_ID_VPN_IPSEC_CLIENT_TO_SITE.equals(auftragDaten.getProdId()) ||
                  Produkt.PROD_ID_TV_SIGNALLIEFERUNG_MV.equals(auftragDaten.getProdId()))) {
                throw new HurricanGUIException(
                        "Der Auftragsstatus kann nur bei Aufträgen der Produkte \"IPSec-Client-2-Site\" bzw. \"TV Signallieferung MV\" manuell geändert werden!");
            }

            CCAuftragService as = getCCService(CCAuftragService.class);
            BAService bas = getCCService(BAService.class);

            // Status aendern
            Long statusIdOld = auftragDaten.getStatusId();
            auftragDaten.setStatusId(rfStatus.getReferenceIdAs(Long.class));
            auftragDaten.setInbetriebnahme(dcInbetriebnahme.getDate(auftragDaten.getInbetriebnahme()));
            auftragDaten.setKuendigung(dcKuendigung.getDate(auftragDaten.getKuendigung()));
            LOGGER.debug("...... kuendigung: " + auftragDaten.getKuendigung());
            as.saveAuftragDaten(auftragDaten, false);

            // Aenderung ueber Verlaufstabelle protokollieren
            Verlauf verlauf = new Verlauf();
            verlauf.setAuftragId(auftragDaten.getAuftragId());
            verlauf.setAkt(Boolean.FALSE);
            verlauf.setAnlass(BAVerlaufAnlass.UNDEFINED);
            verlauf.setProjektierung(Boolean.FALSE);
            verlauf.setStatusIdAlt(statusIdOld);
            verlauf.setVerlaufStatusId(VerlaufStatus.VERLAUF_ABGESCHLOSSEN);
            verlauf.setBemerkung(StringTools.formatString(getSwingFactory().getText("state.change"),
                    new Object[] { HurricanSystemRegistry.instance().getCurrentUserName(),
                            "" + statusIdOld, "" + auftragDaten.getStatusId() }
            ));
            bas.saveVerlauf(verlauf);

            prepare4Close();
            setValue(OK_OPTION);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


