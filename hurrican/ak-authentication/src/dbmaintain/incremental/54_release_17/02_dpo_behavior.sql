-- ANF-217.01 Hardware Administration anpassen
INSERT INTO COMPBEHAVIOR
(ID,
 COMP_ID,
 ROLE_ID,
 VISIBLE, EXECUTABLE, VERSION)
VALUES
  (s_compbehavior_0.nextval,
   (SELECT
      ID
    FROM GUICOMPONENT
    WHERE name = 'dpo.init' and parent = 'de.augustakom.hurrican.gui.hvt.HwDpoAdminPanel'),
   (SELECT
      id
    FROM ROLE
    WHERE name = 'admin.hvt'),
   '1', '1', 0);

INSERT INTO COMPBEHAVIOR
(ID,
 COMP_ID,
 ROLE_ID,
 VISIBLE, EXECUTABLE, VERSION)
VALUES
  (s_compbehavior_0.nextval,
   (SELECT
      ID
    FROM GUICOMPONENT
    WHERE name = 'dpo.modify' and parent = 'de.augustakom.hurrican.gui.hvt.HwDpoAdminPanel'),
   (SELECT
      id
    FROM ROLE
    WHERE name = 'admin.hvt'),
   '1', '1', 0);

INSERT INTO COMPBEHAVIOR
(ID,
 COMP_ID,
 ROLE_ID,
 VISIBLE, EXECUTABLE, VERSION)
VALUES
  (s_compbehavior_0.nextval,
   (SELECT
      ID
    FROM GUICOMPONENT
    WHERE name = 'dpo.delete'and parent = 'de.augustakom.hurrican.gui.hvt.HwDpoAdminPanel'),
   (SELECT
      id
    FROM ROLE
    WHERE name = 'admin.hvt'),
   '1', '1', 0);
