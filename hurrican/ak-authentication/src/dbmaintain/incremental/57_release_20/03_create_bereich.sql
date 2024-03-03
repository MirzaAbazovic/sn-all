-- notwendig fuer ANF-469. wird nicht fuer alle Bereiche gepflegt.
CREATE TABLE bereich (
  id             NUMBER(19),
  name           VARCHAR(32)             NOT NULL,
  bereichsleiter NUMBER(19, 0)           NOT NULL,
  version        NUMBER(19, 0) DEFAULT 0 NOT NULL,
  CONSTRAINT pk_bereich PRIMARY KEY (id),
  CONSTRAINT fk_bleiter_to_user FOREIGN KEY (bereichsleiter) REFERENCES users (id)
);

CREATE SEQUENCE S_BEREICH_0 START WITH 1 INCREMENT BY 1;

GRANT SELECT ON S_BEREICH_0 TO PUBLIC;
GRANT SELECT, INSERT, UPDATE ON bereich TO R_AUTHENTICATION_USER;


