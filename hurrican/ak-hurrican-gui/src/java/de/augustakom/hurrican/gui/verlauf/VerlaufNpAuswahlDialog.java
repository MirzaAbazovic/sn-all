/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.11.2009 09:59:01
 */

package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.NiederlassungService;


/**
 *
 */
public class VerlaufNpAuswahlDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(VerlaufNpAuswahlDialog.class);
    private static final long serialVersionUID = 1698901341990582351L;

    private final Long verlaufId;
    private final List<AKJCheckBox> checkboxes = new ArrayList<>();

    public VerlaufNpAuswahlDialog(Long verlaufId) {
        super("de/augustakom/hurrican/gui/verlauf/resources/VerlaufNpAuswahlDialog.xml", false);
        this.verlaufId = verlaufId;
        createGUI();
    }

    @Override
    protected void doSave() {
        List<Long> result = new ArrayList<>();
        for (AKJCheckBox cb : checkboxes) {
            if (cb.isSelected()) {
                result.add(Long.valueOf(cb.getName()));
            }
        }
        prepare4Close();
        setValue((!result.isEmpty()) ? result : null);
    }

    @Override
    protected final void createGUI() {
        try {
            setTitle(getSwingFactory().getText("proj.title"));
            configureButton(CMD_SAVE, getSwingFactory().getText("btn.save.text"), getSwingFactory().getText("btn.save.tooltip"), true, true);
            configureButton(CMD_CANCEL, getSwingFactory().getText("btn.cancel.text"), getSwingFactory().getText("btn.cancel.tooltip"), true, true);

            NiederlassungService ns = getCCService(NiederlassungService.class);
            List<Niederlassung> nls = ns.findNL4Abteilung(Abteilung.NP);

            BAService bas = getCCService(BAService.class);
            Verlauf verlauf = bas.findVerlauf(verlaufId);

            CCAuftragService auftragService = getCCService(CCAuftragService.class);
            AuftragTechnik at = auftragService.findAuftragTechnikByAuftragId(verlauf.getAuftragId());

            int x = 0;
            AKJPanel nlPnl = new AKJPanel(new GridBagLayout());
            for (Niederlassung nl : nls) {
                AKJCheckBox cbForNiederlassung = new AKJCheckBox();
                cbForNiederlassung.setToolTipText(getSwingFactory().getText("cb.nl.tooltip", nl.getName()));
                cbForNiederlassung.setName(nl.getId().toString());
                cbForNiederlassung.setText(nl.getName());

                if ((at != null) && (at.getNiederlassungId() != null) && at.getNiederlassungId().equals(nl.getId())) {
                    cbForNiederlassung.setSelected(true);
                }

                checkboxes.add(cbForNiederlassung);
                nlPnl.add(cbForNiederlassung, GBCFactory.createGBC(50, 0, x++, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            }

            AKJScrollPane sp = new AKJScrollPane(nlPnl);
            sp.setBorder(BorderFactory.createEmptyBorder());
            getChildPanel().setLayout(new GridBagLayout());
            getChildPanel().add(sp, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(100, 100, 0, 2, 1, 1, GridBagConstraints.BOTH));

            revalidate();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}
