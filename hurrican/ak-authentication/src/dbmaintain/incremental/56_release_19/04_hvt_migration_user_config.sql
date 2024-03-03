INSERT INTO COMPBEHAVIOR
(ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
VALUES
  (S_COMPBEHAVIOR_0.nextVal,
   (SELECT id
    FROM GUICOMPONENT
    WHERE name = 'hvt.umzug.action' AND type = 'MenuItem'),
   (SELECT id
    FROM ROLE
    WHERE name = 'admin.hvt'), '1', '1', 0);