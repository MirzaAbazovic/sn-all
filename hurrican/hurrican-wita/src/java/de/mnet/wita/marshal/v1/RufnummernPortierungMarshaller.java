/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v1;

import java.util.*;
import com.google.common.base.Preconditions;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.OnkzDurchwahlAbfragestelleType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.OnkzRufNrType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PortierungDurchwahlanlageType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PortierungEinzelanschlussType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RufnummernListeType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RufnummernblockType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RufnummernportierungType;
import de.mnet.wita.message.common.portierung.EinzelanschlussRufnummer;
import de.mnet.wita.message.common.portierung.RufnummernBlock;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.message.common.portierung.RufnummernPortierungAnlagenanschluss;
import de.mnet.wita.message.common.portierung.RufnummernPortierungEinzelanschluss;

public class RufnummernPortierungMarshaller {

    public RufnummernportierungType generate(RufnummernPortierung input) {
        if (input == null) {
            return null;
        }
        RufnummernportierungType portierung = new RufnummernportierungType();
        if (input instanceof RufnummernPortierungAnlagenanschluss) {
            RufnummernPortierungAnlagenanschluss inputAnlagenanschlussPortierung = (RufnummernPortierungAnlagenanschluss) input;
            PortierungDurchwahlanlageType anlagenanschlussPortierung = new PortierungDurchwahlanlageType();

            OnkzDurchwahlAbfragestelleType abfragestelle = new OnkzDurchwahlAbfragestelleType();
            abfragestelle.setAbfragestelle(inputAnlagenanschlussPortierung.getAbfragestelle());
            abfragestelle.setDurchwahlnummer(inputAnlagenanschlussPortierung.getDurchwahl());
            abfragestelle.setONKZ(inputAnlagenanschlussPortierung.getOnkz());
            anlagenanschlussPortierung.setOnkzDurchwahlAbfragestelle(abfragestelle);

            List<RufnummernblockType> rufnummernBlock = anlagenanschlussPortierung.getZuPortierenderRufnummernblock();
            for (RufnummernBlock inputBlock : inputAnlagenanschlussPortierung.getRufnummernBloecke()) {
                RufnummernblockType entry = new RufnummernblockType();
                entry.setRnrBlockVon(inputBlock.getVon());
                entry.setRnrBlockBis(inputBlock.getBis());
                rufnummernBlock.add(entry);
            }

            portierung.setAnlagenanschluss(anlagenanschlussPortierung);
        }
        else {
            Preconditions.checkArgument(input instanceof RufnummernPortierungEinzelanschluss);
            RufnummernPortierungEinzelanschluss inputEinzelAnschlussPortierung = (RufnummernPortierungEinzelanschluss) input;

            PortierungEinzelanschlussType einzelanschlussPortierung = new PortierungEinzelanschlussType();
            RufnummernListeType rufnummernliste = new RufnummernListeType();
            List<OnkzRufNrType> zuPortierendeOnkzRnr = rufnummernliste.getZuPortierendeOnkzRnr();
            for (EinzelanschlussRufnummer inputRufnummer : inputEinzelAnschlussPortierung.getRufnummern()) {
                OnkzRufNrType rufnummer = new OnkzRufNrType();
                rufnummer.setONKZ(inputRufnummer.getOnkz());
                rufnummer.setRufnummer(inputRufnummer.getRufnummer());
                zuPortierendeOnkzRnr.add(rufnummer);
            }
            einzelanschlussPortierung.setRufnummernliste(rufnummernliste);

            portierung.setEinzelanschluss(einzelanschlussPortierung);
        }
        portierung.setPortierungskenner(input.getPortierungsKenner());
        return portierung;
    }
}
