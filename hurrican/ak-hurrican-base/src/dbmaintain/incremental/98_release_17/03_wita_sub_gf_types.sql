Insert into T_REFERENCE
   (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE,
    ORDER_NO, DESCRIPTION, VERSION, CHANGED_AT)
 Values
   (8800, 'TAL_INTERNER_GF_TYP', 'Änderung Umzug', NULL, '1',
    100, '', 0, sysdate);

Insert into T_REFERENCE
   (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE,
    ORDER_NO, DESCRIPTION, VERSION, CHANGED_AT)
 Values
   (8801, 'TAL_INTERNER_GF_TYP', 'Änderung Produkt-/Tarifwechsel', NULL, '1',
    100, '', 0, sysdate);


alter table T_CB_VORGANG add GF_TYP_INTERN_REF_ID NUMBER(19);
COMMENT ON COLUMN T_CB_VORGANG.GF_TYP_INTERN_REF_ID IS 'Referenz auf einen internen Geschaeftsfall-Typ zur Steuerung des Kundenanschreibens im Automatismus';

ALTER TABLE T_CB_VORGANG ADD
  CONSTRAINT FK_CBVORGANG_GFTYP_2_REFERENCE
  FOREIGN KEY (GF_TYP_INTERN_REF_ID)
  REFERENCES T_REFERENCE (ID);
