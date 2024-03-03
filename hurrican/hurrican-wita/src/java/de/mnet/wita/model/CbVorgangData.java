/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.08.2011 09:47:59
 */
package de.mnet.wita.model;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.*;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.WitaCBVorgangAnlage;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.CBVorgangSubOrder;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;
import de.mnet.wita.message.auftrag.Kundenwunschtermin;
import de.mnet.wita.message.common.Dateityp;

/**
 * Hilfsklasse, um die fuer einen {@link CBVorgang} notwendigen Daten zu definieren und abhaengig vom angegebenen
 * Carrier das notwendige Objekt ({@link CBVorgang} oder {@link WitaCBVorgang}) zu erstellen.
 */
public class CbVorgangData implements Serializable {

    private static final long serialVersionUID = 6613028300469621025L;

    private Long cbId;
    private final List<Long> auftragIds = newArrayList();
    private final Map<Long, Long> auftragId2AuftragId4PortChange = newHashMap();
    private Long auftragIdHvt4HvtToKvz;
    private Set<CBVorgangSubOrder> subOrders = newHashSet();
    private Long carrierId;
    private Date vorgabe;
    private Long cbVorgangTyp;
    private Long gfTypIntern;
    private Boolean vierDraht;
    private String montagehinweis;
    public String terminReservierungsId;
    private Boolean anbieterwechselTkg46 = Boolean.FALSE;
    private AKUser user;
    private Kundenwunschtermin.Zeitfenster realisierungsZeitfenster;
    private Map<Long, Set<Pair<ArchiveDocumentDto, String>>> archiveDocuments;
    private String projektKenner;
    private String vorabstimmungsId;
    private String kopplungsKenner;
    private Uebertragungsverfahren previousUetv;
    /**
     * DN__NOs fuer die Rufnummerportierung
     */
    private Set<Long> rufnummerIds = newHashSet();
    private Boolean automation = Boolean.FALSE;
    private Long cbVorgangRefId;

    /**
     * Erstellt aus den angegebenen Daten Objekte des Typs {@link CBVorgang} bzw. {@link WitaCBVorgang} (abhaengig vom
     * angegebenen Carrier.
     */
    public List<CBVorgang> buildCbVorgaenge() {
        List<CBVorgang> cbVorgaenge = new ArrayList<CBVorgang>();

        // Nur beim ersten WitaCbVorgang darf die Rufnummerportierung gesetzt sein
        boolean first = true;

        for (Long auftragId : auftragIds) {
            CBVorgang cbVorgang = (Carrier.ID_DTAG.equals(carrierId)) ? new WitaCBVorgang() : new CBVorgang();
            cbVorgang.setAuftragId(auftragId);
            cbVorgang.setCarrierId(carrierId);
            cbVorgang.setMontagehinweis(montagehinweis);
            cbVorgang.setTerminReservierungsId(terminReservierungsId);
            cbVorgang.setAnbieterwechselTkg46(anbieterwechselTkg46);
            cbVorgang.setVorgabeMnet(vorgabe);
            cbVorgang.setTyp(cbVorgangTyp);
            cbVorgang.setGfTypInternRefId(gfTypIntern);
            cbVorgang.setStatus(CBVorgang.STATUS_SUBMITTED);
            cbVorgang.setVierDraht(vierDraht);
            cbVorgang.setSubOrders(subOrders);
            cbVorgang.setBearbeiter(user);
            cbVorgang.setAutomation(automation);
            if (cbVorgang instanceof WitaCBVorgang) {
                WitaCBVorgang witaCBVorgang = (WitaCBVorgang) cbVorgang;
                witaCBVorgang.setRealisierungsZeitfenster(realisierungsZeitfenster);
                if (archiveDocuments != null) {
                    witaCBVorgang.setAnlagen(createFromArchiveDocuments(archiveDocuments.get(auftragId)));
                }
                witaCBVorgang.setProjektKenner(projektKenner);
                witaCBVorgang.setVorabstimmungsId(vorabstimmungsId);
                witaCBVorgang.setKopplungsKenner(kopplungsKenner);
                witaCBVorgang.setPreviousUebertragungsVerfahren(previousUetv);
                if (first) {
                    witaCBVorgang.setRufnummerIds(rufnummerIds);
                    first = false;
                }
                witaCBVorgang.setCbVorgangRefId(cbVorgangRefId);
            }

            cbVorgaenge.add(cbVorgang);
        }

        return cbVorgaenge;
    }

    public static List<WitaCBVorgangAnlage> createFromArchiveDocuments(Set<Pair<ArchiveDocumentDto, String>> archiveDocuments) {
        List<WitaCBVorgangAnlage> result = new ArrayList<>();
        if (CollectionTools.isNotEmpty(archiveDocuments)) {
            for (Pair<ArchiveDocumentDto, String> archiveDoc : archiveDocuments) {
                WitaCBVorgangAnlage anlage = new WitaCBVorgangAnlage();
                ArchiveDocumentDto archiveDocumentDto = archiveDoc.getFirst();
                anlage.setArchiveKey(archiveDocumentDto.getKey());
                String mimeType = archiveDocumentDto.getMimeType() != null
                        ? archiveDocumentDto.getMimeType()
                        : Dateityp.fromExtension(archiveDocumentDto.getFileExtension()).mimeTyp;
                anlage.setMimeType(mimeType);
                anlage.setArchiveVertragsNr(archiveDocumentDto.getVertragsNr());
                anlage.setArchiveDocumentType(archiveDocumentDto.getDocumentType());
                anlage.setAnlagentyp(archiveDoc.getSecond());
                result.add(anlage);
            }
        }
        return result;
    }

    public Long getCbId() {
        return cbId;
    }

    public CbVorgangData withCbId(Long cbId) {
        this.cbId = cbId;
        return this;
    }

    public CbVorgangData withSubOrders(Set<CBVorgangSubOrder> subOrders, Boolean vierDraht) {
        this.vierDraht = vierDraht;

        if (BooleanTools.nullToFalse(vierDraht)) {
            this.subOrders = subOrders;
        }
        else {
            auftragIds.addAll(extractAuftragIds(subOrders));

        }
        return this;
    }

    public CbVorgangData withCbVorgangTyp(Long cbVorgangTyp) {
        this.cbVorgangTyp = cbVorgangTyp;
        return this;
    }

    public CbVorgangData withGfTypIntern(Long gfTypIntern) {
        this.gfTypIntern = gfTypIntern;
        return this;
    }

    public CbVorgangData withMontagehinweis(String montagehinweis) {
        this.montagehinweis = montagehinweis;
        return this;
    }

    public CbVorgangData withTerminReservierungsId(String terminReservierungsId) {
        this.terminReservierungsId = terminReservierungsId;
        return this;
    }

    public CbVorgangData withAnbieterwechselTkg46(Boolean abw) {
        this.anbieterwechselTkg46 = abw;
        return this;
    }

    public CbVorgangData withRealisierungsZeitfenster(Kundenwunschtermin.Zeitfenster zeitfenster) {
        this.realisierungsZeitfenster = zeitfenster;
        return this;
    }

    public CbVorgangData withUser(AKUser user) {
        this.user = user;
        return this;
    }

    public CbVorgangData withArchiveDocuments(Map<Long, Set<Pair<ArchiveDocumentDto, String>>> archiveDocuments) {
        this.archiveDocuments = archiveDocuments;
        return this;
    }

    public CbVorgangData withCarrierId(Long carrierId) {
        this.carrierId = carrierId;
        return this;
    }

    public CbVorgangData withVorgabe(Date vorgabe) {
        this.vorgabe = copyDate(vorgabe);
        return this;
    }

    public CbVorgangData withProjektKenner(String projektKenner) {
        this.projektKenner = projektKenner;
        return this;
    }

    public CbVorgangData withVorabstimmungsId(String vorabstimmungsId) {
        if (StringUtils.isNotBlank(vorabstimmungsId)) {
            this.vorabstimmungsId = vorabstimmungsId;
        }
        return this;
    }

    public CbVorgangData withKopplungsKenner(String kopplungsKenner) {
        this.kopplungsKenner = kopplungsKenner;
        return this;
    }

    public CbVorgangData withPreviousUetv(Uebertragungsverfahren uetv) {
        this.previousUetv = uetv;
        return this;
    }

    public CbVorgangData withRufnummerIds(Set<Long> rufnummerIds) {
        this.rufnummerIds = rufnummerIds;
        return this;
    }

    public CbVorgangData withAutomation(Boolean automation) {
        this.automation = automation;
        return this;
    }

    public List<Long> getAuftragIds() {
        return auftragIds;
    }

    public CbVorgangData addAuftragId(Long auftragIdToAdd) {
        if (!auftragIds.contains(auftragIdToAdd)) {
            this.auftragIds.add(auftragIdToAdd);
        }
        return this;
    }

    /**
     * Definiert die ID des Auftrags, der die zu kuendigende HVT TAL besitzt. Ist nur fuer den CBVorgang-Typs
     * 'TYP_HVT_KVZ' zu verwenden!
     *
     * @param auftragIdHvt4HvtToKvz
     * @return
     */
    public CbVorgangData withAuftragId4HvtToKvz(Long auftragIdHvt4HvtToKvz) {
        this.auftragIdHvt4HvtToKvz = auftragIdHvt4HvtToKvz;
        return this;
    }

    public CbVorgangData withCbVorgangHvtRef(Long cbVorgangHvtRefId) {
        this.cbVorgangRefId = cbVorgangHvtRefId;
        return this;
    }

    public CbVorgangData addAuftragId(Long auftragIdToAdd, Long auftragId4PortChange) {
        addAuftragId(auftragIdToAdd);
        if (auftragId4PortChange != null) {
            auftragId2AuftragId4PortChange.put(auftragIdToAdd, auftragId4PortChange);
        }
        return this;
    }

    public CbVorgangData addAuftragIds(List<Long> auftragIds) {
        for (Long auftragId : auftragIds) {
            addAuftragId(auftragId);
        }
        return this;
    }

    public CbVorgangData addAuftragIds(Long... auftragIds) {
        addAuftragIds(Arrays.asList(auftragIds));
        return this;
    }

    /**
     * Gibt die ID des HVt-Auftrags zurueck, der fuer den CBVorgang als Basis fuer den Wechsel nach Kvz dienen soll.
     */
    public Long getAuftragId4HvtToKvz() {
        return auftragIdHvt4HvtToKvz;
    }

    /**
     * Gibt die ID des Auftrags zurueck, der fuer den CBVorgang als Basis fuer die Port-Aenderung dienen soll.
     *
     * @param auftragId
     * @return
     */
    public Long getAuftragId4PortChange(Long auftragId) {
        return auftragId2AuftragId4PortChange.get(auftragId);
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public Date getVorgabe() {
        return copyDate(vorgabe);
    }

    private Date copyDate(Date vorgabe) {
        return (vorgabe != null) ? new Date(vorgabe.getTime()) : null;
    }

    public Long getCbVorgangTyp() {
        return cbVorgangTyp;
    }

    public String getMontagehinweis() {
        return montagehinweis;
    }

    public Boolean getAnbieterwechselTkg46() {
        return anbieterwechselTkg46;
    }

    public Kundenwunschtermin.Zeitfenster getRealisierungsZeitfenster() {
        return realisierungsZeitfenster;
    }

    public AKUser getUser() {
        return user;
    }

    public Map<Long, Set<Pair<ArchiveDocumentDto, String>>> getArchiveDocuments() {
        return archiveDocuments;
    }

    public Set<CBVorgangSubOrder> getSubOrders() {
        return subOrders;
    }

    public Boolean getVierDraht() {
        return vierDraht;
    }

    public Set<Long> getRufnummerIds() {
        return rufnummerIds;
    }

    public Boolean getAutomation() {
        return automation;
    }

    public String getProjektKenner() {
        return projektKenner;
    }

    public String getKopplungsKenner() {
        return kopplungsKenner;
    }

    public String getVorabstimmungsId() {
        return vorabstimmungsId;
    }

    private Set<Long> extractAuftragIds(Set<CBVorgangSubOrder> subOrders) {
        Set<Long> auftragIds = new HashSet<Long>();
        if (subOrders != null) {
            for (CBVorgangSubOrder subOrder : subOrders) {
                auftragIds.add(subOrder.getAuftragId());
            }
        }
        return auftragIds;
    }

}
