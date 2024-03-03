/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.05.2011 14:47:31
 */
package de.mnet.common.service.impl;

import static com.google.common.collect.Sets.*;
import static de.mnet.wita.message.meldung.Meldung.*;

import java.time.*;
import java.time.format.*;
import java.util.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.WitaCBVorgangAnlage;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentType;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.mnet.common.service.HistoryService;
import de.mnet.common.tools.XmlPrettyFormatter;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wita.IOArchiveProperties;
import de.mnet.wita.IOArchiveProperties.IOType;
import de.mnet.wita.dao.IoArchiveDao;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.common.Anlagentyp;
import de.mnet.wita.message.common.Dateityp;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.model.AnlagenDto;
import de.mnet.wita.model.IoArchive;
import de.mnet.wita.model.SendAllowed;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaConfigService;

@CcTxRequired
public class HistoryServiceImpl implements HistoryService {

    private static final Logger LOGGER = Logger.getLogger(HistoryServiceImpl.class);

    private static final Map<Anlagentyp, ArchiveDocumentType> ANLAGEN_TYP_2_ARCHIVE_DOCUMENT_TYPE = ImmutableMap
            .<Anlagentyp, ArchiveDocumentType>builder()
            .put(Anlagentyp.KUENDIGUNG_ABGEBENDER_PROVIDER, ArchiveDocumentType.CUDA_KUENDIGUNG)
            .put(Anlagentyp.KUENDIGUNGSSCHREIBEN, ArchiveDocumentType.CUDA_KUENDIGUNG)
            .put(Anlagentyp.KUNDENAUFTRAG, ArchiveDocumentType.AUFTRAG)
            .put(Anlagentyp.LAGEPLAN, ArchiveDocumentType.LAGEPLAN)
            .put(Anlagentyp.LETZTE_TELEKOM_RECHNUNG, ArchiveDocumentType.LETZTE_TELEKOM_RECHNUNG)
            .put(Anlagentyp.PORTIERUNGSANZEIGE, ArchiveDocumentType.PORTIERUNGSAUFTRAG)
            .put(Anlagentyp.SONSTIGE, ArchiveDocumentType.WITA_SONSTIGES).build();

    private static final Comparator<AnlagenDto> COMPARATOR_ANLAGEN_DTO = (o1, o2) -> {
        int compareResult = o1.getWitaExtOrderNo().compareTo(o2.getWitaExtOrderNo());
        if (compareResult == 0) {
            return o1.getSentTimestamp().compareTo(o2.getSentTimestamp());
        }
        return compareResult;
    };

    @Autowired
    private IoArchiveDao ioArchiveDao;
    @Autowired
    private MwfEntityDao mwfEntityDao;
    @Autowired
    private WitaConfigService witaConfigService;
    @Autowired
    private CCAuftragService auftragService;
    @Autowired
    private EndstellenService endstellenService;
    @Autowired
    private CarrierService carrierService;
    @Autowired
    private CarrierElTALService carrierElTalService;
    @Autowired
    private WbciCommonService wbciCommonService;
    @Autowired
    private WitaMessageHistoryCreator witaMessageHistoryCreator;


    @Override
    public void saveIoArchive(IoArchive toSave) {
        ioArchiveDao.store(toSave);
    }

    @Override
    public List<IoArchive> findIoArchivesForExtOrderNo(String extOrderNo) {
        LOGGER.info(String.format("searching for IO-Archive entries with EXT_ORDER_NO %s", extOrderNo));
        List<IoArchive> result = ioArchiveDao.findIoArchivesForExtOrderNo(extOrderNo);
        if (LOGGER.isInfoEnabled()) {
            String count = (result != null) ? String.format("%s", result.size()) : "NULL";
            LOGGER.info(String.format("count of IO-Archive entries for EXT_ORDER_NO %s: %s", extOrderNo, count));
        }
        return result;
    }

    @Override
    public List<IoArchive> findIoArchivesForVertragsnummer(String vertragsnummer) {
        LOGGER.info(String.format("searching for IO-Archive entries with witaVertragsnummer %s", vertragsnummer));
        List<IoArchive> result = ioArchiveDao.findIoArchivesForVertragsnummer(vertragsnummer);
        if (LOGGER.isInfoEnabled()) {
            String count = (result != null) ? String.format("%s", result.size()) : "NULL";
            LOGGER.info(String.format("count of IO-Archive entries for witaVertragsnummer %s: %s", vertragsnummer, count));
        }
        return result;
    }

    @Override
    public ArchiveDocumentType getArchiveDocumentType(Anlagentyp anlagenTyp) {
        return ANLAGEN_TYP_2_ARCHIVE_DOCUMENT_TYPE.get(anlagenTyp);
    }

    @Override
    public IoArchiveTreeAndAnlagenList loadIoArchiveTreeAndAnlagenListForExtOrderNo(String... extOrderNo) {
        Preconditions.checkNotNull(extOrderNo, "ExtOrderNo must not be null");

        Set<IoArchive> archiveSet = Sets.newHashSet();
        Set<AnlagenDto> anlagen = Sets.newHashSet();
        for (String id : extOrderNo) {
            archiveSet.addAll(ioArchiveDao.findIoArchivesForExtOrderNo(id));
            archiveSet.addAll(createNotSentIoArchiveEntries(archiveSet, id));

            anlagen.addAll(findAnlagenByExtOrderNo(id));
        }

        return IoArchiveTreeAndAnlagenList.create(createTreeStructure(archiveSet), sortAnlagenDtoSet(anlagen));
    }

    @Override
    public IoArchiveTreeAndAnlagenList loadIoArchiveTreeAndAnlagenListForVertragsnummer(String vertragsnummer)
            throws FindException {
        Preconditions.checkNotNull(vertragsnummer, "Vertragsnummer must not be null");

        List<IoArchive> archiveList = ioArchiveDao.findIoArchivesForVertragsnummer(vertragsnummer);

        // Zugehoerige Archiv-Eintraege ohne Vertragsnummer mit gleicher extOrderNo nachladen
        Set<String> extOrderNos = Sets.newHashSet();
        for (IoArchive archiveEntry : archiveList) {
            if (StringUtils.isNotEmpty(archiveEntry.getWitaExtOrderNo())) {
                extOrderNos.add(archiveEntry.getWitaExtOrderNo());
            }
        }
        Set<IoArchive> archiveSet = Sets.newHashSet(archiveList);
        archiveSet.addAll(ioArchiveDao.findIoArchivesForExtOrderNos(extOrderNos));

        return IoArchiveTreeAndAnlagenList.create(createTreeStructure(archiveSet),
                loadAnlagenListForVertragsnummer(vertragsnummer));
    }

    @Override
    public IoArchiveTreeAndAnlagenList loadIoArchiveTreeAndAnlagenListFor(Long cbId, Long auftragId)
            throws FindException {
        Preconditions.checkNotNull(auftragId, "AuftragId must not be null");

        // WITA EXT_ORDER_NOs ermitteln
        Set<String> extOrderNos = findExtOrderNosFor(cbId, auftragId);

        /* wenn ein Taifunauftrag fuer diesen Hurrican-Auftrag vorhanden ist, werden alle VorabstimmungIds fuer den
         * Taifunauftrag ermittelt und die zugehoerigen WBCI-Vorgaenge werden innerhalb der Historie angezeigt */
        final AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(auftragId);
        if (auftragDaten != null && auftragDaten.getAuftragNoOrig() != null) {
            extOrderNos.addAll(wbciCommonService.getPreAgreementIdsByOrderNoOrig(auftragDaten.getAuftragNoOrig()));
        }

        // WITA Vertragsnummern ermitteln
        Set<String> vertragsnummern = findVertragsnummernFor(cbId, auftragId);

        Set<IoArchive> ioArchives = Sets.newHashSet(ioArchiveDao.findIoArchivesForExtOrderNos(extOrderNos));
        for (String vertragsnummer : vertragsnummern) {
            ioArchives.addAll(ioArchiveDao.findIoArchivesForVertragsnummer(vertragsnummer));
        }
        for (String extOrderNo : extOrderNos) {
            ioArchives.addAll(createNotSentIoArchiveEntries(ioArchives, extOrderNo));
        }
        List<Pair<IoArchive, List<IoArchive>>> ioArchiveTree = createTreeStructure(ioArchives);

        List<AnlagenDto> anlagenList = findAnlagenBy(extOrderNos, vertragsnummern);
        return IoArchiveTreeAndAnlagenList.create(ioArchiveTree, anlagenList);
    }

    @Override
    public List<AnlagenDto> loadAnlagenListForVertragsnummer(String vertragsnummer) {
        Set<AnlagenDto> anlagen = findAnlagenByVertragsnummer(vertragsnummer);
        return sortAnlagenDtoSet(anlagen);
    }

    @Override
    public List<AnlagenDto> loadAnlagenListForExtOrderno(String extOrderNo) {
        Set<AnlagenDto> anlagen = findAnlagenByExtOrderNo(extOrderNo);
        return sortAnlagenDtoSet(anlagen);
    }

    Set<IoArchive> createNotSentIoArchiveEntries(Set<IoArchive> ioArchives, String extOrderNo) {
        Set<IoArchive> notSentEntries = newHashSet();
        for (MnetWitaRequest request : mwfEntityDao.findAllRequests(extOrderNo, MnetWitaRequest.class)) {
            if (!request.getRequestWurdeStorniert() && request.getSentAt() == null) {
                // Eintrag soll nur dann hinzugefuegt werden, wenn er bisher nicht gesendet wurde
                IoArchive ioArchive = createNotAlreadySentEntryFromRequest(request);
                notSentEntries.add(ioArchive);
            }
        }
        return notSentEntries;
    }

    private IoArchive createNotAlreadySentEntryFromRequest(MnetWitaRequest request) {
        IoArchive ioArchive = new IoArchive();
        ioArchive.setIoType(IOType.OUT);
        ioArchive.setIoSource(IOArchiveProperties.IOSource.WITA);
        ioArchive.setWitaExtOrderNo(request.getExterneAuftragsnummer());

        ioArchive.setRequestGeschaeftsfall(request.getGeschaeftsfall().getGeschaeftsfallTyp().name());
        ioArchive.setRequestTimestamp(request.getMwfCreationDate());
        String creationDateString = Instant.ofEpochMilli(request.getMwfCreationDate().getTime()).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        ioArchive.setRequestMeldungstyp(request.getMeldungstypForIoArchive());

        final String notSentWarning = "Dieser Auftrag, erstellt am " + creationDateString
                + ", wurde noch nicht an die Telekom geschickt.\nGrund:\n"
                + getIoArchiveMnetWitaRequestDetails(request);
        final String soapAsString = witaMessageHistoryCreator.createSoapAsString(request, witaConfigService.getDefaultWitaVersion());
        final String formattedSoapAsString = XmlPrettyFormatter.prettyFormat(soapAsString);
        ioArchive.setRequestXml(String.format("%s\n\nKommende Anfrage:\n\n%s\n", notSentWarning, formattedSoapAsString));

        return ioArchive;
    }

    private String getIoArchiveMnetWitaRequestDetails(MnetWitaRequest request) {
        SendAllowed sendAllowed = witaConfigService.checkSendAllowed(request);

        Geschaeftsfall geschaeftsfall = request.getGeschaeftsfall();
        GeschaeftsfallTyp geschaeftsfallTyp = geschaeftsfall.getGeschaeftsfallTyp();
        switch (sendAllowed) {
            case KWT_IN_ZUKUNFT:
                return sendAllowed.getText(geschaeftsfall.getKundenwunschtermin().getDatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        witaConfigService.getCountOfDaysBeforeSent(geschaeftsfallTyp));
            case SENDE_LIMIT:
                return sendAllowed.getText(geschaeftsfallTyp);
            case REQUEST_VORGEHALTEN:
                return sendAllowed.getText(witaConfigService.getMinutesWhileRequestIsOnHold());
            case EARLIEST_SEND_DATE_IN_FUTURE:
                return sendAllowed.getText(Instant.ofEpochMilli(request.getEarliestSendDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            case OK:
            default:
                return "";
        }
    }

    private Set<AnlagenDto> findAnlagenByExtOrderNo(String extOrderNo) {
        Set<AnlagenDto> resultList = Sets.newHashSet();
        resultList.addAll(findAnlagenFromRequests(mwfEntityDao.findAllRequests(extOrderNo)));
        resultList.addAll(findAnlagenFromMeldungen(mwfEntityDao.findAllMeldungen(extOrderNo)));
        return resultList;
    }

    Set<AnlagenDto> findAnlagenByVertragsnummer(String vertragsnummer) {
        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<Meldung<?>> meldungen = (List) mwfEntityDao.findByProperty(Meldung.class, VERTRAGS_NUMMER_FIELD,
                vertragsnummer);
        return findAnlagenFromMeldungen(meldungen);
    }

    private Set<String> findExtOrderNosFor(Long cbId, Long auftragId) throws FindException {
        Set<String> result = Sets.newHashSet();

        AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(auftragId);

        // extOrderNos fuer Wita-Vorgaenge ohne CB (= REX_MK)
        WitaCBVorgang example = WitaCBVorgang.createCompletelyEmptyInstance();
        example.setAuftragId(auftragId);
        List<WitaCBVorgang> cbVorgaenge = carrierElTalService.findCBVorgaengeByExample(example);
        for (CBVorgang cbVorgang : cbVorgaenge) {
            if ((cbVorgang.getCbId() == null) && StringUtils.isNotBlank(cbVorgang.getCarrierRefNr())) {
                result.add(cbVorgang.getCarrierRefNr());
            }
        }

        if ((cbId != null) && (auftragDaten != null)) {
            Endstelle endstelle = endstellenService.findEndstelle4CarrierbestellungAndAuftrag(cbId,
                    auftragDaten.getAuftragId());
            for (Carrierbestellung carrierbestellung : carrierService.findCBsTx(endstelle.getCb2EsId())) {
                List<CBVorgang> cbVorgaenge2 = carrierElTalService.findCBVorgaenge4CB(carrierbestellung.getId());
                for (CBVorgang cbVorgang : cbVorgaenge2) {
                    if ((cbVorgang.getCbId() != null) && StringUtils.isNotBlank(cbVorgang.getCarrierRefNr())) {
                        result.add(cbVorgang.getCarrierRefNr());
                    }
                }
            }
        }
        return result;
    }

    private Set<String> findVertragsnummernFor(Long cbId, Long auftragId) throws FindException {
        Set<String> result = Sets.newHashSet();
        if (cbId == null) {
            return result;
        }
        AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(auftragId);

        Endstelle endstelle = endstellenService.findEndstelle4CarrierbestellungAndAuftrag(cbId,
                auftragDaten.getAuftragId());
        for (Carrierbestellung carrierbestellung : carrierService.findCBsTx(endstelle.getCb2EsId())) {
            if (StringUtils.isNotBlank(carrierbestellung.getVtrNr())) {
                result.add(carrierbestellung.getVtrNr());
            }
        }
        return result;
    }

    List<AnlagenDto> findAnlagenBy(Set<String> extOrderNos, Set<String> vertragsnummern) {
        Set<AnlagenDto> resultList = new HashSet<>();
        for (String extOrderNo : extOrderNos) {
            resultList.addAll(findAnlagenByExtOrderNo(extOrderNo));
        }
        for (String vertragsnummer : vertragsnummern) {
            resultList.addAll(findAnlagenByVertragsnummer(vertragsnummer));
        }
        return sortAnlagenDtoSet(resultList);
    }

    private List<Pair<IoArchive, List<IoArchive>>> createTreeStructure(Set<IoArchive> archiveSet) {
        List<IoArchive> archiveList = Lists.newArrayList(archiveSet);
        Collections.sort(archiveList, IoArchive.COMPARE_BY_REQUEST_TIMESTAMP);
        ListMultimap<String, IoArchive> ioArchivesMm = createIoArchiveMultiMap(archiveList);
        return createTreeStructureFromIoArchiveMultiMap(ioArchivesMm);
    }

    /**
     * Erstellt aus den angegebenen IoArchive Eintraegen eine MultiMap mit {@code IoArchive#witaExtOrderNo} als Key.
     */
    ListMultimap<String, IoArchive> createIoArchiveMultiMap(List<IoArchive> archiveList) {
        ListMultimap<String, IoArchive> ioArchivesMm = ArrayListMultimap.create();
        int akmPvGroup = 0;
        for (IoArchive ioArchive : archiveList) {
            if (StringUtils.isBlank(ioArchive.getWitaExtOrderNo())) {
                // AKM-PVs (ohne WitaExtOrderNo) werden nochmals gruppiert:
                // - jede neue AKM_PV wird als neue Gruppe der MultiMap hinzugefuegt
                // - alle nachfolgenden Meldungen ohne WitaExtOrderNo werden als zur letzten AKM_PV gehoerend angesehen
                if (MeldungsType.AKM_PV.getValue().equals(ioArchive.getRequestMeldungstyp())) {
                    akmPvGroup++;
                }
                ioArchivesMm.put(String.format("%s_%s", MeldungsType.AKM_PV.getValue(), akmPvGroup), ioArchive);
            }
            else {
                ioArchivesMm.put(ioArchive.getWitaExtOrderNo(), ioArchive);
            }
        }
        return ioArchivesMm;
    }

    /**
     * Erstellt aus der angegebenen MultiMap eine Liste mit einer baumartigen Struktur. Der erste Eintrag eines Pairs
     * ist dabei der Parent, der zweite Eintrag eine Liste der nachfolgenden WITA-Messages.
     */
    private List<Pair<IoArchive, List<IoArchive>>> createTreeStructureFromIoArchiveMultiMap(
            ListMultimap<String, IoArchive> ioArchivesMm) {
        List<Pair<IoArchive, List<IoArchive>>> resultList = new ArrayList<>();
        for (String witaExtOrderNo : ioArchivesMm.keySet()) {
            List<IoArchive> ioArchives = ioArchivesMm.get(witaExtOrderNo);
            List<IoArchive> ioArchiveList = ioArchives.subList(1, ioArchives.size());
            // the List implementation must be serializable -> thus create a new one
            List<IoArchive> serializableList = Lists.newArrayList(ioArchiveList);
            resultList.add(Pair.create(ioArchives.get(0), serializableList));
        }

        // sort by requestTimetamp
        Collections.sort(resultList, new Comparator<Pair<IoArchive, List<IoArchive>>>() {
            @Override
            public int compare(Pair<IoArchive, List<IoArchive>> o1, Pair<IoArchive, List<IoArchive>> o2) {
                return o1.getFirst().getRequestTimestamp().compareTo(o2.getFirst().getRequestTimestamp());
            }
        });

        return resultList;
    }

    private Set<AnlagenDto> findAnlagenFromRequests(List<MnetWitaRequest> requests) {
        Set<AnlagenDto> resultList = new HashSet<>();
        for (MnetWitaRequest request : requests) {
            WitaCBVorgang cbVorgang = findWitaCbVorgang(request);
            if ((cbVorgang != null) && (cbVorgang.getAnlagen() != null)) {

                Set<AnlagenDto> result = Sets.newHashSet();
                for (WitaCBVorgangAnlage anlage : cbVorgang.getAnlagen()) {
                    AnlagenDto anlagenDto = new AnlagenDto(request.getGeschaeftsfall().getGeschaeftsfallTyp()
                            .getDisplayName(), Instant.ofEpochMilli(request.getMwfCreationDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime(), request.getExterneAuftragsnummer(),
                            toArchiveDocument(anlage)
                    );
                    result.add(anlagenDto);
                }

                resultList.addAll(result);
            }

        }
        return resultList;
    }

    private ArchiveDocumentDto toArchiveDocument(WitaCBVorgangAnlage anlage) {
        ArchiveDocumentDto archiveDocumentDto = new ArchiveDocumentDto();
        archiveDocumentDto.setDocumentType(anlage.getArchiveDocumentType());
        archiveDocumentDto.setKey(anlage.getArchiveKey());
        archiveDocumentDto.setMimeType(anlage.getMimeType());
        archiveDocumentDto.setFileExtension(Dateityp.fromMimeTyp(anlage.getMimeType()).extension);
        archiveDocumentDto.setVertragsNr(anlage.getArchiveVertragsNr());
        return archiveDocumentDto;
    }

    private Set<AnlagenDto> findAnlagenFromMeldungen(List<Meldung<?>> meldungen) {
        Set<AnlagenDto> resultList = new HashSet<>();
        for (Meldung<?> meldung : meldungen) {
            Set<AnlagenDto> result = Sets.newHashSet();
            for (Anlage anlage : meldung.getAnlagen()) {
                AnlagenDto anlagenDto = new AnlagenDto(meldung.getMeldungsTyp().getValue(),
                        Instant.ofEpochMilli(meldung.getVersandZeitstempel().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime(), meldung.getExterneAuftragsnummer(), anlage);
                result.add(anlagenDto);
            }
            resultList.addAll(result);
        }
        return resultList;
    }

    private WitaCBVorgang findWitaCbVorgang(MnetWitaRequest request) {
        WitaCBVorgang example = WitaCBVorgang.createCompletelyEmptyInstance();
        example.setCarrierRefNr(request.getExterneAuftragsnummer());
        try {
            return Iterables.getOnlyElement(carrierElTalService.findCBVorgaengeByExample(example), null);
        }
        catch (FindException e) {
            throw new RuntimeException(e);
        }

    }

    private List<AnlagenDto> sortAnlagenDtoSet(Set<AnlagenDto> resultList) {
        List<AnlagenDto> sortedResultList = Lists.newArrayList(resultList);
        Collections.sort(sortedResultList, COMPARATOR_ANLAGEN_DTO);
        return sortedResultList;
    }
}
