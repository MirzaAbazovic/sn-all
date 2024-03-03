-- Neuer Verlauf-Status fuer zentrale Dispo

insert into T_VERLAUF_STATUS
   (ID, VERLAUF_STATUS)
 values
   (4000, 'bei zentraler Dispo');
insert into T_VERLAUF_STATUS
   (ID, VERLAUF_STATUS)
 values
   (4600, 'Rückläufer zentrale Dispo');

update T_VERLAUF set VERLAUF_STATUS_ID = 4000 where VERLAUF_STATUS_ID = 4150;
update T_VERLAUF_ABTEILUNG set VERLAUF_STATUS_ID = 4000 where VERLAUF_STATUS_ID = 4150;

delete from T_VERLAUF_STATUS where ID = 4150;
