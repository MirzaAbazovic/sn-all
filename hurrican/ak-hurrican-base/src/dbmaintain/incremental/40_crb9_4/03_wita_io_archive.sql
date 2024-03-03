
create table T_IO_ARCHIVE (
  id number(19,0) not null,
  version number(19,0) default 0 not null,
  IO_TYPE varchar2(10) default 'UNDEFINED' not null,
  WITA_EXT_ORDER_NO varchar2(20),
  REQUEST_TYPE varchar2(10) not null,
  REQUEST_TIMEST date not null,
  REQUEST_XML BLOB not null,
  RESPONSE_TYPE varchar2(10),
  RESPONSE_TIMEST date,
  RESPONSE_XML BLOB,
  primary key(id)
);

comment on table T_IO_ARCHIVE is 'I/O Archive fuer die ein-/ausgehenden SOAP Messages von WITA';
comment on column T_IO_ARCHIVE.WITA_EXT_ORDER_NO is 'externe Auftragsnummer der WITA-Schnittstelle';
comment on column T_IO_ARCHIVE.REQUEST_TYPE is 'Typ des Requests (z.B. TAL-NEU)';
comment on column T_IO_ARCHIVE.REQUEST_TIMEST is 'Zeitpunkt, wann der Request erstellt wurde';
comment on column T_IO_ARCHIVE.REQUEST_XML is 'SOAP Message des Requests';
comment on column T_IO_ARCHIVE.RESPONSE_TYPE is 'Typ des Response (z.B. TEQ ok)';
comment on column T_IO_ARCHIVE.RESPONSE_TIMEST is 'Zeitpunkt, wann der Response uebermittelt wurde';
comment on column T_IO_ARCHIVE.RESPONSE_XML is 'SOAP Message des Response';

grant select, insert, update on  T_IO_ARCHIVE to R_HURRICAN_USER;
grant select on  T_IO_ARCHIVE to R_HURRICAN_READ_ONLY;

create sequence S_T_IO_ARCHIVE_0;
grant select on S_T_IO_ARCHIVE_0 to public;

