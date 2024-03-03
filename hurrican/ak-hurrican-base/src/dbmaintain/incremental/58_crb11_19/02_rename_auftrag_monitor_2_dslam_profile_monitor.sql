-- Nennt die Tabelle T_AUFTRAG_MONITOR um in T_DSLAM_PROFILE_MONITOR
alter table T_AUFTRAG_MONITOR rename to T_DSLAM_PROFILE_MONITOR;

-- Nennt den Primärschlüssel der Tabelle um
alter table T_DSLAM_PROFILE_MONITOR rename constraint T_AUFTRAG_MONITOR_PK to T_DSLAM_PROFILE_MONITOR_PK;

-- Berechtigungen und Sequenz
grant select, insert, update on T_DSLAM_PROFILE_MONITOR to R_HURRICAN_USER;
grant select on T_DSLAM_PROFILE_MONITOR to R_HURRICAN_READ_ONLY;

drop sequence S_T_AUFTRAG_MONITOR_0;
create sequence S_T_DSLAM_PROFILE_MONITOR_0;
grant select on S_T_DSLAM_PROFILE_MONITOR_0 to public;