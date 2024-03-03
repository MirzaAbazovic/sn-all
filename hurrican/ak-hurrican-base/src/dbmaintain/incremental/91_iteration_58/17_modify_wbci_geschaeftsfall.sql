alter table T_WBCI_GESCHAEFTSFALL modify AUFTRAG_ID NULL;

ALTER TABLE T_WBCI_GESCHAEFTSFALL drop constraint ck_wbcigf_status;
                       ALTER TABLE T_WBCI_GESCHAEFTSFALL
                       ADD CONSTRAINT CK_WBCIGF_STATUS
                       CHECK (STATUS IN (
                           'VORGEHALTEN',
                           'VERSENDET',
                           'BEANTWORTET',
                           'EMPFANGEN'
                         )) ENABLE NOVALIDATE;