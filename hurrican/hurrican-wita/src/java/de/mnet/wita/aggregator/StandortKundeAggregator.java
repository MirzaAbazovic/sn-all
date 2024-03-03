/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.05.2011 13:52:25
 */
package de.mnet.wita.aggregator;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleConnect;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.model.shared.view.AuftragDatenView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.ConnectService;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Aggregator-Klasse, um das Objekt fuer den Kundenstandort (WITA: Standort A) zu erzeugen.
 */
public class StandortKundeAggregator extends BaseStandortKundeAggregator {

    private static final int MAX_LENGTH_TAE = 160;

    @Resource(name = "de.augustakom.hurrican.service.cc.ConnectService")
    ConnectService connectService;

    @Override
    protected AddressModel findAdresseStandort(WitaCBVorgang cbVorgang) throws FindException {
        return loadAnschlussadresse(cbVorgang);
    }

    @Override
    protected String getLageTae(AddressModel addressModel, WitaCBVorgang cbVorgang) throws FindException {
        if (StringUtils.isNotBlank(addressModel.getStrasseAdd())) {
            return addressModel.getStrasseAdd();
        }
        Endstelle endstelle = loadEndstelle(cbVorgang);

        String lageTae = createLageTaeFromKundenuebergabe(endstelle);
        if (StringUtils.isNotBlank(lageTae)) {
            return lageTae;
        }
        return loadLageTaeFromWholeOrder(cbVorgang, endstelle.getEndstelleTyp());
    }

    /**
     * Lage TAE aus der Definition 'Kundenuebergabe' ermitteln
     */
    private String createLageTaeFromKundenuebergabe(Endstelle endstelle) throws FindException {
        EndstelleConnect endstelleConnect = connectService.findEndstelleConnectByEndstelle(endstelle);
        if (endstelleConnect != null) {
            String lageTae = StringTools.join(
                    new String[] { endstelleConnect.getGebaeude(), endstelleConnect.getEtage(),
                            endstelleConnect.getRaum(), }, ", ", true
            );
            return StringUtils.abbreviate(lageTae, MAX_LENGTH_TAE);
        }
        return null;
    }

    private String loadLageTaeFromWholeOrder(WitaCBVorgang cbVorgang, String endstelleTyp) throws FindException {
        List<AuftragDatenView> loadAuftragFromWholeOrder = loadAuftragFromWholeOrder(cbVorgang.getAuftragId());
        for (AuftragDatenView adView : loadAuftragFromWholeOrder) {
            Endstelle endstelle2 = endstellenService.findEndstelle4Auftrag(adView.getAuftragId(), endstelleTyp);
            if(endstelle2 != null) {
                String lageTae = createLageTaeFromKundenuebergabe(endstelle2);
                if (StringUtils.isNotBlank(lageTae)) {
                    return lageTae;
                }
            }
        }
        return null;
    }
}
