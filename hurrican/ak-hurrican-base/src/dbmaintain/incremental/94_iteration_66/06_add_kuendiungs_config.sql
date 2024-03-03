-- Surf&Fon Flat
insert into T_KUENDIGUNG_CHECK (ID, OE__NO, DURCH_VERTRIEB, AUTO_VERLAENGERUNG)
  values (S_T_KUENDIGUNG_CHECK_0.nextVal, 3401, '0', 12);
insert into T_KUENDIGUNG_FRIST (ID, KUENDIGUNG_CHECK_ID, MIT_MVLZ, FRIST_IN_WOCHEN, FRIST_AUF, VERTRAG_AB_JAHR, VERTRAG_AB_MONAT, DESCRIPTION)
  values (S_T_KUENDIGUNG_FRIST_0.nextVal, (select c.id from T_KUENDIGUNG_CHECK c where c.OE__NO=3401),
      '0', 6, 'EINGANGSDATUM', null, null, 'ohne Mindestvertragslaufzeit: 6 Wochen Kuendigungsfrist');
insert into T_KUENDIGUNG_FRIST (ID, KUENDIGUNG_CHECK_ID, MIT_MVLZ, FRIST_IN_WOCHEN, FRIST_AUF, VERTRAG_AB_JAHR, VERTRAG_AB_MONAT, DESCRIPTION)
  values (S_T_KUENDIGUNG_FRIST_0.nextVal, (select c.id from T_KUENDIGUNG_CHECK c where c.OE__NO=3401),
      '1', 12, 'ENDE_MVLZ', null, null, 'mit Mindestvertragslaufzeit: 3 Monate zum Ende der MVLZ');
