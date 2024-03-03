-- automatische Vertragsverlaengerung von T_KUENDIGUNG_CHECK nach T_KUENDIGUNG_FRIST verschieben
alter table T_KUENDIGUNG_FRIST add AUTO_VERLAENGERUNG NUMBER(19,0) DEFAULT 0 NOT NULL;
update T_KUENDIGUNG_FRIST kf
  set kf.AUTO_VERLAENGERUNG = (
      select kc.AUTO_VERLAENGERUNG from T_KUENDIGUNG_CHECK kc
        where kc.ID=kf.KUENDIGUNG_CHECK_ID
    );
alter table T_KUENDIGUNG_CHECK drop column AUTO_VERLAENGERUNG;


-- Konfiguration fuer MaxiKomplett erweitern
-- vor 04/2008: automatische Verlaengerung 3 Monate
update T_KUENDIGUNG_FRIST set AUTO_VERLAENGERUNG=3 where KUENDIGUNG_CHECK_ID=98;
-- ab 04/2008: automatische Verlaengerung 12 Monate
Insert into T_KUENDIGUNG_FRIST
   (ID, KUENDIGUNG_CHECK_ID, MIT_MVLZ, FRIST_IN_WOCHEN, FRIST_AUF,
    VERTRAG_AB_JAHR, VERTRAG_AB_MONAT, DESCRIPTION, VERSION, AUTO_VERLAENGERUNG)
 Values
   (S_T_KUENDIGUNG_FRIST_0.nextVal, 98, '1', 12, 'ENDE_MVLZ',
    2008, 4, '3 Monate zum Ende der MVLZ', 0, 12);
Insert into T_KUENDIGUNG_FRIST
   (ID, KUENDIGUNG_CHECK_ID, MIT_MVLZ, FRIST_IN_WOCHEN, FRIST_AUF,
    VERTRAG_AB_JAHR, VERTRAG_AB_MONAT, DESCRIPTION, VERSION, AUTO_VERLAENGERUNG)
 Values
   (S_T_KUENDIGUNG_FRIST_0.nextVal, 98, '0', 12, 'EINGANGSDATUM',
    2008, 4, '3 Monate ab Eingangsdatum', 0, 12);
