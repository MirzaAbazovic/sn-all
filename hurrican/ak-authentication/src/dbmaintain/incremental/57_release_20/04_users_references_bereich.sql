-- ANF-469. Es werden nicht alle Bereiche gepflegt, von daher optional.

ALTER TABLE users ADD bereich NUMBER(19, 0);
ALTER TABLE users ADD CONSTRAINT fk_bereich FOREIGN KEY (bereich) REFERENCES bereich (ID);

