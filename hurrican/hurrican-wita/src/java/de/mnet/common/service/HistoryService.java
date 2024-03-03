/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.05.2011 14:43:20
 */
package de.mnet.common.service;

import java.util.*;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentType;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wita.activiti.BusinessKeyUtils;
import de.mnet.wita.message.common.Anlagentyp;
import de.mnet.wita.model.AnlagenDto;
import de.mnet.wita.model.IoArchive;
import de.mnet.wita.service.WitaService;

/**
 * Service fuer die WITA- und WBCI-History, die die Einträge aus dem IO-Archiv und vorgehaltene WITA-Vorgänge als Tree
 * zur Verfügung stellt.
 */
public interface HistoryService extends WitaService {

    final class IoArchiveTreeAndAnlagenList extends Pair<List<Pair<IoArchive, List<IoArchive>>>, List<AnlagenDto>> {

        private static final long serialVersionUID = -2576221777578764337L;

        public static IoArchiveTreeAndAnlagenList create(List<Pair<IoArchive, List<IoArchive>>> first,
                List<AnlagenDto> second) {
            return new IoArchiveTreeAndAnlagenList(first, second);
        }

        private IoArchiveTreeAndAnlagenList(List<Pair<IoArchive, List<IoArchive>>> first, List<AnlagenDto> second) {
            super(first, second);
        }
    }

    /**
     * Speichert das angegebene {@link IoArchive} Objekt.
     *
     * @param toSave
     */
    void saveIoArchive(IoArchive toSave);

    /**
     * @return List of IoArchive entries corresponding to the extOrderNo sorted by request_timestamp ascending (is aware
     * of {@link de.mnet.wita.activiti.BusinessKeyUtils#KUEDT_SUFFIX})
     */
    List<IoArchive> findIoArchivesForExtOrderNo(String extOrderNo);

    /**
     * @return List of IoArchive entries corresponding to the vertragsnummer sorted by request_timestamp ascending (is
     * aware of {@link BusinessKeyUtils#KUEDT_SUFFIX})
     */
    List<IoArchive> findIoArchivesForVertragsnummer(String vertragsnummer);

    /**
     * Gibt den Scanview DocumentType fuer den Wita AnlagenType zurueck.
     */
    ArchiveDocumentType getArchiveDocumentType(Anlagentyp anlagenTyp);

    /**
     * Ermittelt alle IO-Archiv Eintraege als Baum und alle {@link AnlagenDto}s zu der angegebenen {@code extOrderNo}.
     *
     * @param extOrderNo externe Auftragsnummer nach der IO-Archiv-Eintraege ermittelt werden sollen
     * @return Liste mit den vorhandenen Archiv-Eintraegen als Baustruktur und sortierte Liste mit zugehörigen Anlagen
     * @throws FindException         wenn bei der Abfrage ein Fehler auftritt.
     * @throws NullPointerException, falls {@code extOrderNo} {@code null}
     */
    IoArchiveTreeAndAnlagenList loadIoArchiveTreeAndAnlagenListForExtOrderNo(String... extOrderNo);

    /**
     * Siehe auch {@link HistoryService#loadIoArchiveTreeAndAnlagenListForExtOrderNo(String...)}. Allerdings wird als
     * Suchparameter die Vertragsnummer (= {@code vtnr}) verwendet. Auch zugehoerige IO-Archiv-Eintraege und Anlagen mit
     * zur Vertragsnummer gehoerenden externen Auftragsnummern werden zurueckgegeben. <b>Achtung:</b> Für die Ermittlung
     * der Anlagen werden nur Meldungen durchsucht!
     *
     * @param vertragsnummer nach der IO-Archiv-Eintraege ermittelt werden sollen
     * @return Liste mit den vorhandenen Archiv-Eintraegen als Baustruktur und sortierte Liste mit zugehörigen Anlagen
     * @throws FindException         wenn bei der Abfrage ein Fehler auftritt.
     * @throws NullPointerException, falls {@code vertragsnummer} {@code null}
     */
    IoArchiveTreeAndAnlagenList loadIoArchiveTreeAndAnlagenListForVertragsnummer(String vertragsnummer)
            throws FindException;

    /**
     * Ermittelt alle IO-Archiv Eintraege als Baum und alle {@link AnlagenDto}s zu der angegebenen {@code auftragId} und
     * {@code cbId}.
     * 
     * @param cbId (optional) ID der Carrierbestellung, ueber die die Endstelle ermittelt wird, ansonsten nur REX-MK,
     *            die keine CB haben
     * @param auftragId zu diesem Auftrag werden alle IO-Archiv Einträge gesammelt (abhängig von Endstelle)
     * @return Tree-Struktur für IoArchiv und Anlagen angezeigt IoArchiv-Einträge
     */
    IoArchiveTreeAndAnlagenList loadIoArchiveTreeAndAnlagenListFor(Long cbId, Long auftragId)
            throws FindException;

    /**
     * Ermittelt alle {@link AnlagenDto}s zu der angegebenen {@code extOrderNo}.
     *
     * @throws FindException         wenn bei der Abfrage ein Fehler auftritt.
     * @throws NullPointerException, falls {@code vertragsnummer} {@code null}
     */
    List<AnlagenDto> loadAnlagenListForVertragsnummer(String vertragsnummer);

    /**
     * Ermittelt alle {@link AnlagenDto}s zu der angegebenen {@code extOrderNo}.
     *
     * @throws FindException         wenn bei der Abfrage ein Fehler auftritt.
     * @throws NullPointerException, falls {@code extOrderNo} {@code null}
     */
    List<AnlagenDto> loadAnlagenListForExtOrderno(String extOrderNo);

}
