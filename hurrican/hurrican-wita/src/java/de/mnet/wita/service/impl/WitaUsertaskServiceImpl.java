/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.2011 18:31:00
 */
package de.mnet.wita.service.impl;

import static com.google.common.base.Predicates.*;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.*;
import static de.augustakom.hurrican.model.shared.iface.CCAuftragModel.GET_AUFTRAG_ID;
import static de.mnet.wita.model.WitaCBVorgang.*;
import static org.apache.commons.lang.StringUtils.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.dao.TaskDao;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.exceptions.WitaBpmException;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.IncomingPvMeldung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.message.meldung.attribute.AufnehmenderProvider;
import de.mnet.wita.model.AbgebendeLeitungenUserTask;
import de.mnet.wita.model.AbgebendeLeitungenVorgang;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.model.KueDtUserTask;
import de.mnet.wita.model.TamUserTask;
import de.mnet.wita.model.TamUserTask.TamBearbeitungsStatus;
import de.mnet.wita.model.TamVorgang;
import de.mnet.wita.model.UserTask;
import de.mnet.wita.model.UserTask.UserTaskStatus;
import de.mnet.wita.model.UserTask2AuftragDaten;
import de.mnet.wita.model.UserTaskDetails;
import de.mnet.wita.model.VorabstimmungAbgebend;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaConfigService;
import de.mnet.wita.service.WitaTalOrderService;
import de.mnet.wita.service.WitaUsertaskService;
import de.mnet.wita.service.WitaVorabstimmungService;
import de.mnet.wita.service.WitaWbciServiceFacade;

@CcTxRequired
public class WitaUsertaskServiceImpl implements WitaUsertaskService {

    private static final Logger LOGGER = Logger.getLogger(WitaUsertaskServiceImpl.class);

    @Autowired
    private CCAuftragService auftragService;
    @Autowired
    private PhysikService physikService;
    @Autowired
    private NiederlassungService niederlassungService;
    @Autowired
    private EndstellenService endstellenService;
    @Autowired
    private CarrierService carrierService;
    @Autowired
    private KundenService kundenService;
    @Autowired
    private HVTService hvtService;
    @Autowired
    private WitaVorabstimmungService witaVorabstimmungService;
    @Autowired
    private DateTimeCalculationService dateTimeCalculationService;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private WitaWbciServiceFacade witaWbciServiceFacade;
    @Autowired
    private WitaTalOrderService witaTalOrderService;
    @Autowired
    private WitaConfigService witaConfigService;
    @Autowired
    MwfEntityDao mwfEntityDao;


    @SuppressWarnings("Duplicates")
    @Override
    public List<TamVorgang> findOpenTamTasks() {
        List<TamVorgang> tamVorgaenge = taskDao.findOpenTamTasks();
        if (tamVorgaenge != null) {
            initRestFristInTagen(tamVorgaenge);
            return tamVorgaenge;
        }
        else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<TamVorgang> findOpenTamTasksWithWiedervorlageWithoutTKGTams() {
        List<TamVorgang> tamVorgaenge = findAllOpenTamTasksWithWiedervorlage();
        if (tamVorgaenge != null) {
            for (TamVorgang tamVorgang : tamVorgaenge) {
                tamVorgang.setRestFristInTagen(getRestFristInTagen(tamVorgang));
            }

            List<TamVorgang> tamWithoutTKGTam = tamVorgaenge;
            if (witaConfigService.getDefaultWitaVersion().isGreaterOrEqualThan(WitaCdmVersion.V2)) {
                // TKG-TAM aus der Liste filtern, falls Wita 10 oder groesser aktiv ist
                tamWithoutTKGTam = tamVorgaenge.stream()
                        .filter(t -> !isTKGTam(t))
                        .collect(Collectors.toList());

                if (LOGGER.isDebugEnabled()) {
                    int withoutTKGTam = tamWithoutTKGTam.size();
                    int numOfTams = tamVorgaenge.size();
                    String msg = String.format("TAM: %d,TKG-TAM: %d, result: %d", numOfTams, numOfTams - withoutTKGTam, withoutTKGTam);
                    LOGGER.debug(msg);
                }
            }
            return tamWithoutTKGTam;
        }
        else {
            return Collections.emptyList();
        }
    }


    @Override
    public List<TamVorgang> findOpenTKGTamTasksWithWiedervorlage() {
        List<TamVorgang> tamVorgaenge = findAllOpenTamTasksWithWiedervorlage();
        if (tamVorgaenge != null) {
            return tamVorgaenge.stream()
                    .filter(this::isTKGTam).collect(Collectors.toList());
        }
        else {
            return Collections.emptyList();
        }
    }

    private boolean isTKGTam(TamVorgang tam) {
        WitaCBVorgang cbVorgang = tam.getCbVorgang();
        GeschaeftsfallTyp geschaeftsfallTyp = cbVorgang.getWitaGeschaeftsfallTyp();
        if (LOGGER.isDebugEnabled()) {
            String msg = String.format("TAM: %s, ref-nr: %s, vorabstimmung: %s, montagehinweis: %s",
                    geschaeftsfallTyp, cbVorgang.getCarrierRefNr(),
                    cbVorgang.getVorabstimmungsId(), cbVorgang.getMontagehinweis());
            LOGGER.debug(msg);
        }
        if (geschaeftsfallTyp.equals(GeschaeftsfallTyp.BEREITSTELLUNG)) {
            String montageHinweis = cbVorgang.getMontagehinweis();
            boolean isVorabstimmung = StringUtils.isNotEmpty(cbVorgang.getVorabstimmungsId());
            boolean hasAbwMontageHinweis = montageHinweis != null && montageHinweis.contains(ANBIETERWECHSEL_46TKG);
            return isVorabstimmung || hasAbwMontageHinweis;
        }
        else
            return geschaeftsfallTyp.equals(GeschaeftsfallTyp.PROVIDERWECHSEL)
                    || geschaeftsfallTyp.equals(GeschaeftsfallTyp.VERBUNDLEISTUNG);
    }


    private List<TamVorgang> findAllOpenTamTasksWithWiedervorlage() {
        List<TamVorgang> tamVorgaenge = taskDao.findOpenTamTasksWithWiedervorlage();
        if (tamVorgaenge != null) {
            initRestFristInTagen(tamVorgaenge);
            return tamVorgaenge;
        }
        else {
            return Collections.emptyList();
        }
    }

    private void initRestFristInTagen(final List<TamVorgang> tamVorgaenge) {
        if (tamVorgaenge != null) {
            for (TamVorgang tamVorgang : tamVorgaenge) {
                tamVorgang.setRestFristInTagen(getRestFristInTagen(tamVorgang));
            }
        }
    }

    @Override
    public List<AbgebendeLeitungenVorgang> findOpenAbgebendeLeitungenTasksWithWiedervorlage() throws FindException {
        List<AbgebendeLeitungenVorgang> abgebendeLtgVorgaenge = taskDao.findOpenAbgebendeLeitungenTasksWithWiedervorlage();

        for (AbgebendeLeitungenVorgang abgebendeLtgVorgang : abgebendeLtgVorgaenge) {
            List<AuftragDaten> auftragDaten4CB = findAuftragDatenForUserTask(abgebendeLtgVorgang.getUserTask());
            if (!auftragDaten4CB.isEmpty()) {
                List<AuftragDaten> auftragDaten = Lists.newArrayList(auftragDaten4CB);
                Collections.sort(auftragDaten, Ordering.natural().onResultOf(CCAuftragModel.GET_AUFTRAG_ID));

                AuftragDaten displayAuftragDaten = Iterables.getLast(auftragDaten);
                abgebendeLtgVorgang.setDisplayAuftragDaten(displayAuftragDaten);
                abgebendeLtgVorgang.setAuftragDaten(auftragDaten);

                AuftragTechnik auftragTechnik = auftragService.findAuftragTechnikByAuftragIdTx(displayAuftragDaten
                        .getAuftragId());
                Niederlassung niederlassung = niederlassungService.findNiederlassung(auftragTechnik
                        .getNiederlassungId());
                if (niederlassung != null) {
                    abgebendeLtgVorgang.setNiederlassung(niederlassung.getName());
                }
                if (displayAuftragDaten.isInKuendigung()) {
                    abgebendeLtgVorgang.setOriginalBearbeiter(displayAuftragDaten.getBearbeiter());
                }

                VerbindungsBezeichnung vbz = physikService.findVerbindungsBezeichnungById(auftragTechnik.getVbzId());
                abgebendeLtgVorgang.setVbz(vbz.getVbz());

                final Pair<String, String> lastMeldungsType = mwfEntityDao.findLastMeldungsTyp(abgebendeLtgVorgang.getExterneAuftragsnummer(), abgebendeLtgVorgang.getUserTask().getVertragsNummer());
                if (lastMeldungsType != null) {
                    abgebendeLtgVorgang.setLastMeldungType(MeldungsType.of(lastMeldungsType.getFirst()));
                    if (lastMeldungsType.getSecond() == null) {
                        abgebendeLtgVorgang.setZustimmungProviderWechsel(null);
                    }
                    else {
                        abgebendeLtgVorgang.setZustimmungProviderWechsel("1".equals(lastMeldungsType.getSecond()));
                    }
                }
            }

            // looks for klaerfallbemerkungen and sets it if present
            if (abgebendeLtgVorgang.getUserTask() != null) {
                Set<Long> cbIds = abgebendeLtgVorgang.getAbgebendeLeitungenUserTask().getCbIds();
                String klaerfallBemerkungen = witaTalOrderService.getKlaerfallBemerkungen(cbIds);
                if (isNotEmpty(klaerfallBemerkungen)) {
                    abgebendeLtgVorgang.setKlaerfall(Boolean.TRUE);
                    abgebendeLtgVorgang.setKlaerfallBemerkung(klaerfallBemerkungen);
                }
            }
        }
        return sortOpenAbgebendeLeitungenTasks(abgebendeLtgVorgaenge);
    }

    List<AbgebendeLeitungenVorgang> sortOpenAbgebendeLeitungenTasks(List<AbgebendeLeitungenVorgang> toSort) {

        Ordering<AbgebendeLeitungenVorgang> prioOrdering = Ordering.natural()
                .onResultOf(AbgebendeLeitungenVorgang.GET_PRIO).reverse();
        Ordering<AbgebendeLeitungenVorgang> antwortFristOrdering = Ordering.natural().nullsLast()
                .onResultOf(AbgebendeLeitungenVorgang.GET_ANTWORT_FRIST);
        Ordering<AbgebendeLeitungenVorgang> empfangenOrdering = Ordering.natural().nullsLast()
                .onResultOf(AbgebendeLeitungenVorgang.GET_EMPFANGEN);

        Ordering<AbgebendeLeitungenVorgang> prioAndVorgabeOrdering = prioOrdering.compound(antwortFristOrdering)
                .compound(empfangenOrdering);

        List<AbgebendeLeitungenVorgang> sortedList = Lists.newArrayList(toSort);
        Collections.sort(sortedList, prioAndVorgabeOrdering);
        return sortedList;
    }

    @Override
    public List<AuftragDaten> findAuftragDatenForUserTask(AbgebendeLeitungenUserTask task) throws FindException {
        List<AuftragDaten> auftragDaten = new ArrayList<>();
        for (UserTask2AuftragDaten userTaskAuftragDaten : task.getUserTaskAuftragDaten()) {
            auftragDaten.add(auftragService.findAuftragDatenByAuftragIdTx(userTaskAuftragDaten.getAuftragId()));
        }
        return auftragDaten;
    }

    @Override
    public UserTaskDetails loadUserTaskDetails(TamVorgang tamVorgang) throws FindException {
        return loadUserTaskDetails(tamVorgang.getAuftragId(), tamVorgang.getCbId());
    }

    @Override
    public UserTaskDetails loadUserTaskDetails(AbgebendeLeitungenVorgang abgebendeLeitungVorgang) throws FindException {
        Set<Long> cbIds = abgebendeLeitungVorgang.getAbgebendeLeitungenUserTask().getCbIds();
        Long cbId = (cbIds.size() == 1) ? Iterables.getOnlyElement(cbIds) : null;
        return loadUserTaskDetails(abgebendeLeitungVorgang.getAuftragId(), cbId);
    }

    private UserTaskDetails loadUserTaskDetails(Long auftragId, Long cbId) throws FindException {
        if (auftragId == null) {
            throw new FindException("Die Auftrags-ID ist nicht definiert.\n"
                    + "Die Details zu dem User-Task können daher nicht geladen werden!");
        }

        Auftrag auftrag = auftragService.findAuftragById(auftragId);
        Kunde kunde = kundenService.findKunde(auftrag.getKundeNo());

        Endstelle foundEndstelle = null;
        HVTGruppe hvtGruppe = null;
        if (cbId != null) {
            Carrierbestellung carrierbestellung = carrierService.findCB(cbId);
            List<Endstelle> endstellen = endstellenService.findEndstellen4Auftrag(auftrag.getAuftragId());
            for (Endstelle es : endstellen) {
                if (NumberTools.equal(carrierbestellung.getCb2EsId(), es.getCb2EsId())) {
                    foundEndstelle = es;
                    hvtGruppe = hvtService.findHVTGruppe4Standort(es.getHvtIdStandort());
                }
            }
        }
        return new UserTaskDetails(kunde, foundEndstelle, hvtGruppe);
    }

    int getRestFristInTagen(LocalDateTime to, TerminAnforderungsMeldung tam) {
        return TerminAnforderungsMeldung.TAM_FRIST_TAGE
                - dateTimeCalculationService.getWorkingDaysBetween(DateConverterUtils.asLocalDateTime(tam.getVersandZeitstempel()), to);
    }

    @Override
    public int getRestFristInTagen(TamVorgang tamVorgang) {
        if (tamVorgang.isTv60Sent()) {
            return dateTimeCalculationService.getWorkingDaysBetween(LocalDateTime.now(),
                    DateConverterUtils.asLocalDateTime(tamVorgang.getVorgabeMnet()));
        }
        return getRestFristInTagen(LocalDateTime.now(), tamVorgang.getTam());
    }

    @Override
    public <T extends UserTask> T storeUserTask(T userTask) {
        taskDao.store(userTask);
        taskDao.flushSession();
        return userTask;
    }

    @Override
    public KueDtUserTask createKueDtUserTask(ErledigtMeldung erlm) {
        KueDtUserTask kueDtUserTask = new KueDtUserTask();
        kueDtUserTask.setExterneAuftragsnummer(erlm.getExterneAuftragsnummer());
        kueDtUserTask.setVertragsNummer(erlm.getVertragsNummer());
        kueDtUserTask.setEmpfangsDatum(erlm.getVersandZeitstempel());
        kueDtUserTask.setKuendigungsDatum(erlm.getErledigungstermin());
        kueDtUserTask.getUserTaskAuftragDaten().addAll(createUserTask2AuftragDatenForTask(kueDtUserTask));
        taskDao.store(kueDtUserTask);
        return kueDtUserTask;
    }

    @Override
    public <T extends Meldung<?> & IncomingPvMeldung> AkmPvUserTask createAkmPvUserTask(T meldung) {
        AkmPvUserTask akmPvUserTask = new AkmPvUserTask();
        akmPvUserTask.setExterneAuftragsnummer(meldung.getExterneAuftragsnummer());
        akmPvUserTask.setVertragsNummer(meldung.getVertragsNummer());
        akmPvUserTask.setEmpfangsDatum(meldung.getVersandZeitstempel());
        akmPvUserTask.setLeitungsBezeichnungFromAkmPv(meldung);
        akmPvUserTask.setProviderAndDateFieldsFromAkmPv(meldung);
        akmPvUserTask.getUserTaskAuftragDaten().addAll(createUserTask2AuftragDatenForTask(akmPvUserTask));
        taskDao.store(akmPvUserTask);
        return akmPvUserTask;
    }

    @Override
    public AkmPvUserTask updateAkmPvUserTask(AnkuendigungsMeldungPv akmPv) {
        AkmPvUserTask existingAkmPvUserTask = findOpenAkmPvUserTaskByVertragNr(akmPv.getVertragsNummer());
        if (existingAkmPvUserTask != null) {
            existingAkmPvUserTask.setEmpfangsDatum(akmPv.getVersandZeitstempel());
            existingAkmPvUserTask.setLeitungsBezeichnungFromAkmPv(akmPv);
            existingAkmPvUserTask.setProviderAndDateFieldsFromAkmPv(akmPv);

            taskDao.store(existingAkmPvUserTask);
            return existingAkmPvUserTask;
        }
        return createAkmPvUserTask(akmPv);
    }

    @Override
    public Map<WitaTaskVariables, Object> getAutomaticAnswerForAkmPv(AnkuendigungsMeldungPv akmPv) {
        Preconditions.checkNotNull(akmPv);
        final String vorabstimmungsId = akmPv.getVorabstimmungsId();
        if (vorabstimmungsId != null) {
            return witaWbciServiceFacade.getAutomaticAnswerForAkmPv(akmPv);
        }
        else {
            return getAutomaticAnswerForAkmPvFromWita(akmPv);
        }
    }

    Map<WitaTaskVariables, Object> getAutomaticAnswerForAkmPvFromWita(AnkuendigungsMeldungPv akmPv) {
        Set<UserTask2AuftragDaten> userTasks2AuftragDaten =
                createUserTask2AuftragDatenForVertragsNummer(akmPv.getVertragsNummer());

        Map<WitaTaskVariables, Object> akmPvVariables = new HashMap<>();
        for (UserTask2AuftragDaten userTask2AuftragDaten : userTasks2AuftragDaten) {
            try {
                VorabstimmungAbgebend vorabstimmungAbgebend = getVorabstimmungAbgebendByAuftragId(
                        userTask2AuftragDaten.getAuftragId(), userTask2AuftragDaten.getCbId());
                if (checkPositiveRuemPvCanBeSent(vorabstimmungAbgebend, akmPv)) {
                    akmPvVariables.put(WitaTaskVariables.RUEM_PV_ANTWORTCODE, RuemPvAntwortCode.OK);
                    break;
                }
            }
            catch (FindException e) {
                LOGGER.error("Fehler bei der Erlmittlung der VorabstimmungAbgebend für " + userTask2AuftragDaten, e);
            }
        }
        return akmPvVariables;
    }

    /**
     * Methode prueft folgende Bedingungen und liefert dann true zurueck:
     * <pre>
     *    1. ist Rueckmeldung positiv in der abgebenden Vorabstimmung angegeben
     *    2. stimmt der angegebene Carrier mit dem der AkmPv ueberein
     *    3. stimmt das angegebene UebernahmeDatum der AkmPv mit dem der Vorabstimmung ueberein.
     * </pre>
     * Alle drei Bedingungen muessen erfuellt sein, ansonsten wird false zurueckgeliefert.
     */
    private boolean checkPositiveRuemPvCanBeSent(VorabstimmungAbgebend vorabstimmungAbgebend,
            AnkuendigungsMeldungPv akmPv) {
        if ((vorabstimmungAbgebend == null) || (akmPv == null)) {
            return false;
        }
        AufnehmenderProvider aufnehmenderProvider = akmPv.getAufnehmenderProvider();
        return VorabstimmungAbgebend.RUECKMELDUNG_POSITIVE.equals(vorabstimmungAbgebend.getRueckmeldung())
                && aufnehmenderProvider.getProvidernameAufnehmend().equals(
                vorabstimmungAbgebend.getCarrier().getWitaProviderNameAufnehmend())
                && aufnehmenderProvider.getUebernahmeDatumGeplant().equals(
                vorabstimmungAbgebend.getAbgestimmterProdiverwechsel());
    }

    VorabstimmungAbgebend getVorabstimmungAbgebendByAuftragId(Long auftragId, Long cbId) throws FindException {
        Endstelle endstelle = endstellenService.findEndstelle4CarrierbestellungAndAuftrag(cbId, auftragId);
        if ((auftragId != null) && (endstelle != null)) {
            return witaVorabstimmungService.findVorabstimmungAbgebend(endstelle.getEndstelleTyp(), auftragId);
        }
        return null;
    }

    Set<UserTask2AuftragDaten> createUserTask2AuftragDatenForTask(AbgebendeLeitungenUserTask task) {
        return createUserTask2AuftragDatenForVertragsNummer(task.getVertragsNummer());
    }

    /**
     * Ermittelt die Carriermeldungen, die die Vertragsnummer aus dem angegebenen Task besitzen. Die Vertragsnummer muss
     * dabei nicht exakt matchen: {@link CarrierService#findCBsByNotExactVertragsnummer}. Als Ergebnis wird ein Set
     * {@link UserTask2AuftragDaten} zurueckgeliefert, das Usertask und AuftragenDaten zugeordnet enthaelt.
     */
    Set<UserTask2AuftragDaten> createUserTask2AuftragDatenForVertragsNummer(String vertragsNummer) {
        List<Carrierbestellung> carrierbestellungen = carrierService.findCBsByNotExactVertragsnummer(vertragsNummer);
        Set<UserTask2AuftragDaten> userTasks2AuftragDatenActive = Sets.newHashSet();
        Set<UserTask2AuftragDaten> userTasks2AuftragDatenNotActive = Sets.newHashSet();
        try {
            // Iteriere ueber Carrierbestellungen und sammle alle Auftraege
            for (Carrierbestellung carrierbestellung : carrierbestellungen) {
                List<AuftragDaten> auftragDaten4CB = carrierService.findAuftragDaten4CB(carrierbestellung.getId());
                for (AuftragDaten auftragDaten : auftragDaten4CB) {
                    UserTask2AuftragDaten userTask2AuftragDaten = new UserTask2AuftragDaten();
                    userTask2AuftragDaten.setAuftragId(auftragDaten.getAuftragId());
                    userTask2AuftragDaten.setCbId(carrierbestellung.getId());

                    if (auftragDaten.isAuftragActive()
                            || (auftragDaten.isInKuendigung() && !auftragDaten.isCancelled())) {
                        userTasks2AuftragDatenActive.add(userTask2AuftragDaten);
                    }
                    else {
                        userTasks2AuftragDatenNotActive.add(userTask2AuftragDaten);
                    }
                }
            }
        }
        catch (FindException e) {
            throw new WitaBaseException("Konnte Auftragsdaten fuer User Task nicht ermitteln", e);
        }
        if (userTasks2AuftragDatenActive.isEmpty()) {
            return userTasks2AuftragDatenNotActive;
        }
        return userTasks2AuftragDatenActive;
    }

    @Override
    public <T extends UserTask> T claimUserTask(T userTask, AKUser user) throws StoreException {
        Preconditions.checkNotNull(userTask, "Kein UserTask ausgewählt.");

        if (user != null) {
            if (userTask.getUserId() != null) {
                throw new StoreException("Der Task ist bereits von dem Benutzer "
                        + ((userTask.getBearbeiter() != null) ? userTask.getBearbeiter().getLoginName()
                        : userTask.getUserId() + "(ID)")
                        + " in Bearbeitung.");
            }
            userTask.setBearbeiter(user);
            userTask.setLetzteAenderung(new Date());
        }
        else { // Task freigeben
            userTask.setBearbeiter(null);
        }

        // TAM-Bearbeitungsstatus aendern
        if (userTask instanceof TamUserTask) {
            TamUserTask tamUserTask = (TamUserTask) userTask;
            if ((tamUserTask.getTamBearbeitungsStatus() == TamBearbeitungsStatus.OFFEN) && (user != null)) {
                tamUserTask.setTamBearbeitungsStatus(TamBearbeitungsStatus.IN_BEARBEITUNG);
            }
            else if ((tamUserTask.getTamBearbeitungsStatus() == TamBearbeitungsStatus.IN_BEARBEITUNG) && (user == null)) {
                tamUserTask.setTamBearbeitungsStatus(TamBearbeitungsStatus.OFFEN);
            }
        }
        return storeUserTask(userTask);
    }

    @Override
    public <T extends UserTask> T closeUserTask(T userTask, AKUser user) throws StoreException {
        Preconditions.checkArgument(userTask.getStatus() == UserTaskStatus.OFFEN, "UserTask ist schon geschlossen!");
        checkUserTaskNotClaimedByOtherUser(userTask, user);
        return closeUserTask(userTask);
    }

    @Override
    public <T extends UserTask> T closeUserTask(T userTask) throws StoreException {
        Preconditions.checkNotNull(userTask, "Kein UserTask ausgewählt.");

        userTask.setStatusLast(userTask.getStatus());
        userTask.setStatus(UserTaskStatus.GESCHLOSSEN);

        userTask.setLetzteAenderung(new Date());
        return taskDao.store(userTask);
    }

    @Override
    public <T extends UserTask> T resetUserTask(T userTask) {
        Preconditions.checkNotNull(userTask, "Kein UserTask ausgewählt.");

        if (userTask.getStatusLast() != null) {
            userTask.setStatus(userTask.getStatusLast());
            userTask.setStatusLast(null);

            userTask.setLetzteAenderung(new Date());
        }
        return taskDao.store(userTask);
    }

    @Override
    public void checkUserTaskNotClaimedByOtherUser(UserTask userTask, AKUser user) {
        if ((userTask == null) || (UserTaskStatus.GESCHLOSSEN == userTask.getStatus())) {
            return;
        }
        if ((userTask.getUserId() != null) && ((user == null) || !userTask.getUserId().equals(user.getId()))) {
            throw new WitaBpmException("Die Aktion kann nicht ausgelöst werden, da der Task dem Benutzer "
                    + ((userTask.getBearbeiter() != null) ? userTask.getBearbeiter().getLoginName()
                    : userTask.getUserId() + "(ID)")
                    + " zugeordnet ist.");
        }
    }

    @Override
    public AkmPvUserTask findAkmPvUserTask(String externeAuftragsnummer) {
        return taskDao.findAkmPvUserTask(externeAuftragsnummer);
    }

    @Override
    public AkmPvUserTask findAkmPvUserTaskByContractId(String vertragsnummer) {
        return taskDao.findAkmPvUserTaskByContractId(vertragsnummer);
    }

    @Override
    public KueDtUserTask findKueDtUserTask(String externeAuftragsnummer) {
        return taskDao.findKueDtTask(externeAuftragsnummer);
    }

    private AkmPvUserTask findOpenAkmPvUserTaskByVertragNr(String vertragsNr) {
        return taskDao.findOpenAkmPvUserTaskByVertragNr(vertragsNr);
    }

    @Override
    public AbgebendeLeitungenUserTask completeUserTask(AbgebendeLeitungenUserTask userTask, AKUser user)
            throws StoreException, FindException {
        Preconditions.checkNotNull(userTask.getEmpfangsDatum(), "EmpfangsDatum darf nicht null sein!");

        userTask.setBenachrichtigung(null);
        Set<Long> cbIds = userTask.getCbIds();
        if (cbIds.size() == 1) {
            updateCarrierbestellung(userTask, Iterables.getOnlyElement(cbIds));
        }
        return closeUserTask(userTask, user);

    }

    @Override
    public void createUserTask2AuftragDatenFor(AbgebendeLeitungenUserTask userTask,
            Pair<Long, Long> auftragIdAndCbId) throws FindException, StoreException {
        Long auftragId = auftragIdAndCbId.getFirst();
        Preconditions.checkNotNull(auftragId, "AuftragId darf nicht null sein!");

        AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(auftragId);
        if (auftragDaten == null) {
            throw new StoreException(String.format("Auftrag mit der ID %s konnte nicht ermittelt werden!", auftragId));
        }
        userTask.getUserTaskAuftragDaten().clear();

        UserTask2AuftragDaten userTask2AuftragDaten = new UserTask2AuftragDaten();
        userTask2AuftragDaten.setAuftragId(auftragId);
        userTask2AuftragDaten.setCbId(auftragIdAndCbId.getSecond());
        userTask.getUserTaskAuftragDaten().add(userTask2AuftragDaten);
        storeUserTask(userTask);
    }

    void updateCarrierbestellung(AbgebendeLeitungenUserTask userTask, Long cbId) throws FindException {
        Carrierbestellung cb = carrierService.findCB(cbId);
        if (cb == null) {
            throw new FindException("Carrierbestellung mit id " + cbId + " nicht gefunden!");
        }
        LocalDate kuendigungsDatum = userTask.getKuendigungsDatum();
        LocalDateTime empfangsDatum = DateConverterUtils.asLocalDateTime(userTask.getEmpfangsDatum());
        List<AuftragDaten> auftragDaten = carrierService.findAuftragDaten4CB(cb.getId());
        if (userTask.kuendigeCarrierbestellung()) {
            if (cb.getKuendigungAnCarrier() == null) {
                // Wird schon bei der Vorabstimmung gesetzt. Nicht mehr ueberschreiben
                cb.setKuendigungAnCarrier(Date.from(empfangsDatum.atZone(ZoneId.systemDefault()).toInstant()));
            }
            cb.setKuendBestaetigungCarrier(Date.from(kuendigungsDatum.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            List<AuftragDaten> aktiveAuftraege = newArrayList(filter(auftragDaten, not(AuftragDaten::isInKuendigung)));
            // Nicht alle gekuendigt -> Muss noch gekuendigt werden
            if (!aktiveAuftraege.isEmpty()) {
                userTask.setBenachrichtigung("Die Leitung wird gekündigt, "
                        + "aber die zugehörigen Aufträge mit den Auftrag Ids "
                        + Joiner.on(", ").join(transform(aktiveAuftraege, GET_AUFTRAG_ID))
                        + " noch nicht. Bitte noch kündigen.");
            }
        }
        else {
            cb.setKuendBestaetigungCarrier(null);
            List<AuftragDaten> gekuendigteAuftraege = newArrayList(filter(auftragDaten, AuftragDaten::isInKuendigung));
            // nicht alle aktiv (nicht gekuendigt) => Muessen wieder aktiviert werden
            if (!gekuendigteAuftraege.isEmpty()) {
                userTask.setBenachrichtigung("Die Leitung wurde nicht gekündigt, "
                        + "aber die Aufträge mit den AuftragIds "
                        + Joiner.on(", ").join(transform(gekuendigteAuftraege, GET_AUFTRAG_ID))
                        + " befinden sich noch in Kündigung. " + "Bitte Kündigung wieder rückgängig machen.");
            }
        }
        carrierService.storeCarrierbestellung(cb);
    }
}
