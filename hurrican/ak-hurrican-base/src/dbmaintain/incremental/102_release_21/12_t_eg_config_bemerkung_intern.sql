-- HUR-24341 / ANF-536.C - Neues, internes Bemerkungsfeld für Endgeräte


ALTER TABLE T_EG_CONFIG
ADD (BEMERKUNG_INTERN VARCHAR2(4000));