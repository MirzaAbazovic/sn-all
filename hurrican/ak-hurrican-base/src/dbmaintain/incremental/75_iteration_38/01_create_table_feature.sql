CREATE TABLE T_FEATURE (
    ID      number(19,0) not null,
    NAME    varchar2(30) not null,
    FLAG    CHAR(1) default '0' not null,
    VERSION number(19) default 0 not null
);

alter table T_FEATURE add constraint PK_FEATURE primary key (ID);
alter table T_FEATURE add constraint UQ_FEATURE_NAME unique (NAME);

CREATE SEQUENCE S_T_FEATURE_0;

GRANT SELECT, INSERT, UPDATE ON T_FEATURE TO R_HURRICAN_USER;
GRANT SELECT ON T_FEATURE TO R_HURRICAN_READ_ONLY;
GRANT SELECT ON S_T_FEATURE_0 TO PUBLIC;

-- Insert ein Feature für den Automatismus-Check beim TAL-Abschluss
INSERT INTO T_FEATURE
(ID, NAME, FLAG)
VALUES
(S_T_FEATURE_0.NEXTVAL, 'ORDER AUTOMATION', '0');