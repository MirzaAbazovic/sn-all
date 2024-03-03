DELETE FROM COMPBEHAVIOR
WHERE comp_id IN (SELECT
                    ID
                  FROM GUICOMPONENT
                  WHERE name = 'tal.interne.bestellung.action');
DELETE FROM GUICOMPONENT
WHERE name = 'tal.interne.bestellung.action';
