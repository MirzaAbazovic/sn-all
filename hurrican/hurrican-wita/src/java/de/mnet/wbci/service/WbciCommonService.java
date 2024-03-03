/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.07.13
 */
package de.mnet.wbci.service;

import java.util.*;
import javax.annotation.*;
import javax.validation.constraints.*;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.WBCIVorabstimmungFax;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wbci.dao.VorabstimmungIdsByBillingOrderNoCriteria;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.OverdueAbmPvVO;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.TechnischeRessource;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequest;

/**
 *
 */
public interface WbciCommonService extends WbciService {

    WBCIVorabstimmungFax save(WBCIVorabstimmungFax wbciVorabstimmungFax);

    WBCIVorabstimmungFax findByVorabstimmungsID(String vorabstimmungsId);

    List<WBCIVorabstimmungFax> findAll(long auftragsId);

    List<WBCIVorabstimmungFax> findAll(long auftragId, RequestTyp requestTyp);

    void delete(Collection<WBCIVorabstimmungFax> ids);

    /**
     * Ermittelt die TNB-Kennung (z.B. 'D052') zu dem angegebenen Auftrag. Die TNB-Kennung wird ueber die CarrierKennung
     * des technischen Standorts von der Endstelle 'B' des Auftrags ermittelt. <br/>
     *
     * Falls NGN aktiv ist, wird die TNB ueber Taifun ermittelt, falls sie in Hurrican nicht bekannt ist
     *
     * @param auftragId Hurrican Auftrags-ID
     * @return TnbKennung
     */
    String getTnbKennung(Long auftragId);

    /**
     * Ermittelt die "M-net Technologie", die fuer den angegebenen Auftrag verwendet wird. (Es wird der Typ des
     * zugeordneten techn. Standorts der Endstelle B des Auftrags ermittelt.)
     *
     * @param auftragId
     * @return
     */
    Technologie getMnetTechnologie(Long auftragId);

    /**
     * Ermittelt die Endstelle zu einer Auftrags-Id
     *
     * @param auftragId
     * @return
     * @throws FindException
     */
    Endstelle getEndstelle4Auftrag(Long auftragId) throws FindException;

    /**
     * Ermittelt ob es sich beim Kunden des angegeben Taifun Auftrags um einen Geschaeftskunden {@link KundenTyp#GK}
     * oder einen Privatkunden {@link KundenTyp#PK} handelt.
     *
     * @param billingOrderNoOrig Taifun-Auftrags-Nr.
     * @return {@link KundenTyp}
     */
    KundenTyp getKundenTyp(Long billingOrderNoOrig) throws FindException;

    /**
     * Ermittelt alle 'relevanten' Hurrican Auftrag IDs zu dem angegebenen Billing-Auftraegen gesucht. <br> <br> Ein
     * Hurrican Auftrag wird dann als 'relevant' angesehen, wenn folgende Bedingungen erfuellt sind:
     * <p/>
     * <pre>
     *   * grundsaetzliche Bedinungen:
     *       * Hurrican Auftrag ist NICHT storniert/abgesagt (status 1150 bzw. 3400)
     *
     *   * auswerten der Auftragsbeziehnung Taifun/Hurrican:
     *       * nur ein Hurrican-Auftrag zum Taifun Auftrag --> dieser ist der relevante Auftrag
     *       * mehrere Hurrican-Auftraege zum Taifun Auftrag:
     *           * es gibt aktive (status<9800) Hurrican-Auftraege  -->  aktive Auftraege sind relevant
     *           * es gibt nur gekuendigte (status=9800) Auftraege  -->  gekuendigte Auftraege sind relevant
     * </pre>
     *
     * @param billingOrderNoOrigs Billing-Auftrags-Nummern
     * @return Liste mit den Hurrican Auftrags-IDs, die fuer WBCI-Operationen als 'relevante' Auftraege angesehen
     * werden.
     */
    Set<Long> getWbciRelevantHurricanOrderNos(@Nullable Set<Long> billingOrderNoOrigs);

    /**
     * Ermittelt in der selben Art und Weise wie {@link #getWbciRelevantHurricanOrderNos(Set)}} all im WBCI-Kontext
     * relevanten Hurrican-Auftrags-Nr. Zusaetzlich zum Billing-Haupt-Aufrag werden auch die zusaetzliche zugeordneten
     * Billing-Aufraege betrachtet.
     *
     * @param billingOrderNoOrig            Billing-Auftrag-Nummer (Haupt-Auftrag)
     * @param nonBillableBillingOderNoOrigs {@link Set} an zustaezlich zugeordneten Billing-Aufrags-Nummern (Aufraege
     *                                      mit gleicher Rufnummer und Flag 'NON_BILLABLE')
     * @return Liste mit den Hurrican Auftrags-IDs, die fuer WBCI-Operationen als 'relevante' Auftraege angesehen
     * werden.
     */
    Set<Long> getWbciRelevantHurricanOrderNos(@NotNull Long billingOrderNoOrig, @Nullable Set<Long> nonBillableBillingOderNoOrigs);

    /**
     * Ermittelt alle relevanten Hurrican Auftraege zu den angegebenen Billing-Auftraegen und sucht den Hurrican
     * Auftrag, bei dem die WITA VtrNr mit der angegebenen {@code witaVtrNr} ueberein simmt.
     *
     * @param witaVtrNr
     * @param billingOrderNoOrig
     * @param nonBillableBillingOderNoOrigs
     * @return Hurrican Auftrags-Id mit der angegebenen WITA VtrNr oder {@code null} falls nicht gefunden.
     */
    Long getHurricanOrderIdForWitaVtrNrAndCurrentVA(
            @NotNull String witaVtrNr,
            @NotNull Long billingOrderNoOrig,
            @Nullable Set<Long> nonBillableBillingOderNoOrigs);

    /**
     * Ermittelt anhand der angegebenen Billing-Auftrags-Nummern alle relevanten Hurrican-Auftraege und fuegt die
     * folgenden Informationen zu einem Set an {@link TechnischeRessource}n zusammen: <ul> <li>Relevante
     * WITA-Vertragsnummern oder WITA-Line-Id - Ermittelt aus den Carrierbestellungsdaten bzw. der {@link
     * de.augustakom.hurrican.model.cc.VerbindungsBezeichnung} der internen Auftraege.</li> <li>Die TNB-Kennung (z.B.
     * 'D052') - Ermittelt ueber die CarrierKennung des technischen Standorts von der Endstelle 'B' des Auftrags
     * ermittelt.</li> </ul>
     *
     * @param billingOrderNoOrig            Billing-Auftrag-Nummer (Haupt-Auftrag)
     * @param nonBillableBillingOderNoOrigs {@link Set} an zustaezlich zugeordneten Billing-Aufrags-Nummern (Aufraege
     *                                      mit gleicher Rufnummer und Flag 'NON_BILLABLE')
     * @return a {@link Set} of {@link TechnischeRessource}n
     */
    Set<TechnischeRessource> getTechnischeRessourcen(@NotNull Long billingOrderNoOrig, @Nullable Set<Long> nonBillableBillingOderNoOrigs) throws WbciServiceException;

    /**
     * Ermittelt den naechsten Sequenzwert fuer den angegebenen RequestTyp.
     *
     * @return den naechsten aus der DB generierten Sequenzwert als String im Format "%08d"
     */
    String getNextSequence4RequestType(RequestTyp requestTyp);

    /**
     * Ermittelt die naechste VorabstimmungsId fuer den angegebenen RequestTyp.
     *
     * @return Eine einzigartige VorabstimmungsId
     */
    String getNextPreAgreementId(RequestTyp requestTyp);

    /**
     * Ermittelt die naechste Vorabstimmungs-ID fuer den angegebenen RequestTyp fuer Fax Abstimmung.
     * Bedeutet, dass statt dem HURRICAN Praefix 'H' der Praefix 'F' benutzt wird.
     *
     * @param requestTyp Geschaeftfall fuer den ID generiert wird V, S oder T
     * @return eine Fax Vorabstimmungs-ID
     */
    String getNextPreAgreementIdFax(RequestTyp requestTyp);

    /**
     * Ermittelt alle Rufnummern, die zu MNet portiert werden koennen. Diese werden innerhalb vom WBCI-Wizard angezeigt
     * werden. Mobilrufnummern werden ausgefiltert, weil WBCI ausschliesslich fuer Festnetznummer relevant ist.
     *
     * @param auftragNoOrig Billing-Auftrag-Nummer
     * @return Liste von DTOs
     */
    List<RufnummerPortierungSelection> getRufnummerPortierungList(Long auftragNoOrig);

    /**
     * Liefert zu einem Auftrag die Anschluss-Adresse
     *
     * @param auftragNoOrig Auftragsnummer
     * @return Adress-Objekt der Anschlussadresse
     */
    Adresse getAnschlussAdresse(Long auftragNoOrig);

    /**
     * Liefert alle im System vorhandenen Vorabstimmungen. Je nach {@link CarrierRole} von M-Net werden entweder die
     * aufnehmende ( {@link CarrierRole#AUFNEHMEND}) oder die abgebende ({@link CarrierRole#ABGEBEND}) Vorabstimmungen
     * zurueck geliefert.
     *
     * @param mnetCarrierRole {@link CarrierRole} aus Sicht von M-Net
     * @return Ein Liste von {@link PreAgreementVO}-Objekten
     */
    List<PreAgreementVO> findPreAgreements(CarrierRole mnetCarrierRole);

    /**
     * Ermittelt eine bestimmte Vorabstimmung in der angegebenen Rolle. Dies kann z.B. verwendet werden, um in einer GUI
     * nach einem Update nicht immer alle, sondern nur eine bestimmte VA neu zu laden.
     *
     * @param mnetCarrierRole
     * @param singlePreAgreementIdToLoad
     * @return
     */
    List<PreAgreementVO> findPreAgreements(CarrierRole mnetCarrierRole, String singlePreAgreementIdToLoad);

    /**
     * Ermittelt den letzten {@link WbciRequest} mit der angegebenen {@code vorabstimmungsId} und gibt diesen zurueck.
     *
     * @param vorabstimmungsId
     * @return
     */
    WbciRequest findLastWbciRequest(String vorabstimmungsId);

    /**
     * Ermittelt alle {@link WbciRequest} mit der angegebenen {@code vorabstimmungsId} und {@code requestTyp} und gibt
     * sie zurueck.
     *
     * @param vorabstimmungsId
     * @param requestTyp
     * @return
     */
    <T extends WbciRequest> List<T> findWbciRequestByType(String vorabstimmungsId, Class<T> requestTyp);

    /**
     * Ermittelt einen {@link WbciRequest} ueber die Id (PrimaryKey!)
     *
     * @param id
     * @return
     */
    WbciRequest findWbciRequestById(Long id);

    /**
     * Ermittelt alle {@code WbciRequest}s zu der angegebenen {@code vorabstimmungsId} und {@code changeId}.
     *
     * @param vorabstimmungsId
     * @param changeId
     * @return
     */
    WbciRequest findWbciRequestByChangeId(String vorabstimmungsId, String changeId);

    /**
     * Liefert den {@link WbciGeschaeftsfall} mit der angegebenen {@code vorabstimmungsId} zurueck.
     *
     * @param vorabstimmungsId
     * @return
     */
    WbciGeschaeftsfall findWbciGeschaeftsfall(String vorabstimmungsId);

    /**
     * Search for a {@link VorabstimmungsAnfrage} with the assigned vorabstimmungsId. If more than 1 {@link
     * VorabstimmungsAnfrage} have been found, the service will throw a {@link WbciServiceException}.
     *
     * @param vorabstimmungsId matching to {@link VorabstimmungsAnfrage#getVorabstimmungsId()}
     * @return NULL if no {@link VorabstimmungsAnfrage} have been found
     */
    VorabstimmungsAnfrage findVorabstimmungsAnfrage(String vorabstimmungsId);

    /**
     * Liefert alle Taifunauftrag Ids zurueck, die als nicht billing relevant zum WBCI Geschaeftsfall mit der
     * angegebenen {@code vorabstimmungsId} zugeordnet sind.
     *
     * @param vorabstimmungsId
     * @return
     */
    Set<Long> findNonBillingRelevantTaifunAuftragIds(String vorabstimmungsId);

    /**
     * Ermittelt die letzte {@code Meldung} zu der angegebenen Vorabstimmungs-Id.
     *
     * @param vorabstimmungsId
     * @return letzte empfangene {@code Meldung} zu der angegebenen Vorabstimmungs-Id
     */
    Meldung findLastForVaId(String vorabstimmungsId);

    /**
     * Ermittelt die letzte Meldung des angegebenen Typs fuer die Vorabstimmungs-Id
     *
     * @param vorabstimmungsId Vorabstimmungs-Id, zu der eine Meldung gesucht werden soll
     * @param classDef         Typ der gesuchten Meldung (z.B. {@link de.mnet.wbci.model.RueckmeldungVorabstimmung#getClass()}
     * @return letzte Meldung des gesuchten Typs
     */
    <M extends Meldung> M findLastForVaId(String vorabstimmungsId, Class<M> classDef);

    /**
     * Ermittelt alle {@code Meldung}en zu der angegebenen Vorabstimmungs-Id
     *
     * @param vorabstimmungsId
     * @return
     */
    List<Meldung> findMeldungenForVaId(String vorabstimmungsId);

    /**
     * Filtert alle Meldungen heraus, die direkt zu einer Vorabstimmung und nicht(!) zu einer Aenderung (TV) bzw. Storno
     * gehoeren.
     *
     * @param meldungen
     * @return
     */
    List<Meldung> filterMeldungenForVa(@NotNull List<Meldung> meldungen);

    /**
     * Filtert die Liste der {@link Meldung}en nach der angegebenen tvId.
     *
     * @param meldungen
     * @param tvId
     * @return
     */
    List<Meldung> filterMeldungenForTv(@NotNull List<Meldung> meldungen, @NotNull String tvId);

    /**
     * Filtert die Liste der {@link Meldung}en nach der angegebenen stornoId.
     *
     * @param meldungen
     * @param stornoId
     * @return
     */
    List<Meldung> filterMeldungenForStorno(@NotNull List<Meldung> meldungen, @NotNull String stornoId);

    /**
     * Liefert eine Liste von VorabstimmungIds fuer den angegebenen Billing-Auftrag.
     *
     * @param billingOrderNoOrig ID des Billing-Auftrags
     * @return Liste von VorabstimmungIds zu dem Taifun-Auftrag; gibt ein leeres Set zurueck, falls {@code null} als
     * {@code billingOrderId} uebergeben wird.
     */
    Set<String> getPreAgreementIdsByOrderNoOrig(Long billingOrderNoOrig);

    /**
     * Ordnet die angegebene taifunAuftragsId der Vorabsitummungsanfrage mit der angegebenen VorabstimmungsId zu. Eine
     * Taifun AuftragsId kann nur einer eingehende Vorabstimmungsanfrage zugeordnet werden. Falls es bei der angegebenen
     * Vorabstimmungsanfrage es sich um eine ausgehende handelt, dann wird eine Exception geworfen. Ausserdem wird
     * geprueft, ob zu der angegebenen Auftrags-Id auch wirklich ein Auftrag existiert.
     *
     * @param vorabstimmungsId         die VorabstimmungsId der eingehende Vorabstimmungsanfrage
     * @param taifunOrderId            die TaifunAuftragsId, die der Vorabstimmungsanfrage zugeordnet werden muss.
     * @param addCustomerCommunication definiert, ob ein sog. Protokolleintrag fuer die Auftragszuordnung erstellt
     *                                 werden soll. Dies ist nur bei manueller Auftragszuordnung (also aus der GUI
     *                                 heraus) notwendig; bei automatischer Auftragszuordnung erfolgt der
     *                                 Protokolleintrag aus der Route heraus.
     */
    void assignTaifunOrder(@NotNull String vorabstimmungsId, @NotNull Long taifunOrderId,
            boolean addCustomerCommunication) throws FindException;

    /**
     * Returns a Set of {@code VorabstimmungIds} matching the supplied search {@code criteria}.
     *
     * @param criteria the criteria to use when searching. The criteria cannot be null and a billingOrderNo must be
     *                 specified. All other criteria is optional.
     * @return
     */
    Set<String> findVorabstimmungIdsByBillingOrderNoOrig(VorabstimmungIdsByBillingOrderNoCriteria criteria);

    /**
     * Assign wbci geschaeftsfall to user.
     *
     * @param vorabstimmungsId
     * @param user
     */
    void assignTask(@NotNull String vorabstimmungsId, AKUser user);

    /**
     * Release wbci geschaeftsfall from user.
     *
     * @param vorabstimmungsId
     */
    void releaseTask(@NotNull String vorabstimmungsId);

    /**
     * Updates the Geschaeftsfall status to {@link WbciGeschaeftsfallStatus#PASSIVE}, indicating that the geschaeftsfall
     * processing has for the time being been completed (until the geschaeftsfall is re-activated). <br/> When the GF is
     * in state 'NEW_VA' then its NOT possible to set the state to 'PASSIV' as the next legal state is 'COMPLETE'. If
     * the task is released then don't change the status.
     *
     * @param requestId the primary key of the WBCI Request
     */
    void closeProcessing(Long requestId);

    void closeProcessing(WbciRequest wbciRequest);

    /**
     * Liefert alle im System vorhandenen aktiven {@link WbciGeschaeftsfall}, die dem Taifunauftrag mit der angegebenen
     * Id zugeordnet sind.
     *
     * @param billingOrderNoOrig             Taifunauftrag-Id.
     * @param interpretNewVaStatusesAsActive wenn true, wird {@link WbciGeschaeftsfallStatus#NEW_VA} und {@link
     *                                       WbciGeschaeftsfallStatus#NEW_VA_EXPIRED} als aktiv interpretiert.
     * @return Ein Liste von {@link WbciGeschaeftsfall}-Objekten
     */
    List<WbciGeschaeftsfall> findActiveGfByTaifunId(Long billingOrderNoOrig, boolean interpretNewVaStatusesAsActive);

    /**
     * Liefert alle im System vorhandenen geschlossenen {@link WbciGeschaeftsfall}, die dem Taifunauftrag mit der
     * angegebenen Id zugeordnet sind.
     *
     * @param billingOrderNoOrig Taifunauftrag-Id.
     * @return Ein Liste von {@link WbciGeschaeftsfall}-Objekten
     */
    List<WbciGeschaeftsfall> findCompleteGfByTaifunId(Long billingOrderNoOrig);

    /**
     * Liefert alle im System vorhandenen {@link WbciGeschaeftsfall}, die dem Taifunauftrag mit der angegebenen Id
     * zugeordnet sind.
     *
     * @param billingOrderNoOrig Taifunauftrag-Id.
     * @return Ein Liste von {@link WbciGeschaeftsfall}-Objekten
     */
    List<WbciGeschaeftsfall> findAllGfByTaifunId(Long billingOrderNoOrig);

    /**
     * Saves new comment to wbci geschaeftsfall. Appends comment to existing comments and automatically adds user login
     * name and timestamp to comment data.
     *
     * @param vorabstimmungsId
     * @param comment
     * @param user
     * @return
     */
    String saveComment(@NotNull String vorabstimmungsId, String comment, AKUser user);

    /**
     * Ermittelt die bisherigen Bemerkungen zu der angegebenen Vorabstimmung und ergaenzt diese um {@code comment}
     *
     * @param vorabstimmungsId
     * @param comment
     * @param user
     * @return
     */
    String addComment(@NotNull String vorabstimmungsId, String comment, AKUser user);

    /**
     * Returns <b>TRUE</b> if: <ol> <li>An AKM-TR ({@link de.mnet.wbci.model.UebernahmeRessourceMeldung}) exists for the
     * supplied {@code vorabstimmungsId} and</li> <li>SichererHafen ({@link de.mnet.wbci.model.UebernahmeRessourceMeldung#isSichererHafen()})
     * is set to true</li> </ol> Otherwise false is returned.
     * <p/>
     * Note: If more than one AKM-TR is found matching the vorabstimmungsId then the most recent AKM-TR is considered.
     *
     * @param vorabstimmungsId
     * @return
     */
    Boolean isSichererHafenRequested(String vorabstimmungsId);

    /**
     * Returns <b>TRUE</b> if: <ol> <li>An AKM-TR ({@link de.mnet.wbci.model.UebernahmeRessourceMeldung}) exists for the
     * supplied {@code vorabstimmungsId} and</li> <li>Resourceuebernahme ({@link de.mnet.wbci.model.UebernahmeRessourceMeldung#isUebernahme()})
     * is set to true</li> </ol> Otherwise false is returned.
     * <p/>
     * Note: If more than one AKM-TR is found matching the vorabstimmungsId then the most recent AKM-TR is considered.
     *
     * @param vorabstimmungsId
     * @return
     */
    Boolean isResourceUebernahmeRequested(String vorabstimmungsId);

    UebernahmeRessourceMeldung getLastUebernahmeRessourceMeldung(String vorabstimmungsId);

    /**
     * Ermittelt die Rufnummern zu dem Taifun-Auftrag. <br/> Mobilrufnummern werden ausgefiltert, weil WBCI
     * ausschliesslich fuer Festnetznummer relevant ist. <br/>
     * <p/>
     * <pre>
     *   <b>Auftrag noch aktiv:</b>
     *        * es werden alle aktiven Rufnummern des Auftrags ermittelt
     *
     *   <b>Auftrag bereits gekuendigt:</b>
     *        * Ermittlung der Rufnummern, die zum Kuendigungsdatum aktiv waren
     *        ** Filterung auf Rufnummern, die als PORT_MODE den Wert 'RUECKFALL' oder 'DEAKTIVIERUNG'
     *           besitzen
     * </pre>
     *
     * @param billingOrderNoOrig die Taifun-Auftragsnummer, zu der die Rufnummern ermittelt werden sollen
     * @return Collection mit den Rufnummern, die dem Filter entsprechen
     */
    Collection<Rufnummer> getRNs(Long billingOrderNoOrig);

    /**
     * Returns all preagreements that are missing a corresponding WITA ABM-PV Meldung. Typically the ABM-PV Meldung
     * should be received by the donating carrier (M-NET) shortly after the AKM-TR Meldung has been received.
     *
     * @return The preagreements with overdue ABM-PV notifications
     */
    List<OverdueAbmPvVO> findPreagreementsWithOverdueAbmPv();

    /**
     * Finds original {@link WbciGeschaeftsfall} by the recursive chain of change ids: {@link
     * WbciGeschaeftsfall#strAenVorabstimmungsId}
     *
     * @param vorabstimmungsId vorabstimmungs id
     * @return geschaeftfall
     */
    WbciGeschaeftsfall findOriginalWbciGeschaeftsfall(String vorabstimmungsId);
}
