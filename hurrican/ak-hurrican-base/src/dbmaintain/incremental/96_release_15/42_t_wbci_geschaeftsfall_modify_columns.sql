-- modify column sizes since we are using the CarrierCode-Enum instead of the ITU_CarrierCode
ALTER TABLE T_WBCI_GESCHAEFTSFALL MODIFY (ABGEBENDEREKP VARCHAR2(20));
ALTER TABLE T_WBCI_GESCHAEFTSFALL MODIFY (AUFNEHMENDEREKP VARCHAR2(20));
ALTER TABLE T_WBCI_GESCHAEFTSFALL MODIFY (ABSENDER VARCHAR2(20));