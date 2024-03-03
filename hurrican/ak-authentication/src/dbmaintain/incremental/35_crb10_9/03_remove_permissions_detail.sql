DELETE FROM COMPBEHAVIOR
      WHERE ROLE_ID = 9 AND "VISIBLE" = '1' AND EXECUTABLE = '1'
            AND COMP_ID =
                   (SELECT ID
                      FROM GUICOMPONENT
                     WHERE name = 'detail'
                           AND parent =
                                  'de.augustakom.hurrican.gui.tools.tal.AbgebendeLeitungenPanel');

DELETE FROM GUICOMPONENT
      WHERE NAME = 'detail'
            AND PARENT =
                   'de.augustakom.hurrican.gui.tools.tal.AbgebendeLeitungenPanel'
            AND "TYPE" = 'Button';
