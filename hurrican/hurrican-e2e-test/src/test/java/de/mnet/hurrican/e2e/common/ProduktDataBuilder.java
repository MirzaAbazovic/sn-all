/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.03.2012 16:52:54
 */
package de.mnet.hurrican.e2e.common;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.ProduktGruppeBuilder;
import de.augustakom.hurrican.service.cc.ProduktService;

/**
 * Data Builder erstellt eine neue Produktgruppe und ein neues Produkt
 */
public class ProduktDataBuilder {

    public static class ProduktData {
        public final Produkt produkt;
        public final ProduktGruppe produktGruppe;

        public ProduktData(Produkt produkt, ProduktGruppe produktGruppe) {
            this.produkt = produkt;
            this.produktGruppe = produktGruppe;
        }
    }

    private ProduktData produktData;

    @Autowired
    private ProduktService produktService;

    public synchronized ProduktData getProduktData() throws Exception {
        if (produktData == null) {
            produktData = build();
        }
        return produktData;
    }

    private <T extends AbstractCCIDModel> Long getNextId(List<T> entities) {
        long max = -1;
        for (AbstractCCIDModel entity : entities) {
            if (NumberTools.isGreater(entity.getId(), max)) {
                max = entity.getId();
            }
        }
        return max + 1;
    }

    private ProduktData build() throws Exception {
        List<ProduktGruppe> produktGruppen = produktService.findProduktGruppen();
        ProduktGruppeBuilder produktGruppeBuilder = (new ProduktGruppeBuilder()).withId(getNextId(produktGruppen));
        ProduktGruppe produktGruppe = produktGruppeBuilder.setPersist(false).build();
        produktService.saveProduktGruppe(produktGruppe);

        List<Produkt> produkte = produktService.findProdukte(false);
        ProduktBuilder produktBuilder = (new ProduktBuilder()).withProduktGruppeId(produktGruppe.getId())
                .withId(getNextId(produkte)).withAuftragserstellung(Boolean.FALSE).withLeitungsNrAnlegen(Boolean.FALSE)
                .withBuendelProdukt(Boolean.FALSE);
        Produkt produkt = produktBuilder.setPersist(false).build();
        produktService.saveProdukt(produkt);

        return new ProduktData(produkt, produktGruppe);
    }
}


