UPDATE T_WBCI_MELDUNG SET AENDERUNG_ID_REF = NULL, STORNO_ID_REF = NULL WHERE GESCHAEFTSFALL_ID = (SELECT ID FROM T_WBCI_GESCHAEFTSFALL WHERE VORABSTIMMUNGSID = '${preAgreementId}');
UPDATE T_WBCI_REQUEST SET AENDERUNGS_ID = NULL WHERE GESCHAEFTSFALL_ID = (SELECT ID FROM T_WBCI_GESCHAEFTSFALL WHERE VORABSTIMMUNGSID = '${preAgreementId}');
UPDATE T_WBCI_GESCHAEFTSFALL SET VORABSTIMMUNGSID = '${preAgreementIdCleanup}' WHERE VORABSTIMMUNGSID = '${preAgreementId}';
UPDATE T_IO_ARCHIVE SET WITA_EXT_ORDER_NO = '${preAgreementIdCleanup}' WHERE WITA_EXT_ORDER_NO = '${preAgreementId}';
