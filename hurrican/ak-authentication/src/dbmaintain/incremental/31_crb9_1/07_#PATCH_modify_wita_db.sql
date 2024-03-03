-- WITA DB in auf die richtige Datenbank gehen lassen

UPDATE   DB D
   SET   D.URL =
            (SELECT   DU.URL
               FROM   DB DU
              WHERE   DU.ID = 4),
         D.SCHEMA =
            (SELECT   DS.SCHEMA
               FROM   DB DS
              WHERE   DS.ID = 4)
 WHERE   D.ID = 5;