package de.mnet.hurrican.scheduler.service.impl;

import static de.mnet.wita.config.WitaConstants.*;

import java.text.*;
import java.time.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.Function2;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.SmsConfig;
import de.augustakom.hurrican.model.cc.SmsMontage;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.OEService;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.customer.messagedeliveryservice.v1.Email;
import de.mnet.esb.cdm.customer.messagedeliveryservice.v1.MessageDeliveryService;
import de.mnet.esb.cdm.customer.messagedeliveryservice.v1.ProtocolEntry;
import de.mnet.esb.cdm.customer.messagedeliveryservice.v1.ShortMessage;
import de.mnet.hurrican.scheduler.service.CustomerNotificationService;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.auftrag.Kundenwunschtermin;
import de.mnet.wita.message.common.EmailStatus;
import de.mnet.wita.message.common.SmsStatus;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.MwfEntityService;


public class CustomerNotificationServiceImpl implements CustomerNotificationService {
    private static final String CONTENT_TYPE = "Content-type: text/plain; charset=UTF-8";
    private static final String EMAIL_SUBJECT = "Terminankündigung";
    private static final String DEFAULT_MELDUNGSCODE = "0";

    enum MeldungscodeInDb {
        SMS_CONFIG_MELDUNGSCODE_6012(MELDUNGSCODE_6012),
        SMS_CONFIG_MELDUNGSCODE_DEFAULT(DEFAULT_MELDUNGSCODE);

        private String meldungsCodeDb;

        MeldungscodeInDb(String meldungsCodeDb) {

            this.meldungsCodeDb = meldungsCodeDb;
        }

        public String getMeldungsCodeDb() {
            return meldungsCodeDb;
        }
    }

    MessageDeliveryService messageDeliveryService;
    CarrierElTALService carrierElTALService;
    CCAuftragService ccAuftragService;
    QueryCCService queryCCService;
    MwfEntityService mwfEntityService;
    AnsprechpartnerService ansprechpartnerService;
    RegistryService registryService;
    OEService oeService;

    static final Function2<String, Date, String> REPLACE_SCHALTTERMIN = new Function2<String, Date, String>() {
        private DateFormat getDateFormat() {
            return new SimpleDateFormat("dd.MM.yyyy");
        }

        @Override
        public String apply(@Nonnull final Date schalttermin, @Nonnull final String template) throws Exception {
            return template.replaceAll("@schalttermin@", getDateFormat().format(schalttermin));
        }
    };

    static final Function2<String, String, String> REPLACE_ZEITFENSTER =
            new Function2<String, String, String>() {
                @Override
                public String apply(@Nonnull final String template, @Nonnull final String zeitfenster) {
                    return template.replaceAll("@zeitfenster@", zeitfenster);
                }
            };

    public static final Function<Meldung<?>, LocalDateTime> GET_VERSANDZEIT = new Function<Meldung<?>, LocalDateTime>() {
        @Override
        public LocalDateTime apply(Meldung<?> in) {
            return DateConverterUtils.asLocalDateTime(in.getVersandZeitstempel());
        }
    };

    final String processTemplate(@Nonnull final String template, @CheckForNull final Date schalttermin, @Nonnull final Kundenwunschtermin.Zeitfenster zeitfenster) {
        try {
            return REPLACE_ZEITFENSTER.apply(
                    (schalttermin == null)
                            ? template
                            : REPLACE_SCHALTTERMIN.apply(schalttermin, template),
                    zeitfenster.getShortDescription()
            );
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    final String processEmailTemplate(
            @Nonnull final String template,
            @CheckForNull final Date schalttermin,
            @Nonnull final Kundenwunschtermin.Zeitfenster zeitfenster,
            final String produkt,
            @CheckForNull final String taifunNo) {
        try {
            String dateReplaced = REPLACE_ZEITFENSTER.apply(
                    (schalttermin == null)
                            ? template
                            : REPLACE_SCHALTTERMIN.apply(schalttermin, template),
                    zeitfenster.getShortDescription());

            dateReplaced = dateReplaced.replaceAll("@produkt@", (produkt != null) ? produkt : "");
            return dateReplaced;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ListMultimap<String, Meldung<?>> groupByExterneAuftragId(@Nonnull List<Meldung<?>> listIn) {
        // Meldungen nach Versandzeit sortieren - juengste zuerst
        List<Meldung<?>> ordered = Ordering.natural().onResultOf(GET_VERSANDZEIT).reverse().sortedCopy(listIn);

        // Meldungen nach ext. Auftragsnummer gruppieren - Reihenfolge der Vorsortierung bleibt dabei erhalten
        ArrayListMultimap<String, Meldung<?>> groups = ArrayListMultimap.create();
        for (Meldung meldung : ordered) {
            groups.put(meldung.getExterneAuftragsnummer(), meldung);
        }
        return groups;
    }

    public List<Meldung<?>> findOffeneMeldungenSms() {
        return mwfEntityService.findMeldungenForSmsVersand();
    }

    public List<Meldung<?>> findOffeneMeldungenEmail() {
        return mwfEntityService.findMeldungenForEmailVersand();
    }

    // Templates fuer Meldung ermitteln
    SmsConfig getTemplate(Meldung<?> meldung, CBVorgang cbVorgang) throws Exception {
        boolean montage = BooleanUtils.isTrue(cbVorgang.getReturnKundeVorOrt());
        MeldungscodeInDb meldungsCode = getDBMeldungscodeForTam(meldung);
        SmsConfig example = new SmsConfig();
        example.setSchnittstelle("WITA");
        example.setGeschaeftsfallTyp(meldung.getGeschaeftsfallTyp().name());
        example.setMeldungTyp(meldung.getMeldungsTyp().toString());
        example.setAenderungsKennzeichen(meldung.getAenderungsKennzeichen().name());
        example.setMontage(montage ? SmsMontage.YES : SmsMontage.NO);
        example.setMeldungsCode(meldungsCode.getMeldungsCodeDb());

        List<SmsConfig> templates = queryCCService.findByExample(example, SmsConfig.class);

        if (templates.size() == 1) {
            return templates.get(0);
        }

        example.setMontage(SmsMontage.IGNORE);
        templates = queryCCService.findByExample(example, SmsConfig.class);

        if (templates.size() == 1) {
            return templates.get(0);
        }
        else {
            return null;
        }
    }

    /**
     * Die TA-TAM mit dem Meldungscode '6012' besitzt einen eigenen Text <br/>
     * Hier wird ermittelt, ob die Meldung eine TAM ist und den Meldungscode '6012' enthaelt <br/>
     * In diesem Fall wird der Meldungscode '6012' zurueckgegeben, andernfalls '0' als Default-Wert
     *
     * @param meldung Die Meldung, die geprueft wird
     * @return Der Meldungscode, der zum Ermitteln des Textes in der DB benoetigt wird <br/> Der Meldungscode ist
     * entweder '6012' im TA-Fall oder '0' als Defaultwert
     */
    MeldungscodeInDb getDBMeldungscodeForTam(final Meldung<?> meldung) {
        final Optional<MeldungsPosition> firstMeldungsPostion = getFirstMeldungsPostion(meldung);
        if (MeldungsType.TAM.equals(meldung.getMeldungsTyp())
                && firstMeldungsPostion.isPresent()
                && firstMeldungsPostion.get().getMeldungsCode().equals(MELDUNGSCODE_6012)) {
            return MeldungscodeInDb.SMS_CONFIG_MELDUNGSCODE_6012;
        }
        else {
            return MeldungscodeInDb.SMS_CONFIG_MELDUNGSCODE_DEFAULT;
        }
    }

    /**
     * Hilfsmethode um die erste Meldungsposition zu bestimmen. <br/>
     *
     * @return Die erste Meldungsposition oder falls es keine Meldungsposition gibt, ein leeres Optional
     */
    private Optional<MeldungsPosition> getFirstMeldungsPostion(Meldung<?> meldung) {
        if (meldung.getMeldungsPositionen() != null && meldung.getMeldungsPositionen().size() == 1) {
            return Optional.of((MeldungsPosition) CollectionUtils.get(meldung.getMeldungsPositionen(), 0));
        }
        else {
            return Optional.empty();
        }
    }

    /**
     * SMS-Reason fuer WITA Meldungen bestimmen.
     *
     * @param meldung WITA-Meldung
     * @return siehe https://intranet.m-net.de/display/MAITSerivceRepository/CustomerService+V1
     */
    String getSmsWitaReason(Meldung<?> meldung) {
        if (meldung.getMeldungsTyp().equals(MeldungsType.RUEM_PV)) {
            return "WITA-Rückmeldung";
        }
        else {
            return "WITA-Meldung";
        }
    }

    /**
     * SMS versenden und im CMS zum Auftrag (contractId) und Kunden (customerId) protokollieren lassen.
     */
    private void sendeSms(Meldung<?> meldung, String smsRufnummer, Long kundeNo, @Nullable Long taifunAuftragNr, String message) throws FindException {
        String from = registryService.getStringValue(RegistryService.REGID_SMS_FROM);
        ProtocolEntry protocol = new ProtocolEntry();
        protocol.setCustomerId(Long.toString(kundeNo));
        protocol.setReason(getSmsWitaReason(meldung));
        if (taifunAuftragNr != null) {
            ProtocolEntry.Context context = new ProtocolEntry.Context();
            context.setContractId(Long.toString(taifunAuftragNr));
            protocol.setContext(context);
        }
        ShortMessage sms = new ShortMessage();
        sms.setFrom(from);
        sms.setTo(smsRufnummer);
        sms.setText(message);
        messageDeliveryService.sendShortMessage(sms, protocol);
    }

    private void sendEmail(Meldung<?> meldung, Long kundeNo, String emailAddress, @Nullable Long taifunAuftragNr, String message, String subject) throws FindException {
        String from = registryService.getStringValue(RegistryService.REGID_CUSTOMER_EMAIL_FROM);
        ProtocolEntry protocol = new ProtocolEntry();
        protocol.setCustomerId(Long.toString(kundeNo));
        protocol.setReason(getSmsWitaReason(meldung));
        if (taifunAuftragNr != null) {
            ProtocolEntry.Context context = new ProtocolEntry.Context();
            context.setContractId(Long.toString(taifunAuftragNr));
            protocol.setContext(context);
        }

        Email email = new Email();
        email.setFrom(from);
        email.getTo().add(emailAddress);
        email.setSubject(StringUtils.isNotEmpty(subject) ? subject : EMAIL_SUBJECT);
        Email.Body body = new Email.Body();
        body.setContent(message);
        body.setContentType(CONTENT_TYPE);
        email.setBody(body);

        messageDeliveryService.sendEmail(email, protocol);
    }

    public void sendEmailForGroup(@Nonnull List<Meldung<?>> meldungen) throws Exception {

        if (meldungen.size() < 1)
            return;

        Meldung<?> meldung = meldungen.get(0);

        StatusSetter statusSetter = new StatusSetter() {
            @Override
            public void setUngueltig() {
                meldung.setEmailStatus(EmailStatus.UNGUELTIG);
            }

            @Override
            public void setVeraltet() {
                meldung.setEmailStatus(EmailStatus.VERALTET);
            }

            @Override
            public void setKeineConfig() {
                meldung.setEmailStatus(EmailStatus.KEINE_CONFIG);
            }

            @Override
            public void setToInvalid(Meldung<?> meldungInput) {
                if (meldungInput.isStatusOpenForMail())
                    meldungInput.setEmailStatus(EmailStatus.UNGUELTIG);
            }

            @Override
            public void createData(
                    AuftragDaten auftragDaten,
                    Ansprechpartner ansprechpartner,
                    Auftrag auftrag,
                    CBVorgang cbVorgang,
                    SmsConfig config
            ) throws FindException {

                // nicht Flag Sms und Mail versenden gesetzt -> UNERWUENSCHT
                if (!auftragDaten.isAutoSmsAndMailVersand()){
                    meldung.setEmailStatus(EmailStatus.UNERWUENSCHT);
                    return;
                }

                String emailAddress = (ansprechpartner == null || ansprechpartner.getAddress() == null) ? null : ansprechpartner.getAddress().getEmail();
                if (StringUtils.isEmpty(emailAddress) || !auftragDaten.isAutoSmsAndMailVersand()) {
                    meldung.setEmailStatus(EmailStatus.KEINE_EMAIL);
                    return;
                }

                Long kundeNo = auftrag.getKundeNo();
                Long taifunAuftragNr = auftragDaten.getAuftragNoOrig();

                String productName = oeService.findProduktName4Auftrag(auftragDaten.getAuftragNoOrig());

                String message = processEmailTemplate(
                        config.getEmailText(),
                        cbVorgang.getReturnRealDate(),
                        determineZeitfenster(cbVorgang),
                        productName,
                        Long.toString(taifunAuftragNr));

                sendEmail(meldung, kundeNo, emailAddress, taifunAuftragNr, message, config.getEmailBetreff());
                meldung.setEmailStatus(EmailStatus.GESENDET);
            }
        };

        sendData(statusSetter, meldungen);
    }

    public void sendSmsForGroup(@Nonnull List<Meldung<?>> meldungen) throws Exception {

        if (meldungen.size() < 1)
            return;

        final Meldung<?> meldung = meldungen.get(0);

        StatusSetter statusSetter = new StatusSetter() {
            @Override
            public void setUngueltig() {
                meldung.setSmsStatus(SmsStatus.UNGUELTIG);
            }

            @Override
            public void setVeraltet() {
                meldung.setSmsStatus(SmsStatus.VERALTET);
            }

            @Override
            public void setKeineConfig() {
                meldung.setSmsStatus(SmsStatus.KEINE_CONFIG);
            }

            @Override
            public void createData(
                    AuftragDaten auftragDaten,
                    Ansprechpartner ansprechpartner,
                    Auftrag auftrag,
                    CBVorgang cbVorgang,
                    SmsConfig config
            ) throws FindException {

                // nicht Flag Sms und Mail versenden gesetzt -> UNERWUENSCHT
                if (!auftragDaten.isAutoSmsAndMailVersand()) {
                    meldung.setSmsStatus(SmsStatus.UNERWUENSCHT);
                    return;
                }

                // keine Rufnr. (Endst. Ansprechp.) -> KEINE_RN
                String smsRufnummer = getSmsRufnummer(ansprechpartner);
                if (smsRufnummer == null) {
                    meldung.setSmsStatus(SmsStatus.KEINE_RN);
                    return;
                }

                Long kundeNo = auftrag.getKundeNo();
                Long taifunAuftragNr = auftragDaten.getAuftragNoOrig();

                String message = processTemplate(config.getTemplateText(), cbVorgang.getReturnRealDate(), determineZeitfenster(cbVorgang));
                sendeSms(meldung, smsRufnummer, kundeNo, taifunAuftragNr, message);
                meldung.setSmsStatus(SmsStatus.GESENDET);
            }

            @Override
            public void setToInvalid(Meldung<?> meldungInput) {
                if (meldungInput.isStatusOpenForSMS())
                    meldungInput.setSmsStatus(SmsStatus.UNGUELTIG);
            }
        };

        sendData(statusSetter, meldungen);
    }

    protected void sendData(StatusSetter statusSetter, @Nonnull List<Meldung<?>> meldungen) throws Exception {
        Meldung<?> meldung = meldungen.get(0);

        processStatus(meldung, statusSetter);

        setOtherToInvalid(meldungen, statusSetter);
    }

    Kundenwunschtermin.Zeitfenster determineZeitfenster(CBVorgang cbVorgang) {
        // Standardzeitfenster setzen
        Kundenwunschtermin.Zeitfenster zeitfenster = Kundenwunschtermin.Zeitfenster.SLOT_8;
        if (cbVorgang instanceof WitaCBVorgang) {
            WitaCBVorgang witaCBVorgang = (WitaCBVorgang) cbVorgang;
            final Kundenwunschtermin.Zeitfenster witaZeitfenster = witaCBVorgang.getRealisierungsZeitfenster();
            if (witaZeitfenster != null) {
                zeitfenster = witaZeitfenster;
            }
        }
        return zeitfenster;
    }

    private void processStatus(final Meldung<?> meldung, final StatusSetter statusSetter) throws Exception {
        CBVorgang cbVorgang = carrierElTALService.findCBVorgangByCarrierRefNr(meldung.getExterneAuftragsnummer());

        if (cbVorgang == null) {
            statusSetter.setKeineConfig();
            return;
        }

        AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragId(cbVorgang.getAuftragId());

        if (auftragDaten == null) {
            statusSetter.setUngueltig();
            return;
        }

        if (auftragDaten.isCancellatedWithNonRenewal()) {
            // Auftrag storniert || gekuendigt -> FALSCHER_AUFTRAGSTATUS
            meldung.setSmsStatus(SmsStatus.FALSCHER_AUFTRAGSTATUS);
            return;
        }

        SmsConfig template = getTemplate(meldung, cbVorgang);

        if (template == null) {
            statusSetter.setKeineConfig();
            return;
        }

        // Schalttermin/In-Betriebnahmedatum in der Vergangenheit -> VERALTET
        if (auftragDaten.isStartOperationInPast()
                || cbVorgang.isTurnOnInPast()) {
            statusSetter.setVeraltet();
            return;
        }

        statusSetter.createData(
                auftragDaten,
                ansprechpartnerService
                        .findPreferredAnsprechpartner(Ansprechpartner.Typ.ENDSTELLE_B, cbVorgang.getAuftragId()),
                ccAuftragService.findAuftragById(auftragDaten.getAuftragId()),
                cbVorgang,
                template);
    }

    protected void setOtherToInvalid(@Nonnull List<Meldung<?>> meldungen, StatusSetter statusSetter) {
        List<Meldung<?>> modified = Lists.newArrayList();
        for (Meldung einzelMeldung : meldungen) {
            // alle noch offenen Meldungen mit Status 'ungueltig' versehen
            statusSetter.setToInvalid(einzelMeldung);
            // SmsStatus speichern
            modified.add(mwfEntityService.store(einzelMeldung));
        }
    }

    String getSmsRufnummer(Ansprechpartner ansprechpartner) {
        if (ansprechpartner == null) {
            return null;
        }
        String rufnummer = normiereRufnummer(ansprechpartner.getAddress().getHandy());
        if (rufnummer != null) {
            return rufnummer;
        }
        // Fallback auf Mobilenummern im Feld "telefon"
        rufnummer = normiereRufnummer(ansprechpartner.getAddress().getTelefon());
        if (rufnummer == null || rufnummer.length() <= 3) {
            return null;
        }
        switch (rufnummer.substring(0, 3)) {
            case "015":
            case "016":
            case "017":
                return rufnummer;
            default:
                return null;
        }
    }

    String normiereRufnummer(String rufnummerIn) {
        String rufnummer = rufnummerIn;
        rufnummer = StringUtils.stripToNull(rufnummer);
        if (rufnummer == null) {
            return null;
        }
        // nach 0049 oder +49 darf keine 0 kommen. Fehleingabe hier korrigieren.
        rufnummer = rufnummer.replaceAll("[ /()]", "");
        rufnummer = rufnummer.replaceAll("^00490", "0");
        rufnummer = rufnummer.replaceAll("^0049", "0");
        rufnummer = rufnummer.replaceAll("^\\+490", "0");
        rufnummer = rufnummer.replaceAll("^\\+49", "0");
        return rufnummer;
    }

    public CarrierElTALService getCarrierElTALService() {
        return carrierElTALService;
    }

    public void setCarrierElTALService(CarrierElTALService carrierElTALService) {
        this.carrierElTALService = carrierElTALService;
    }

    public CCAuftragService getCcAuftragService() {
        return ccAuftragService;
    }

    public void setCcAuftragService(CCAuftragService ccAuftragService) {
        this.ccAuftragService = ccAuftragService;
    }

    public MessageDeliveryService getMessageDeliveryService() {
        return messageDeliveryService;
    }

    public void setMessageDeliveryService(MessageDeliveryService messageDeliveryService) {
        this.messageDeliveryService = messageDeliveryService;
    }

    public RegistryService getRegistryService() {
        return registryService;
    }

    public void setRegistryService(RegistryService registryService) {
        this.registryService = registryService;
    }

    public AnsprechpartnerService getAnsprechpartnerService() {
        return ansprechpartnerService;
    }

    public void setAnsprechpartnerService(AnsprechpartnerService ansprechpartnerService) {
        this.ansprechpartnerService = ansprechpartnerService;
    }

    public QueryCCService getQueryCCService() {
        return queryCCService;
    }

    public void setQueryCCService(QueryCCService queryCCService) {
        this.queryCCService = queryCCService;
    }

    public MwfEntityService getMwfEntityService() {
        return mwfEntityService;
    }

    public void setMwfEntityService(MwfEntityService mwfEntityService) {
        this.mwfEntityService = mwfEntityService;
    }

    interface StatusSetter {

        void setUngueltig();

        void setVeraltet();

        void setKeineConfig();

        void createData(AuftragDaten auftragDaten, Ansprechpartner ansprechpartner, Auftrag auftrag, CBVorgang cbVorgang, SmsConfig template) throws FindException;

        void setToInvalid(Meldung<?> meldung);

    }

    public OEService getOeService() {
        return oeService;
    }

    public void setOeService(OEService oeService) {
        this.oeService = oeService;
    }
}
