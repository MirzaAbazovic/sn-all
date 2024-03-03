package de.mnet.wbci.model;

import static de.mnet.wbci.model.AutomationTask.*;

import java.time.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.springframework.util.CollectionUtils;

import de.augustakom.authentication.model.AKUser;
import de.mnet.wbci.validation.constraints.CheckKundenwunschtermin;
import de.mnet.wbci.validation.constraints.CheckKundenwunschterminIgnoringNextDay;
import de.mnet.wbci.validation.constraints.CheckKundenwunschterminNotInRange;
import de.mnet.wbci.validation.groups.V1Meldung;
import de.mnet.wbci.validation.groups.V1RequestVa;
import de.mnet.wbci.validation.groups.V1RequestVaKueMrn;
import de.mnet.wbci.validation.groups.V1RequestVaKueMrnWarn;
import de.mnet.wbci.validation.groups.V1RequestVaKueOrn;
import de.mnet.wbci.validation.groups.V1RequestVaKueOrnWarn;
import de.mnet.wbci.validation.groups.V1RequestVaRrnp;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_WBCI_GESCHAEFTSFALL")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYP", discriminatorType = DiscriminatorType.STRING)
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_WBCI_GESCHAEFTSFALL_0", allocationSize = 1)
public abstract class WbciGeschaeftsfall extends WbciEntity {

    private static final long serialVersionUID = 6513912261992315437L;

    public static final String VORABSTIMMUNGS_ID = "vorabstimmungsId";
    public static final String AUFTRAG_ID = "auftragId";
    public static final String BILLING_ORDER_NO_ORIG = "billingOrderNoOrig";
    public static final String NON_BILLING_RELEVANT_ORDER_NO_ORIGS = "nonBillingRelevantOrderNoOrigs";
    public static final String STATUS = "status";
    public static final String AUTOMATION_TASKS = "automationTasks";
    public static final String KLAERFALL = "klaerfall";
    public static final String WECHSELTERMIN = "wechseltermin";
    public static final String AUFNEHMENDER_EKP = "aufnehmenderEKP";
    public static final String MNET_TECHNOLOGIE = "mnetTechnologie";

    private WbciVersion wbciVersion;
    private CarrierCode absender;
    private CarrierCode aufnehmenderEKP;
    private CarrierCode abgebenderEKP;
    private String vorabstimmungsId;
    private LocalDate kundenwunschtermin;
    private LocalDate wechseltermin;
    private PersonOderFirma endkunde;
    private List<PersonOderFirma> weitereAnschlussinhaber;
    private Projekt projekt;
    private Long auftragId;
    private WbciGeschaeftsfallStatus status = WbciGeschaeftsfallStatus.ACTIVE;
    private Long billingOrderNoOrig;
    private Set<Long> nonBillingRelevantOrderNoOrigs;
    private Long userId;
    private Long teamId;
    private String userName;
    private Long currentUserId;
    private String currentUserName;
    private Technologie mnetTechnologie;
    private Boolean klaerfall = Boolean.FALSE;
    private List<AutomationTask> automationTasks;

    private String bemerkungen;

    private String strAenVorabstimmungsId;
    private String internalStatus;

    private Boolean automatable;

    @Transient
    public abstract GeschaeftsfallTyp getTyp();

    /**
     * Gibt an, ob der WBCI Geschaeftsfall eine Rufnummer zur Identifikation des Auftrags besitzt.
     *
     * @return
     */
    @Transient
    public boolean hasRufnummerIdentification() {
        return true;
    }

    @Enumerated(EnumType.STRING)
    public WbciVersion getWbciVersion() {
        return wbciVersion;
    }

    public void setWbciVersion(WbciVersion wbciVersion) {
        this.wbciVersion = wbciVersion;
    }

    @Enumerated(EnumType.STRING)
    public CarrierCode getAbsender() {
        return absender;
    }

    public void setAbsender(CarrierCode absender) {
        this.absender = absender;
    }

    @Enumerated(EnumType.STRING)
    public CarrierCode getAufnehmenderEKP() {
        return aufnehmenderEKP;
    }

    public void setAufnehmenderEKP(CarrierCode aufnehmenderEKP) {
        this.aufnehmenderEKP = aufnehmenderEKP;
    }

    @NotNull(groups = V1RequestVaKueOrn.class, message = "der abgebende Carrier darf nicht leer sein.")
    @Enumerated(EnumType.STRING)
    public CarrierCode getAbgebenderEKP() {
        return abgebenderEKP;
    }

    public void setAbgebenderEKP(CarrierCode abgebenderEKP) {
        this.abgebenderEKP = abgebenderEKP;
    }

    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name = "PROJECT_ID", nullable = true)
    @Valid
    public Projekt getProjekt() {
        return projekt;
    }

    public void setProjekt(Projekt projekt) {
        this.projekt = projekt;
    }

    public String getVorabstimmungsId() {
        return vorabstimmungsId;
    }

    public void setVorabstimmungsId(String vorabstimmungsId) {
        this.vorabstimmungsId = vorabstimmungsId;
    }

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @NotNull(groups = { V1RequestVa.class, V1Meldung.class }, message = "darf nicht leer sein")
    @CheckKundenwunschterminIgnoringNextDay(groups = { V1RequestVaRrnp.class }, minimumLeadTime = 1)
    @CheckKundenwunschtermin(groups = { V1RequestVaKueMrn.class, V1RequestVaKueOrn.class }, minimumLeadTime = 7)
    @CheckKundenwunschterminNotInRange(groups = { V1RequestVaKueMrnWarn.class, V1RequestVaKueOrnWarn.class }, from = 7, to = 10)
    public LocalDate getKundenwunschtermin() {
        return kundenwunschtermin;
    }

    public void setKundenwunschtermin(LocalDate kundenwunschtermin) {
        this.kundenwunschtermin = kundenwunschtermin; // without time
    }

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "WECHSELTERMIN", nullable = true)
    public LocalDate getWechseltermin() {
        return wechseltermin;
    }

    public void setWechseltermin(LocalDate wechseltermin) {
        this.wechseltermin = wechseltermin; // without time
    }

    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, targetEntity = PersonOderFirma.class)
    @JoinColumn(name = "ENDKUNDE_ID", nullable = true)
    @Valid
    @NotNull(groups = { V1RequestVa.class }, message = "darf nicht leer sein")
    public PersonOderFirma getEndkunde() {
        return endkunde;
    }

    public void setEndkunde(PersonOderFirma endkunde) {
        this.endkunde = endkunde;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "T_WBCI_GESCHAEFTSFALL_ANSCHL",
            joinColumns = @JoinColumn(name = "GESCHAEFTSFALL_ID"),
            inverseJoinColumns = @JoinColumn(name = "ANSCHLUSS_ID")
    )
    @Fetch(FetchMode.SUBSELECT)
    @Size(min = 0, max = 99, groups = { V1RequestVa.class })
    @Valid
    public List<PersonOderFirma> getWeitereAnschlussinhaber() {
        return weitereAnschlussinhaber;
    }

    public void setWeitereAnschlussinhaber(List<PersonOderFirma> weitereAnschlussinhaber) {
        this.weitereAnschlussinhaber = weitereAnschlussinhaber;
    }

    public void addWeitererAnschlussinhaber(PersonOderFirma weitererAnschlussinhaber) {
        if (this.weitereAnschlussinhaber == null) {
            this.weitereAnschlussinhaber = new ArrayList<>();
        }
        this.weitereAnschlussinhaber.add(weitererAnschlussinhaber);
    }

    @CollectionTable(
            name = "T_WBCI_GF_BILLING_ORDER_NO",
            joinColumns = @JoinColumn(name = "GESCHAEFTSFALL_ID")
    )
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @Column(name = "BILLING_ORDER__NO")
    public Set<Long> getNonBillingRelevantOrderNoOrigs() {
        return nonBillingRelevantOrderNoOrigs;
    }

    public void setNonBillingRelevantOrderNoOrigs(Set<Long> nonBillingRelevantOrderNoOrigs) {
        this.nonBillingRelevantOrderNoOrigs = nonBillingRelevantOrderNoOrigs;
    }

    @Transient
    public Set<Long> getOrderNoOrigs() {
        Set<Long> auftragOrderNoOrigs = new HashSet<>();
        if (getBillingOrderNoOrig() != null) {
            auftragOrderNoOrigs.add(getBillingOrderNoOrig());
        }

        if (!CollectionUtils.isEmpty(getNonBillingRelevantOrderNoOrigs())) {
            // additionally add non billing relevant order no origs
            auftragOrderNoOrigs.addAll(getNonBillingRelevantOrderNoOrigs());
        }
        return auftragOrderNoOrigs;
    }

    @Column(name = "AUFTRAG_ID")
    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @Enumerated(EnumType.STRING)
    @NotNull
    public WbciGeschaeftsfallStatus getStatus() {
        return status;
    }

    public void setStatus(WbciGeschaeftsfallStatus status) {
        this.status = status;
    }

    @Column(name = "BILLING_ORDER__NO")
    public Long getBillingOrderNoOrig() {
        return billingOrderNoOrig;
    }

    public void setBillingOrderNoOrig(Long billingOrderNoOrig) {
        this.billingOrderNoOrig = billingOrderNoOrig;
    }

    @Column(name = "USER_ID")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "USER_NAME")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "TEAM_ID")
    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    @Column(name = "CURRENT_USER_NAME")
    public String getCurrentUserName() {
        return currentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
    }

    @Column(name = "CURRENT_USER_ID")
    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }

    @Column(name = "MNET_TECHNOLOGIE")
    @Enumerated(EnumType.STRING)
    public Technologie getMnetTechnologie() {
        return mnetTechnologie;
    }

    public void setMnetTechnologie(Technologie mnetTechnologie) {
        this.mnetTechnologie = mnetTechnologie;
    }

    /**
     * Setzt den initialen Bearbeiter (transient). Intern wird auch die (persistente) userId gesetzt.
     *
     * @param user
     */
    public void setUserAndTeam(AKUser user) {
        if (userId != null) {
            throw new IllegalStateException("Der initiale Bearbeiter wurde bereits gesetzt und darf nicht geändert werden.");
        }
        this.userName = (user == null ? null : user.getLoginName());
        this.userId = (user == null ? null : user.getId());
        this.teamId = (user == null || user.getTeam() == null ? null : user.getTeam().getId());
    }

    /**
     * Setzt den aktuellen Bearbeiter (transient). Intern wird auch die (persistente) userId gesetzt.
     *
     * @param user
     */
    public void setCurrentUser(AKUser user) {
        this.currentUserName = (user == null ? null : user.getLoginName());
        this.currentUserId = (user == null ? null : user.getId());
    }

    @Column(name = "BEMERKUNGEN")
    public String getBemerkungen() {
        return bemerkungen;
    }

    public void setBemerkungen(String bemerkungen) {
        this.bemerkungen = bemerkungen;
    }

    @Column(name = "KLAERFALL")
    public Boolean getKlaerfall() {
        return klaerfall;
    }

    public void setKlaerfall(Boolean klaerfall) {
        this.klaerfall = klaerfall;
    }

    /**
     * Gets the Storno-Aenderung vorabstimmungsId. This Id is set for Vorabstimmungen that were created as a result of
     * another vorabstimmung being cancelled (i.e. via a STR-AEN request). Using this vorabstimmungsId it is possible to
     * correlate this vorabstimmung to the original str-aen vorabstimmung.
     *
     * @return
     */
    @Column(name = "STR_AEN_VORABSTIMMUNGSID")
    public String getStrAenVorabstimmungsId() {
        return strAenVorabstimmungsId;
    }

    public void setStrAenVorabstimmungsId(String strAenVorabstimmungsId) {
        this.strAenVorabstimmungsId = strAenVorabstimmungsId;
    }

    @Column(name = "INTERNAL_STATUS")
    public String getInternalStatus() {
        return internalStatus;
    }

    public void setInternalStatus(String internalStatus) {
        this.internalStatus = internalStatus;
    }

    @Column(name = "AUTOMATABLE")
    public Boolean getAutomatable() {
        return automatable;
    }

    public void setAutomatable(Boolean automatable) {
        this.automatable = automatable;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = AutomationTask.class)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 1000)
    @JoinColumn(name = "GESCHAEFTSFALL_ID")
    public List<AutomationTask> getAutomationTasks() {
        return automationTasks;
    }

    public void setAutomationTasks(List<AutomationTask> automationTasks) {
        this.automationTasks = automationTasks;
    }

    public void addAutomationTask(AutomationTask automationTask) {
        if (automationTasks == null) {
            automationTasks = new ArrayList<>();
        }
        automationTasks.add(automationTask);
    }

    @Transient
    public AutomationTask getAutomationTask(TaskName taskName) {
        if (!CollectionUtils.isEmpty(automationTasks)) {
            for (AutomationTask automationTask : automationTasks) {
                if (automationTask.getName() == taskName) {
                    return automationTask;
                }
            }
        }
        return null;
    }

    @Transient
    public AutomationTask getAutomationTask(TaskName taskName, AutomationStatus status) {
        if (!CollectionUtils.isEmpty(automationTasks)) {
            for (AutomationTask automationTask : automationTasks) {
                if (automationTask.getName() == taskName && automationTask.getStatus() == status) {
                    return automationTask;
                }
            }
        }
        return null;
    }

    @Transient
    public boolean isTaskProcessingPossible(TaskName taskName) {
        if (!taskName.isMultipleTask() && !CollectionUtils.isEmpty(automationTasks)) {
            for (AutomationTask automationTask : automationTasks) {
                if (automationTask.getName() == taskName && automationTask.getStatus() == AutomationStatus.COMPLETED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns the {@link CarrierCode} of the EKP Partner. The EKP Partner is determined by checking the donating and
     * receiving EKPs and returning the <b>non</b> {@link CarrierCode#MNET} EKP. {@literal NULL} can be returned if the
     * donating or receiving EKP is not set.
     *
     * @return the {@link CarrierCode} of the partner EKP
     */
    @Transient
    public CarrierCode getEKPPartner() {
        if (CarrierCode.MNET.equals(aufnehmenderEKP)) {
            return abgebenderEKP;
        }
        else {
            return aufnehmenderEKP;
        }
    }

    /**
     * Returns true if M-Met is the receiving carrier.
     *
     * @return true if M-Net is the receiving carrier, otherwise false.
     */
    @Transient
    public boolean isMNetReceivingCarrier() {
        return (CarrierCode.MNET.equals(aufnehmenderEKP));
    }

    @Override
    public String toString() {
        return "WbciGeschaeftsfall{" +
                "wbciVersion=" + wbciVersion +
                ", auftragId=" + auftragId +
                ", billingOrderNoOrig=" + billingOrderNoOrig +
                ", nonBillingRelevantOrderNoOrigs=" + nonBillingRelevantOrderNoOrigs +
                ", absender=" + absender +
                ", aufnehmenderEKP=" + aufnehmenderEKP +
                ", abgebenderEKP=" + abgebenderEKP +
                ", vorabstimmungsId='" + vorabstimmungsId + '\'' +
                ", kundenwunschtermin=" + kundenwunschtermin +
                ", endkunde=" + endkunde +
                ", weitereAnschlussinhaber=" + weitereAnschlussinhaber +
                ", projekt=" + projekt +
                ", userName=" + userName +
                ", userId=" + userId +
                ", teamId=" + teamId +
                ", currentUserId=" + currentUserId +
                ", currentUserName=" + currentUserName +
                ", type='" + getTyp() + '\'' +
                ", mnetTechnologie='" + mnetTechnologie + '\'' +
                ", klaerfall='" + klaerfall + '\'' +
                ", strAenVorabstimmungsId='" + strAenVorabstimmungsId + '\'' +
                ", internalStatus='" + internalStatus + '\'' +
                ", automatable='" + automatable + '\'' +
                ", toString='" + super.toString() + '\'' +
                '}';
    }

}
