/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.11.2009 11:49:43
 */

package de.augustakom.hurrican.gui.auftrag.actions;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.shared.BAProjektierungDefinitionDialog;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.shared.iface.KundenModel;
import de.augustakom.hurrican.model.shared.view.AuftragDatenQuery;
import de.augustakom.hurrican.model.shared.view.AuftragDatenView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Basisklasse, um Projektierungen zu erstellen.
 *
 *
 */
public abstract class CreateProjektierungBaseAction extends AbstractAuftragAction {

    private static final Logger LOGGER = Logger.getLogger(CreateProjektierungBaseAction.class);

    protected AuftragDaten auftragDaten = null;
    protected AuftragTechnik auftragTechnik = null;
    protected KundenModel kundenModel = null;
    protected Produkt produkt = null;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (hasChanges()) {
            getAuftragDataFrame().saveModel();
        }

        auftragDaten = findModelByType(AuftragDaten.class);
        auftragTechnik = findModelByType(AuftragTechnik.class);
        kundenModel = findModelByType(KundenModel.class);
        if ((auftragDaten != null) && (auftragTechnik != null) && (kundenModel != null)) {

            if ((auftragDaten.getInbetriebnahme() != null)
                    || NumberTools.isGreaterOrEqual(auftragDaten.getStatusId(), AuftragStatus.PROJEKTIERUNG_ERLEDIGT)) {
                MessageHelper.showErrorDialog(getMainFrame(), new Exception(
                        "Die Projektierung kann nicht erstellt werden, da der Auftrag bereits in Betrieb ist."));
                return;
            }

            createProjektierung();
        }
        else {
            LOGGER.error("Es konnten nicht die erwarteten Objekte (AuftragDaten, AuftragTechnik, KundenModel) ermittelt werden!");
            MessageHelper.showMessageDialog(getMainFrame(), "Die Projektierung konnte nicht erstellt werden, da die\n"
                            + "Auftrags- oder Kundendaten nicht ermittelt werden konnten!", "Abbruch",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    protected abstract void createProjektierung();

    protected DialogResultSet showDialog() {
        List<AuftragDatenView> views = null;
        try {
            // Auftraege aus gleicher ProduktGruppe laden und falls vorhanden
            // zur Auswahl anbieten (Status >= 4000)
            ProduktService ps = getCCService(ProduktService.class);
            produkt = ps.findProdukt(auftragDaten.getProdId());
            if (produkt == null) {
                throw new FindException("Produkt-Daten zum Auftrag " + auftragDaten.getAuftragId() + " konnten nicht ermittelt werden.");
            }

            AuftragDatenQuery query = new AuftragDatenQuery();
            query.setKundeNo(kundenModel.getKundeNo());
            query.setAuftragStatusMin(AuftragStatus.TECHNISCHE_REALISIERUNG);

            CCAuftragService as = getCCService(CCAuftragService.class);
            views = as.findAuftragDatenViews(query, false);
            filterActualAuftrag(views);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        BAProjektierungDefinitionDialog dlg = new BAProjektierungDefinitionDialog(
                views, auftragDaten.getAuftragId(), auftragDaten.getAuftragNoOrig());
        Object value = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
        if (value instanceof AuftragDatenView) {
            return new DialogResultSet(((AuftragDatenView) value).getAuftragId(), dlg.getSelectedSubOrders());
        }
        else if ((value instanceof Integer) && value.equals(JOptionPane.OK_OPTION)) {
            return new DialogResultSet(null, dlg.getSelectedSubOrders());
        }
        return null;
    }

    private void filterActualAuftrag(List<AuftragDatenView> views) {
        Long id = auftragTechnik.getAuftragId();
        if (views != null) {
            for (AuftragDatenView v : views) {
                if (NumberTools.equal(v.getAuftragId(), id)) {
                    views.remove(v);
                    break;
                }
            }
        }
    }

    protected static class DialogResultSet {
        protected Long auftragIdAlt = null;
        protected Set<Long> subAuftragsIds = null;

        public DialogResultSet(Long auftragIdAlt, Set<Long> subAuftragsIds) {
            this.auftragIdAlt = auftragIdAlt;
            this.subAuftragsIds = subAuftragsIds;
        }
    }

}
