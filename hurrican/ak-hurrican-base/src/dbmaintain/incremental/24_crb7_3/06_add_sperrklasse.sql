alter table T_SPERRKLASSEN add PREMIUM_SERVICES_INT CHAR(1);
comment on column T_SPERRKLASSEN.PREMIUM_SERVICES_INT
    is 'Sperrklasse <Premium Dienste im Ausland und Voting-Dienste>';
update T_SPERRKLASSEN set PREMIUM_SERVICES_INT='0';

alter table T_SPERRKLASSEN drop column ANSICHT;

Insert into T_SPERRKLASSEN
   (ID,
   SPERRKLASSE,
   ABGEHEND,
   NATIONAL,
   INNOVATIVE_DIENSTE,
   MABEZ,
   MOBIL,
   VPN,
   PRD,
   AUSKUNFTSDIENSTE,
   INTERNATIONAL,
   OFF_LINE,
   PREMIUM_SERVICES_INT,
   VERSION)
 Values
   (S_T_SPERRKLASSEN_0.nextVal,
   179,
   '0',
   '0',
   '0',
   '0',
   '0',
   '1',
   '1',
   '1',
   '0',
   '0',
   '1',
   0);

