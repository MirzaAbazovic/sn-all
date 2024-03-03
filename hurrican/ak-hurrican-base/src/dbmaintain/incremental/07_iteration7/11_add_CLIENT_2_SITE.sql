create table T_CLIENT_2_SITE (
  ID                   NUMBER(10)               NOT NULL,
  AUFTRAG_ID           NUMBER(10)               NOT NULL,
  ADMIN_ZUGANG         CHAR(1)                  NOT NULL
);

COMMENT ON COLUMN T_CLIENT_2_SITE.ADMIN_ZUGANG IS 'Flag ob es sich um einen Admin-Zugang handelt';

CREATE SEQUENCE S_T_CLIENT_2_SITE_0 start with 1;
grant select on S_T_CLIENT_2_SITE_0 to public;

grant select on T_CLIENT_2_SITE to R_HURRICAN_READ_ONLY;
grant select, insert, update on T_CLIENT_2_SITE to R_HURRICAN_USER;
