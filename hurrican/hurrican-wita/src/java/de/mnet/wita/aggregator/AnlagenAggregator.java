/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2011 16:22:57
 */
package de.mnet.wita.aggregator;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.WitaCBVorgangAnlage;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.exmodules.archive.ArchiveService;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.common.Anlagentyp;
import de.mnet.wita.message.common.Dateityp;
import de.mnet.wita.model.WitaCBVorgang;

public class AnlagenAggregator extends AbstractWitaDataAggregator<List<Anlage>> {

    private static final Logger LOGGER = Logger.getLogger(AnlagenAggregator.class);

    @Resource(name = "de.augustakom.hurrican.service.exmodules.archive.ArchiveService")
    ArchiveService archiveService;

    @Override
    public List<Anlage> aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        return generateAnlagen(cbVorgang);
    }

    private List<Anlage> generateAnlagen(WitaCBVorgang cbVorgang) {
        try {
            List<Anlage> mwfAnlagen = new ArrayList<Anlage>(0);

            List<WitaCBVorgangAnlage> anlagen = cbVorgang.getAnlagen();
            if (anlagen != null) {
                int counter = 0;
                for (WitaCBVorgangAnlage witaCbVorgangAnlage : anlagen) {
                    ArchiveDocumentDto archiveDoc = archiveService.retrieveDocument(
                            witaCbVorgangAnlage.getArchiveVertragsNr(), witaCbVorgangAnlage.getArchiveDocumentType(),
                            witaCbVorgangAnlage.getArchiveKey(), null);
                    if (archiveDoc == null) {
                        throw new WitaDataAggregationException(String.format(
                                "Anlage mit SAP-Id (= Vertragsnr.) %s, Dokumententyp %s und Archive-Key %s "
                                        + "konnte nicht ermittelt werden.", witaCbVorgangAnlage.getArchiveVertragsNr(),
                                witaCbVorgangAnlage.getArchiveDocumentType(), witaCbVorgangAnlage.getArchiveKey()
                        ));
                    }
                    Anlagentyp anlagentyp = getAnlagentyp(witaCbVorgangAnlage.getAnlagentyp());
                    Dateityp dateityp = getDateityp(archiveDoc.getFileExtension());

                    Anlage anlage = new Anlage();
                    anlage.setAnlagentyp(anlagentyp);
                    anlage.setBeschreibung((anlagentyp != null) ? anlagentyp.toString() : null);
                    anlage.setDateiname(String.format("%s_%s.%s", cbVorgang.getCarrierRefNr(), counter,
                            (dateityp != null) ? dateityp.extension : "pdf"));
                    anlage.setInhalt(archiveDoc.getStream());
                    anlage.setDateityp(dateityp);

                    mwfAnlagen.add(anlage);

                    counter++;
                }
            }
            return mwfAnlagen;
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new WitaDataAggregationException("Fehler bei der Aggregierung der Anlagen: " + e.getMessage(), e);
        }
    }

    protected Anlagentyp getAnlagentyp(String anlagentyp) {
        if (anlagentyp == null) {
            return Anlagentyp.SONSTIGE;
        }

        Anlagentyp result = Anlagentyp.from(anlagentyp);
        return result != null ? result : Anlagentyp.SONSTIGE;
    }

    private Dateityp getDateityp(String fileExtension) {
        if (fileExtension == null) {
            throw new WitaDataAggregationException("Fehlender Dateityp");
        }
        Dateityp result = Dateityp.fromExtension(fileExtension);
        if (result == null) {
            throw new WitaDataAggregationException("Unbekannter Dateityp " + fileExtension);
        }
        return result;
    }
}
