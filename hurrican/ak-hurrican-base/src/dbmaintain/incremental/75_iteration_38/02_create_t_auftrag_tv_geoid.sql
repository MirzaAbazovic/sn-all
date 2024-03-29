CREATE TABLE T_AUFTRAG_TV_GEO_ID (
    ID          number(19,0) not null,
    AUFTRAG_ID  number(19, 0) not null,
    GEO_ID      number(19, 0) not null,
    AKTIV_VON   DATE not null,
    AKTIV_BIS   DATE not null,
    USERW       VARCHAR2(8) NOT NULL,
    DATEW       DATE NOT NULL,
    VERSION     number(19) default 0 not null,
    PRIMARY KEY(ID)
);

ALTER TABLE T_AUFTRAG_TV_GEO_ID ADD CONSTRAINT FK_AUFTRAG_TV_GEO_ID_2_AUFTRAG
    FOREIGN KEY (AUFTRAG_ID)
    REFERENCES T_AUFTRAG(ID)
;

ALTER TABLE T_AUFTRAG_TV_GEO_ID ADD CONSTRAINT FK_AUFTRAG_TV_GEO_ID_2_GEOID
    FOREIGN KEY (GEO_ID)
    REFERENCES T_GEO_ID_CACHE(ID)
;

CREATE SEQUENCE S_T_AUFTRAG_TV_GEO_ID_0;

GRANT SELECT, INSERT, UPDATE ON T_AUFTRAG_TV_GEO_ID TO R_HURRICAN_USER;
GRANT SELECT ON T_AUFTRAG_TV_GEO_ID TO R_HURRICAN_READ_ONLY;
GRANT SELECT ON S_T_AUFTRAG_TV_GEO_ID_0 TO PUBLIC;
