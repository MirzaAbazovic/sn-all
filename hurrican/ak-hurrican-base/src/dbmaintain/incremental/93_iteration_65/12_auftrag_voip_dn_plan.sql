create table T_AUFTRAG_VOIP_DN_PLAN (
  id number(19) primary key,
  gueltig_ab TIMESTAMP not null,
  auftrag_voip_dn_id number(19) references T_AUFTRAG_VOIP_DN(id) not null,
  version number(19) default 0 not null
);

create sequence S_T_AUFTRAG_VOIP_DN_PLAN start with 1 increment by 1;

create table T_VOIP_DNPLAN_BLOCK (
  voip_dn_plan_id number(19) references T_AUFTRAG_VOIP_DN_PLAN(id) not null,
  anfang varchar2(25) not null,
  ende varchar2(25),
  zentrale char(1) not null
);

GRANT SELECT, INSERT, UPDATE ON T_AUFTRAG_VOIP_DN_PLAN TO R_HURRICAN_USER;
GRANT SELECT ON T_AUFTRAG_VOIP_DN_PLAN TO R_HURRICAN_READ_ONLY;
GRANT SELECT ON S_T_AUFTRAG_VOIP_DN_PLAN TO PUBLIC;

GRANT SELECT, INSERT, UPDATE ON T_VOIP_DNPLAN_BLOCK TO R_HURRICAN_USER;
GRANT SELECT ON T_VOIP_DNPLAN_BLOCK TO R_HURRICAN_READ_ONLY;
