/*
 * Copyright (c) 2017 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.03.2017
 */
package de.augustakom.hurrican.gui.tools.tal.wizard;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.service.WbciCommonService;

class VorabstimmungFaxPanel extends JPanel {

    public static final String LBL_NEUE_VORABSTIMMUNG = "Neue Vorabstimmung";
    public static final String LBL_STORNO_VORABSTIMMUNG = "Storno/Änderung";
    public static final String LBL_TERMINVERSCHIEBUNG = "Terminverschiebung";
    public static final String VORABSTIMMUNG_FAX = "Vorabstimmung Fax";

    VorabstimmungFaxPanel(long auftragId, WbciCommonService wbciCommonService) {
        super(new FlowLayout());
        setBorder(new TitledBorder(VORABSTIMMUNG_FAX));

        final Dimension buttonSize = new Dimension(135, 35);

        final JButton newVorabstimmung = new JButton(LBL_NEUE_VORABSTIMMUNG);
        newVorabstimmung.setPreferredSize(buttonSize);
        final JButton storno = new JButton(LBL_STORNO_VORABSTIMMUNG);
        storno.setPreferredSize(buttonSize);
        final JButton terminverschiebung = new JButton(LBL_TERMINVERSCHIEBUNG);
        terminverschiebung.setPreferredSize(buttonSize);

        newVorabstimmung.addActionListener(e -> new VorabstimmungFaxDialog(auftragId, wbciCommonService, RequestTyp.VA, this));
        storno.addActionListener(e -> new VorabstimmungFaxDialog(auftragId, wbciCommonService, RequestTyp.STR_AEN_ABG, this));
        terminverschiebung.addActionListener(e -> new VorabstimmungFaxDialog(auftragId, wbciCommonService, RequestTyp.TV, this));

        add(newVorabstimmung);
        add(storno);
        add(terminverschiebung);
    }

}