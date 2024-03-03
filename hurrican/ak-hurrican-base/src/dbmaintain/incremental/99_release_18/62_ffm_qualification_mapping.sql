CREATE TABLE T_FFM_QUALIFICATION_MAPPING (
     ID NUMBER(19, 0) PRIMARY KEY NOT NULL,
     QUALIFICATION_ID NUMBER(19, 0) NOT NULL,
     PRODUCT_ID NUMBER(19, 0),
     TECH_LEISTUNG_ID NUMBER(19, 0),
     STANDORT_REF_ID NUMBER(19, 0)
);

ALTER TABLE T_FFM_QUALIFICATION_MAPPING ADD CONSTRAINT UK_FFM_QUALMAP_IDS UNIQUE (QUALIFICATION_ID, PRODUCT_ID, TECH_LEISTUNG_ID, STANDORT_REF_ID);

ALTER TABLE T_FFM_QUALIFICATION_MAPPING ADD CONSTRAINT FK_FFM_QUALMAP_2_QUAL FOREIGN KEY (QUALIFICATION_ID) REFERENCES T_FFM_QUALIFICATION (ID);
ALTER TABLE T_FFM_QUALIFICATION_MAPPING ADD CONSTRAINT FK_FFM_QUALMAP_2_PROD FOREIGN KEY (PRODUCT_ID) REFERENCES T_PRODUKT (PROD_ID);
ALTER TABLE T_FFM_QUALIFICATION_MAPPING ADD CONSTRAINT FK_FFM_QUALMAP_2_LEIST FOREIGN KEY (TECH_LEISTUNG_ID) REFERENCES T_TECH_LEISTUNG (ID);
ALTER TABLE T_FFM_QUALIFICATION_MAPPING ADD CONSTRAINT FK_FFM_QUALMAP_2_STDREF FOREIGN KEY (STANDORT_REF_ID) REFERENCES T_REFERENCE (ID);

ALTER TABLE T_FFM_QUALIFICATION_MAPPING ADD CONSTRAINT CK_FFM_QUALMAP_IDS CHECK ((PRODUCT_ID IS NOT NULL) OR (TECH_LEISTUNG_ID IS NOT NULL) OR (STANDORT_REF_ID IS NOT NULL));

GRANT SELECT, INSERT, UPDATE ON T_FFM_QUALIFICATION_MAPPING TO R_HURRICAN_USER;
GRANT SELECT ON T_FFM_QUALIFICATION_MAPPING TO R_HURRICAN_READ_ONLY;

CREATE SEQUENCE S_T_FFM_QUAL_MAPPING_0;
GRANT SELECT ON S_T_FFM_QUAL_MAPPING_0 TO PUBLIC;

-- move tech leistung qualification mappings to new mapping table
CREATE OR REPLACE PROCEDURE move_tech_leistung_mappings
IS
  BEGIN
    FOR mapping IN (SELECT tl.ID, tl.FFM_QUALIFICATION_ID
                    FROM T_TECH_LEISTUNG tl
                    WHERE tl.FFM_QUALIFICATION_ID IS NOT NULL)
    LOOP
      INSERT INTO T_FFM_QUALIFICATION_MAPPING (ID, QUALIFICATION_ID, TECH_LEISTUNG_ID)
      VALUES (S_T_FFM_QUAL_MAPPING_0.nextval, mapping.FFM_QUALIFICATION_ID, mapping.ID);
    END LOOP;
  END;
/
CALL move_tech_leistung_mappings();
DROP PROCEDURE move_tech_leistung_mappings;