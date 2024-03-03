CREATE TABLE T_HW_BGCHANGE_CARD_2_BG (
  HW_BG_CHANGE_CARD_ID NUMBER(19) NOT NULL,
  HW_BAUGRUPPEN_ID_NEW NUMBER(19) NOT NULL,
  CONSTRAINT UK_HW_BGCHANGE_CARD_2_BG UNIQUE (HW_BG_CHANGE_CARD_ID, HW_BAUGRUPPEN_ID_NEW),
  CONSTRAINT FK_HW_BGCC_2_BG_CCARD_ID FOREIGN KEY (HW_BG_CHANGE_CARD_ID) REFERENCES T_HW_BG_CHANGE_CARD (ID),
  CONSTRAINT FK_HW_BGCC_2_BG_BG_ID FOREIGN KEY (HW_BAUGRUPPEN_ID_NEW) REFERENCES T_HW_BAUGRUPPE (ID)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON T_HW_BGCHANGE_CARD_2_BG TO R_HURRICAN_USER;
GRANT SELECT ON T_HW_BGCHANGE_CARD_2_BG TO R_HURRICAN_READ_ONLY;

CREATE PROCEDURE migrate_to_T_HW_BGCH_CARD_2_BG IS
  BEGIN
    FOR rec IN (SELECT
                  id,
                  hw_baugruppen_id_new
                FROM T_HW_BG_CHANGE_CARD
                WHERE hw_baugruppen_id_new IS NOT NULL) LOOP
      INSERT INTO T_HW_BGCHANGE_CARD_2_BG (HW_BG_CHANGE_CARD_ID, HW_BAUGRUPPEN_ID_NEW)
      VALUES (rec.ID, rec.HW_BAUGRUPPEN_ID_NEW);
    END LOOP;
  END;
/

CALL migrate_to_T_HW_BGCH_CARD_2_BG();
DROP PROCEDURE migrate_to_T_HW_BGCH_CARD_2_BG;

ALTER TABLE T_HW_BG_CHANGE_CARD DROP COLUMN HW_BAUGRUPPEN_ID_NEW;