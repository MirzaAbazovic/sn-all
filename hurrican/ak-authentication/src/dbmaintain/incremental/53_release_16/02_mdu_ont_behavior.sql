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
    WHERE name = 'mdu.init'),
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
    WHERE name = 'ont.init'),
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
    WHERE name = 'ont.modify'),
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
    WHERE name = 'ont.delete'),
   (SELECT
      id
    FROM ROLE
    WHERE name = 'admin.hvt'),
   '1', '1', 0);
