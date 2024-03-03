/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.04.2014
 */
package de.mnet.wbci.model;

import java.util.*;

/**
 *
 */
public class EscalationPreAgreementVO extends BasePreAgreementVO {
    public static final List<EscalationPreAgreementVO.EscalationType> ESC_TYPES_WITH_WECHSEL_TERMIN_DEADLINE = Arrays.asList(
            EscalationPreAgreementVO.EscalationType.RUEM_VA_VERSENDET,
            EscalationPreAgreementVO.EscalationType.RUEM_VA_EMPFANGEN,
            EscalationPreAgreementVO.EscalationType.STORNO_AEN_ERLM_VERSENDET_AUF,
            EscalationPreAgreementVO.EscalationType.STORNO_AEN_ERLM_VERSENDET_ABG,
            EscalationPreAgreementVO.EscalationType.STORNO_AEN_ERLM_EMPFANGEN_AUF,
            EscalationPreAgreementVO.EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG);

    private static final long serialVersionUID = -4768968343778352884L;
    private EscalationType escalationType;
    private EscalationLevel escalationLevel;
    private Long deadlineDays;


    public EscalationType getEscalationType() {
        return escalationType;
    }

    public void setEscalationType(EscalationType escalationType) {
        this.escalationType = escalationType;
    }

    public EscalationLevel getEscalationLevel() {
        return escalationLevel;
    }

    public void setEscalationLevel(EscalationLevel escalationLevel) {
        this.escalationLevel = escalationLevel;
    }

    public Long getDeadlineDays() {
        return deadlineDays;
    }

    public void setDeadlineDays(Long deadlineDays) {
        this.deadlineDays = deadlineDays;
    }

    public String getEscalationStatusDescription() {
        if (escalationType != null && escalationType.isNewVaExpired()) {
            return getGeschaeftsfallStatusDescription();
        }
        return getRequestStatusDescription();
    }


    public String getDeadlineDaysStringValue() {
        if (deadlineDays != null) {
            if (ESC_TYPES_WITH_WECHSEL_TERMIN_DEADLINE.contains(escalationType)) {
                return deadlineDays + " (vor WT)";
            }
            return deadlineDays.toString();
        }
        return null;
    }

    @Override
    public String toString() {
        return "EscalationPreAgreementVO{" +
                super.toString() +
                ", escalationType=" + escalationType +
                ", escalationLevel=" + escalationLevel +
                ", deadlineDays=" + deadlineDays +
                '}';
    }

    /**
     * Escaltion Type describes what kind of escalation occurred.
     */
    public static enum EscalationType {
        /**
         * For Preagreements, which haven't been answered in time. The base for the deadline calculation are the days
         * from the request sending date plus 3 days until now.
         */
        VA_VERSENDET(
                RequestTyp.VA,
                WbciRequestStatus.VA_VERSENDET,
                false,
                true,
                Arrays.asList(MeldungTyp.RUEM_VA, MeldungTyp.ABBM),
                Arrays.asList(
                        new EscalationRange(EscalationLevel.LEVEL_1, -1, -4),
                        new EscalationRange(EscalationLevel.LEVEL_2, -4, -7),
                        new EscalationRange(EscalationLevel.LEVEL_3, -7, null)
                )
        ),
        /**
         * Internal Escalation Type for overdue VAs. Because of the internal usage, no special {@link EscalationLevel}s
         * are configured.
         */
        VA_EMPFANGEN(
                RequestTyp.VA,
                WbciRequestStatus.VA_EMPFANGEN,
                true,
                false,
                Arrays.asList(MeldungTyp.RUEM_VA, MeldungTyp.ABBM),
                Arrays.asList(new EscalationRange(EscalationLevel.LEVEL_1, -1, null))
        ),
        /**
         * For RUEM-VAs, which haven't been answered in time. The base for the deadline calculation are the days from
         * the confirmed Wechseltermin minus 10 until now.
         */
        RUEM_VA_VERSENDET(
                RequestTyp.VA,
                WbciRequestStatus.RUEM_VA_VERSENDET,
                false,
                true,
                Arrays.asList(MeldungTyp.AKM_TR),
                Arrays.asList(
                        new EscalationRange(EscalationLevel.LEVEL_1, -1, -5),
                        new EscalationRange(EscalationLevel.LEVEL_2, -5, -7),
                        new EscalationRange(EscalationLevel.LEVEL_3, -7, null)
                )
        ),
        /**
         * Internal Escalation Type for overdue RUEM_VAs. Because of the internal usage, no special {@link
         * EscalationLevel}s are configured.
         */
        RUEM_VA_EMPFANGEN(
                RequestTyp.VA,
                WbciRequestStatus.RUEM_VA_EMPFANGEN,
                true,
                false,
                Arrays.asList(MeldungTyp.AKM_TR),
                Arrays.asList(new EscalationRange(EscalationLevel.LEVEL_1, -1, null))
        ),
        /**
         * For Stronos, which haven't been answered in time. The base for the deadline calculation are the days from the
         * storno sending date until now.
         */
        STORNO_AEN_ABG_VERSENDET(
                RequestTyp.STR_AEN_ABG,
                WbciRequestStatus.STORNO_VERSENDET,
                false,
                true,
                Arrays.asList(MeldungTyp.ERLM, MeldungTyp.ABBM),
                Arrays.asList(
                        new EscalationRange(EscalationLevel.LEVEL_1, -1, -2),
                        new EscalationRange(EscalationLevel.LEVEL_2, -2, -4),
                        new EscalationRange(EscalationLevel.LEVEL_3, -4, null)
                )
        ),
        STORNO_AEN_AUF_VERSENDET(
                RequestTyp.STR_AEN_AUF,
                WbciRequestStatus.STORNO_VERSENDET,
                false,
                true,
                Arrays.asList(MeldungTyp.ERLM, MeldungTyp.ABBM),
                Arrays.asList(
                        new EscalationRange(EscalationLevel.LEVEL_1, -1, -2),
                        new EscalationRange(EscalationLevel.LEVEL_2, -2, -4),
                        new EscalationRange(EscalationLevel.LEVEL_3, -4, null)
                )
        ),
        STORNO_AUFH_ABG_VERSENDET(
                RequestTyp.STR_AUFH_ABG,
                WbciRequestStatus.STORNO_VERSENDET,
                false,
                true,
                Arrays.asList(MeldungTyp.ERLM, MeldungTyp.ABBM),
                Arrays.asList(
                        new EscalationRange(EscalationLevel.LEVEL_1, -1, -2),
                        new EscalationRange(EscalationLevel.LEVEL_2, -2, -4),
                        new EscalationRange(EscalationLevel.LEVEL_3, -4, null)
                )
        ),
        STORNO_AUFH_AUF_VERSENDET(
                RequestTyp.STR_AUFH_AUF,
                WbciRequestStatus.STORNO_VERSENDET,
                false,
                true,
                Arrays.asList(MeldungTyp.ERLM, MeldungTyp.ABBM),
                Arrays.asList(
                        new EscalationRange(EscalationLevel.LEVEL_1, -1, -2),
                        new EscalationRange(EscalationLevel.LEVEL_2, -2, -4),
                        new EscalationRange(EscalationLevel.LEVEL_3, -4, null)
                )
        ),
        /**
         * Internal Escalation Type for overdue STORNOs. Because of the internal usage, no special {@link
         * EscalationLevel}s are configured.
         */
        STORNO_AEN_ABG_EMPFANGEN(
                RequestTyp.STR_AEN_ABG,
                WbciRequestStatus.STORNO_EMPFANGEN,
                true,
                false,
                Arrays.asList(MeldungTyp.ERLM, MeldungTyp.ABBM),
                Arrays.asList(new EscalationRange(EscalationLevel.LEVEL_1, -1, null))
        ),
        STORNO_AEN_AUF_EMPFANGEN(
                RequestTyp.STR_AEN_AUF,
                WbciRequestStatus.STORNO_EMPFANGEN,
                true,
                false,
                Arrays.asList(MeldungTyp.ERLM, MeldungTyp.ABBM),
                Arrays.asList(new EscalationRange(EscalationLevel.LEVEL_1, -1, null))
        ),
        STORNO_AUFH_ABG_EMPFANGEN(
                RequestTyp.STR_AUFH_ABG,
                WbciRequestStatus.STORNO_EMPFANGEN,
                true,
                false,
                Arrays.asList(MeldungTyp.ERLM, MeldungTyp.ABBM),
                Arrays.asList(new EscalationRange(EscalationLevel.LEVEL_1, -1, null))
        ),
        STORNO_AUFH_AUF_EMPFANGEN(
                RequestTyp.STR_AUFH_AUF,
                WbciRequestStatus.STORNO_EMPFANGEN,
                true,
                false,
                Arrays.asList(MeldungTyp.ERLM, MeldungTyp.ABBM),
                Arrays.asList(new EscalationRange(EscalationLevel.LEVEL_1, -1, null))
        ),
        /**
         * For Strono Erledigtmeldungen, for which no Storno Erledigt meldung has been received.
         */
        STORNO_AEN_ERLM_EMPFANGEN_ABG(
                RequestTyp.STR_AEN_ABG,
                WbciRequestStatus.STORNO_ERLM_EMPFANGEN,
                true,
                true,
                "neue VA",
                Arrays.asList(
                        new EscalationRange(EscalationLevel.LEVEL_1, 5, 3),
                        new EscalationRange(EscalationLevel.LEVEL_2, 3, 1),
                        new EscalationRange(EscalationLevel.LEVEL_3, 1, -1)
                )
        ),
        STORNO_AEN_ERLM_EMPFANGEN_AUF(
                RequestTyp.STR_AEN_AUF,
                WbciRequestStatus.STORNO_ERLM_EMPFANGEN,
                true,
                true,
                "neue VA",
                Arrays.asList(
                        new EscalationRange(EscalationLevel.LEVEL_1, 5, 3),
                        new EscalationRange(EscalationLevel.LEVEL_2, 3, 1),
                        new EscalationRange(EscalationLevel.LEVEL_3, 1, -1)
                )
        ),
        STORNO_AEN_ERLM_VERSENDET_ABG(
                RequestTyp.STR_AEN_ABG,
                WbciRequestStatus.STORNO_ERLM_VERSENDET,
                true,
                true,
                "neue VA",
                Arrays.asList(
                        new EscalationRange(EscalationLevel.LEVEL_1, 5, 3),
                        new EscalationRange(EscalationLevel.LEVEL_2, 3, 1),
                        new EscalationRange(EscalationLevel.LEVEL_3, 1, -1)
                )
        ),
        STORNO_AEN_ERLM_VERSENDET_AUF(
                RequestTyp.STR_AEN_AUF,
                WbciRequestStatus.STORNO_ERLM_VERSENDET,
                true,
                true,
                "neue VA",
                Arrays.asList(
                        new EscalationRange(EscalationLevel.LEVEL_1, 5, 3),
                        new EscalationRange(EscalationLevel.LEVEL_2, 3, 1),
                        new EscalationRange(EscalationLevel.LEVEL_3, 1, -1)
                )
        ),

        TV_VERSENDET(
                RequestTyp.TV,
                WbciRequestStatus.TV_VERSENDET,
                false,
                true,
                Arrays.asList(MeldungTyp.ERLM, MeldungTyp.ABBM),
                Arrays.asList(
                        new EscalationRange(EscalationLevel.LEVEL_1, -1, -2),
                        new EscalationRange(EscalationLevel.LEVEL_2, -2, -4),
                        new EscalationRange(EscalationLevel.LEVEL_3, -4, null)
                )
        ),
        /**
         * Internal Escalation Type for overdue VAs. Because of the internal usage, no special {@link EscalationLevel}s
         * are configured.
         */
        TV_EMPFANGEN(
                RequestTyp.TV,
                WbciRequestStatus.TV_EMPFANGEN,
                true,
                false,
                Arrays.asList(MeldungTyp.ERLM, MeldungTyp.ABBM),
                Arrays.asList(new EscalationRange(EscalationLevel.LEVEL_1, -1, null))
        ),

        /**
         * Special state for all VAs with the {@link WbciGeschaeftsfallStatus#NEW_VA_EXPIRED}. In this case, the
         * deadline count won't be considered.
         */
        NEW_VA_EXPIRED(
                null,
                null,
                true,
                false,
                "Vorgang abschließen",
                Arrays.asList(new EscalationRange(EscalationLevel.INFO, null, null)));
        /**
         * Flag which indicates if this escalation typ is usable for the INTERNAL escalation list.
         */
        private final boolean internal;
        /**
         * Flag which indicates if this escalation typ is usable for the EXTERNAL escalation list.
         */
        private final boolean external;

        /**
         * describes the {@link WbciRequestStatus} for a possible Escalation
         */
        private WbciRequestStatus requestStatus;

        /**
         * describes the {@link RequestTyp} for a possible Escalation
         */
        private RequestTyp typ;

        /**
         * describes the expected action from the carrier.
         */
        private String expectedAction;

        private Collection<EscalationRange> ranges;

        private EscalationType(RequestTyp typ, WbciRequestStatus requestStatus, boolean internal, boolean external, String expectedAction, Collection<EscalationRange> ranges) {
            this.typ = typ;
            this.requestStatus = requestStatus;
            this.internal = internal;
            this.external = external;
            this.expectedAction = expectedAction;
            this.ranges = ranges;
        }

        private EscalationType(RequestTyp typ, WbciRequestStatus requestStatus, boolean internal, boolean external, List<MeldungTyp> expectedMeldungTyp, Collection<EscalationRange> ranges) {
            this(typ, requestStatus, internal, external, meldungenToAction(expectedMeldungTyp), ranges);
        }

        private static String meldungenToAction(List<MeldungTyp> meldungen) {
            String result = "";
            Iterator<MeldungTyp> it = meldungen.iterator();
            while ((it.hasNext())) {
                result += it.next().getShortName();
                if (it.hasNext()) {
                    result += ", ";
                }
            }
            return result;
        }

        public RequestTyp getTyp() {
            return typ;
        }

        public WbciRequestStatus getRequestStatus() {
            return requestStatus;
        }

        /**
         * Calculates the EscalationLevel based on the collection of EscalationRanges defined. If the {@link #ranges}
         * contains a range with the escalation type {@link EscalationLevel#INFO}, don't check the {@link
         * EscalationRange#from} and {@link EscalationRange#to};
         *
         * @param daysUntilDeadline days until deadline
         * @return the calculated {@link EscalationLevel} or null
         */
        public EscalationLevel getEscalationLevelForDeadline(Integer daysUntilDeadline) {
            if (ranges != null) {
                for (EscalationRange range : ranges) {
                    if (range.isInRange(daysUntilDeadline)) {
                        return range.getLevel();
                    }
                }
            }
            return null;
        }

        public String getExpectedAction() {
            return expectedAction;
        }

        public boolean isInternal() {
            return internal;
        }

        public boolean isExternal() {
            return external;
        }

        public boolean isRuemVa() {
            return this.equals(RUEM_VA_EMPFANGEN) || this.equals(RUEM_VA_VERSENDET);
        }

        public boolean isNewVaExpired() {
            return this.equals(NEW_VA_EXPIRED);
        }
    }

    /**
     * EscalationLevel describes how  serious the escalation is.
     */
    public static enum EscalationLevel {
        /**
         * Escalation should take place over E-Mail and Excel escalation list.
         */
        LEVEL_1,
        /**
         * Escalation should be done by the escalation teams of the carriers
         */
        LEVEL_2,
        /**
         * Highest escalation level. Escalation should be don by the managers of the WBCI processing team.
         */
        LEVEL_3,
        /**
         * If this escalation typ is set in {@link EscalationRange}, the from and to fields won't be considered.
         */
        INFO,
    }

    public static class EscalationRange {
        private EscalationLevel level;
        private Integer from;
        private Integer to;

        /**
         * @param level
         * @param from  greater than or equal to
         * @param to    less than
         */
        public EscalationRange(EscalationLevel level, Integer from, Integer to) {
            this.level = level;
            this.from = ((from == null) ? Integer.valueOf(Integer.MAX_VALUE) : from);
            this.to = ((to == null) ? Integer.valueOf(Integer.MIN_VALUE) : to);
        }

        /**
         * return true: <ul> <li>the defined level is {@link EscalationLevel#INFO}</li> <li>the deadlineDays is not null
         * AND the deadlineDays are in range.</li> </ul>
         *
         * @param deadlineInDays
         * @return
         */
        public boolean isInRange(Integer deadlineInDays) {
            return EscalationLevel.INFO.equals(level) ||
                    (deadlineInDays != null && (from >= deadlineInDays && deadlineInDays > to));
        }

        public EscalationLevel getLevel() {
            return level;
        }
    }
}
