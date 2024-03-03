-- Konfiguration fuer Premium Produkte nachziehen
Insert into T_KUENDIGUNG_FRIST
   (ID, KUENDIGUNG_CHECK_ID, MIT_MVLZ, FRIST_IN_WOCHEN, FRIST_AUF,
    VERTRAG_AB_JAHR, VERTRAG_AB_MONAT, DESCRIPTION, VERSION, AUTO_VERLAENGERUNG)
 Values
   (S_T_KUENDIGUNG_FRIST_0.nextVal, 97, '0', 4, 'EINGANGSDATUM',
    2010, 4, '4 Wochen ab Eingangsdatum', 0, 12);
Insert into T_KUENDIGUNG_FRIST
   (ID, KUENDIGUNG_CHECK_ID, MIT_MVLZ, FRIST_IN_WOCHEN, FRIST_AUF,
    VERTRAG_AB_JAHR, VERTRAG_AB_MONAT, DESCRIPTION, VERSION, AUTO_VERLAENGERUNG)
 Values
   (S_T_KUENDIGUNG_FRIST_0.nextVal, 97, '1', 4, 'ENDE_MVLZ',
    2010, 4, '4 Wochen zum Ende MVLZ; sonst Verlängerung um 3 Monate', 0, 12);


-- M-net Premium Glasfaser DSL
Insert into T_KUENDIGUNG_CHECK
   (ID, OE__NO, DURCH_VERTRIEB, VERSION)
 Values
   (S_T_KUENDIGUNG_CHECK_0.nextVal, 3011, '0', 0);

insert into T_KUENDIGUNG_FRIST (ID, KUENDIGUNG_CHECK_ID,
  MIT_MVLZ, FRIST_IN_WOCHEN, FRIST_AUF, VERTRAG_AB_JAHR, VERTRAG_AB_MONAT, DESCRIPTION,
  AUTO_VERLAENGERUNG)
  values (S_T_KUENDIGUNG_FRIST_0.nextVal, (select c.id from T_KUENDIGUNG_CHECK c where c.OE__NO=3011),
      '0', 6, 'EINGANGSDATUM', null, null, '6 Wochen ab Eingangsdatum',
      12);

insert into T_KUENDIGUNG_FRIST (ID, KUENDIGUNG_CHECK_ID,
  MIT_MVLZ, FRIST_IN_WOCHEN, FRIST_AUF, VERTRAG_AB_JAHR, VERTRAG_AB_MONAT, DESCRIPTION,
  AUTO_VERLAENGERUNG)
  values (S_T_KUENDIGUNG_FRIST_0.nextVal, (select c.id from T_KUENDIGUNG_CHECK c where c.OE__NO=3011),
      '1', 12, 'ENDE_MVLZ', null, null, '3 Monate vor Ende der MVLZ',
      12);
