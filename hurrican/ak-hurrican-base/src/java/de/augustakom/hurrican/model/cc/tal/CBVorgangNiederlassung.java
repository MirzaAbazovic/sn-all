package de.augustakom.hurrican.model.cc.tal;

import java.io.*;
import java.util.*;
import com.google.common.base.Function;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.model.AbstractObservable;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.iface.ICBVorgangStatusModel;

/**
 * DTO zur Uebergabe von CBVorgang-Objekten inklusive der Niederlassung des zugehörigen Auftrags. Wird benutzt von der
 * Uebersicht der offenen TAL-Bestellungen, zum besseren Filtern
 */
public class CBVorgangNiederlassung extends AbstractObservable implements CCAuftragModel, ICBVorgangStatusModel, Serializable {
    private static final long serialVersionUID = -4144682442475583382L;

    private static final Logger LOGGER = Logger.getLogger(CBVorgangNiederlassung.class);

    public static final Function<CBVorgangNiederlassung, Boolean> GET_PRIO =
            new Function<CBVorgangNiederlassung, Boolean>() {

                @Override
                public Boolean apply(CBVorgangNiederlassung from) {
                    return (from.getPrio() != null) ? from.getPrio() : Boolean.FALSE;
                }
            };

    public static final Function<CBVorgangNiederlassung, Date> GET_VORGABE_MNET = new Function<CBVorgangNiederlassung, Date>() {

        @Override
        public Date apply(CBVorgangNiederlassung from) {
            return from.getVorgabeMnet();
        }
    };

    public static final Function<CBVorgangNiederlassung, Long> GET_AUFTRAGSKLAMMER = new Function<CBVorgangNiederlassung, Long>() {

        @Override
        public Long apply(CBVorgangNiederlassung from) {
            return from.getAuftragsKlammer() != null ? from.getAuftragsKlammer() : Long.valueOf(0);
        }
    };

    public static final Function<CBVorgangNiederlassung, Long> GET_CB_VORGANG_ID = new Function<CBVorgangNiederlassung, Long>() {

        @Override
        public Long apply(CBVorgangNiederlassung from) {
            return from.getCbVorgang().getId();
        }
    };

    private final String niederlassung;
    private CBVorgang cbVorgang;
    private Enum<?> aenderungsKennzeichen;
    private Boolean prio;
    private Long auftragsKlammer;
    private String auftragsKlammerSymbol;

    public CBVorgangNiederlassung(CBVorgang cbVorgang, String niederlassung) {
        super();
        this.niederlassung = niederlassung;
        this.cbVorgang = cbVorgang;
        initCBVorgangFields(cbVorgang);
    }

    public void initCBVorgangFields(CBVorgang cbVorgang) {
        // guiber: Umstellung von Reflection copy auf delegate ... spart enorm an Zeit
        this.cbVorgang = cbVorgang;
        if (PropertyUtils.isReadable(cbVorgang, "aenderungsKennzeichen")) {
            // WitaCBVorgang instanz ...
            try {
                aenderungsKennzeichen = (Enum<?>) PropertyUtils.getSimpleProperty(cbVorgang, "aenderungsKennzeichen");
                prio = (Boolean) PropertyUtils.getSimpleProperty(cbVorgang, "prio");
                auftragsKlammer = (Long) PropertyUtils.getSimpleProperty(cbVorgang, "auftragsKlammer");
            }
            catch (Exception e) {
                LOGGER.error(
                        "initCBVorgangFields() - copy properties of cbVorgang failed with message " + e.getMessage(), e);
            }
        }
    }

    public String getNiederlassung() {
        return niederlassung;
    }

    public void setCbVorgang(CBVorgang cbVorgang) {
        this.cbVorgang = cbVorgang;
    }

    public CBVorgang getCbVorgang() {
        return cbVorgang;
    }

    public Enum<?> getAenderungsKennzeichen() {
        return aenderungsKennzeichen;
    }

    public void setAenderungsKennzeichen(Enum<?> aenderungsKennzeichen) {
        this.aenderungsKennzeichen = aenderungsKennzeichen;
    }

    public Boolean getPrio() {
        return prio;
    }

    public void setPrio(Boolean prio) {
        this.prio = prio;
    }

    public Long getAuftragsKlammer() {
        return auftragsKlammer;
    }

    public void setAuftragsKlammer(Long auftragsKlammer) {
        this.auftragsKlammer = auftragsKlammer;
    }

    public String getAuftragsKlammerSymbol() {
        return auftragsKlammerSymbol;
    }

    public void setAuftragsKlammerSymbol(String auftragsKlammerSymbol) {
        this.auftragsKlammerSymbol = auftragsKlammerSymbol;
    }

    public Long getId() {
        return cbVorgang.getId();
    }

    public boolean isAnbieterwechsel() {
        return cbVorgang.isAnbieterwechsel();
    }

    public boolean isKuendigung() {
        return cbVorgang.isKuendigung();
    }

    public boolean isStorno() {
        return cbVorgang.isStorno();
    }

    public boolean hasAnswer() {
        return cbVorgang.hasAnswer();
    }

    public Long getCbId() {
        return cbVorgang.getCbId();
    }

    public Long getCarrierId() {
        return cbVorgang.getCarrierId();
    }

    public Long getTyp() {
        return cbVorgang.getTyp();
    }

    public Boolean getAutomation() {
        return cbVorgang.getAutomation();
    }

    @Override
    public Long getStatus() {
        return cbVorgang.getStatus();
    }

    public String getBezeichnungMnet() {
        return cbVorgang.getBezeichnungMnet();
    }

    public String getReturnLBZ() {
        return cbVorgang.getReturnLBZ();
    }

    public String getReturnVTRNR() {
        return cbVorgang.getReturnVTRNR();
    }

    public String getReturnAQS() {
        return cbVorgang.getReturnAQS();
    }

    public String getReturnLL() {
        return cbVorgang.getReturnLL();
    }

    public Date getReturnRealDate() {
        return cbVorgang.getReturnRealDate();
    }

    public String getReturnBemerkung() {
        return cbVorgang.getReturnBemerkung();
    }

    public Date getVorgabeMnet() {
        return cbVorgang.getVorgabeMnet();
    }

    @Override
    public Boolean getReturnOk() {
        return cbVorgang.getReturnOk();
    }

    public Date getSubmittedAt() {
        return cbVorgang.getSubmittedAt();
    }

    public Date getAnsweredAt() {
        return cbVorgang.getAnsweredAt();
    }

    @Override
    public Long getAuftragId() {
        return cbVorgang.getAuftragId();
    }

    @Override
    public void setAuftragId(Long auftragId) {
        // nothing to do!
    }

    public AKUser getBearbeiter() {
        return cbVorgang.getBearbeiter();
    }

    public String getCarrierBearbeiter() {
        return cbVorgang.getCarrierBearbeiter();
    }

    public String getMontagehinweis() {
        return cbVorgang.getMontagehinweis();
    }

    public Long getUsecaseId() {
        return cbVorgang.getUsecaseId();
    }

    public Long getExmId() {
        return cbVorgang.getExmId();
    }

    public Long getExmRetFehlertyp() {
        return cbVorgang.getExmRetFehlertyp();
    }

    public String getCarrierRefNr() {
        return cbVorgang.getCarrierRefNr();
    }

    public String getStatusBemerkung() {
        return cbVorgang.getStatusBemerkung();
    }

    @Override
    public boolean hasAutomationErrors() {
        return cbVorgang.hasAutomationErrors();
    }

    public boolean isKlaerfallSet() {
        return cbVorgang != null && cbVorgang.isKlaerfall() != null && cbVorgang.isKlaerfall();
    }

}
