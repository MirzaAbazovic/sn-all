-- MIN/MINPR/BIS/NefMaxi/NefMaxiPur
update T_KUENDIGUNG_FRIST set FRIST_AUF='EINGANGSDATUM', AUTO_VERLAENGERUNG=0, DESCRIPTION='4 Wochen ab Eingangsdatum' where ID in (41,39,43);
update T_KUENDIGUNG_FRIST set FRIST_AUF='EINGANGSDATUM', AUTO_VERLAENGERUNG=0, DESCRIPTION='3 Monate ab Eingangsdatum' where ID in (89,95);

-- MaxiKomplett vor 04/2008
update T_KUENDIGUNG_FRIST set FRIST_AUF='EINGANGSDATUM', AUTO_VERLAENGERUNG=0, DESCRIPTION='3 Monate ab Eingangsdatum' where ID in (185);
