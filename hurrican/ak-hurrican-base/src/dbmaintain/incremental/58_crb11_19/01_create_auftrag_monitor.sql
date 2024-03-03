-- Erstellt eine neue Tabelle T_AUFTRAG_MONITOR in welcher alle Aufträge für eine Überwachung eingetragen werden
create table T_AUFTRAG_MONITOR (
        ID number(19,0) not null,
        VERSION number(19,0) default 0 not null,
        AUFTRAG_ID number(19,0) not null,
        MONITORING_SINCE date not null,
        MONITORING_ENDS date not null,
        DELETED char(1) default 0 not null
    );

-- Fügt der Tabelle einen Primärschlüssel hinzu
alter table T_AUFTRAG_MONITOR add constraint T_AUFTRAG_MONITOR_PK primary key (ID);

-- Fügt der Tabelle T_AUFTRAG_MONITOR einen Fremdschlüssel auf die Tabelle T_AUFTRAG hinzu
alter table T_AUFTRAG_MONITOR add constraint FK_AUFTRAG_ID
  foreign key (AUFTRAG_ID) references T_AUFTRAG (ID);

-- Berechtigungen und Sequenz
grant select, insert, update on T_AUFTRAG_MONITOR to R_HURRICAN_USER;
grant select on T_AUFTRAG_MONITOR to R_HURRICAN_READ_ONLY;

create sequence S_T_AUFTRAG_MONITOR_0;
grant select on S_T_AUFTRAG_MONITOR_0 to public;