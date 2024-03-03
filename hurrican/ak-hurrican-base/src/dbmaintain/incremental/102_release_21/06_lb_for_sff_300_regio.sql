INSERT INTO T_LB_2_PRODUKT
  (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION, VERSION)
  (SELECT
    17,
    90662,
    3401,
    unistr('\00DC') || '/Surf' || chr(38) || 'Fon-Flat 300 inkl. Komf-Ansch.', -- Bezeichnung aus Taifun
    0
    FROM DUAL WHERE NOT EXISTS (SELECT * FROM T_LB_2_PRODUKT where LEISTUNG__NO = 90662 and PRODUCT_OE__NO = 3401));
