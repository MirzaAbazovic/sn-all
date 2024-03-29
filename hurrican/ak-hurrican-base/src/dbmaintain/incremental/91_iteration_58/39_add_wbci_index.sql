CREATE INDEX IX_WBCIREQ_2_AENDID ON T_WBCI_REQUEST (AENDERUNGS_ID) TABLESPACE "I_HURRICAN";
CREATE INDEX IX_WBCIREQ_2_STATUS ON T_WBCI_REQUEST (STATUS) TABLESPACE "I_HURRICAN";
CREATE INDEX IX_WBCIREQ_2_GFID ON T_WBCI_REQUEST (GESCHAEFTSFALL_ID) TABLESPACE "I_HURRICAN";
CREATE INDEX IX_WBCIREQ_2_EKID ON T_WBCI_REQUEST (ENDKUNDE_ID) TABLESPACE "I_HURRICAN";
CREATE INDEX IX_WBCIREQ_2_STDID ON T_WBCI_REQUEST (STANDORT_ID) TABLESPACE "I_HURRICAN";


CREATE INDEX IX_WBCIGF_2_EKID ON T_WBCI_GESCHAEFTSFALL (ENDKUNDE_ID) TABLESPACE "I_HURRICAN";
CREATE INDEX IX_WBCIGF_2_PROJID ON T_WBCI_GESCHAEFTSFALL (PROJECT_ID) TABLESPACE "I_HURRICAN";
CREATE INDEX IX_WBCIGF_2_STDID ON T_WBCI_GESCHAEFTSFALL (STANDORT_ID) TABLESPACE "I_HURRICAN";
CREATE INDEX IX_WBCIGF_2_RNPID ON T_WBCI_GESCHAEFTSFALL (RUFNUMMERPORTIERUNG_ID) TABLESPACE "I_HURRICAN";
CREATE INDEX IX_WBCIGF_2_AID ON T_WBCI_GESCHAEFTSFALL (ANSCHLUSSIDENTIFIKATION_ID) TABLESPACE "I_HURRICAN";

CREATE INDEX IX_WBCIMLD_2_IOTYPE ON T_WBCI_MELDUNG (IO_TYPE) TABLESPACE "I_HURRICAN";
CREATE INDEX IX_WBCIMLD_2_AENID ON T_WBCI_MELDUNG (AENDERUNG_ID_REF) TABLESPACE "I_HURRICAN";
CREATE INDEX IX_WBCIMLD_2_STOID ON T_WBCI_MELDUNG (STORNO_ID_REF) TABLESPACE "I_HURRICAN";
CREATE INDEX IX_WBCIMLD_2_GFID ON T_WBCI_MELDUNG (GESCHAEFTSFALL_ID) TABLESPACE "I_HURRICAN";

CREATE INDEX IX_WBCIGFA_2_GFID ON T_WBCI_GESCHAEFTSFALL_ANSCHL (GESCHAEFTSFALL_ID) TABLESPACE "I_HURRICAN";
