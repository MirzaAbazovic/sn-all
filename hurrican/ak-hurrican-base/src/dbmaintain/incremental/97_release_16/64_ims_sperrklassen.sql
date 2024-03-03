-- name fuer vorhandene IMS sperrklassen
UPDATE T_SPERRKLASSEN SET NAME='Alle Verbindungen möglich (auch Premium-Rate-Dienste)' WHERE SPERRKLASSE_IMS=0;
UPDATE T_SPERRKLASSEN SET NAME='Sperre: Premium-Rate-Dienste1 (0900x) und Voting Rufnummern (0137x, 0138x)' WHERE SPERRKLASSE_IMS=2;
UPDATE T_SPERRKLASSEN SET NAME='Sperre: Premium-Rate-Dienste und nationale Mobilfunkrufnummern (015x, 016x, 017x)' WHERE SPERRKLASSE_IMS=3;
UPDATE T_SPERRKLASSEN SET NAME='Sperre: Premium-Rate-Dienste und Offline-Dienste (0181x .. 0189x, 0118xy)' WHERE SPERRKLASSE_IMS=4;
UPDATE T_SPERRKLASSEN SET NAME='Sperre: Premium-Rate-Dienste und Auslandspremium' WHERE SPERRKLASSE_IMS=5;
UPDATE T_SPERRKLASSEN SET NAME='Sperre: Premium-Rate-Dienste und alle Auslandsverbindungen (00x)' WHERE SPERRKLASSE_IMS=6;
UPDATE T_SPERRKLASSEN SET NAME='Nur nationales Festnetz möglich' WHERE SPERRKLASSE_IMS=7;
UPDATE T_SPERRKLASSEN SET NAME='Nur nationales Festnetz und nationale Mobilfunkrufnummern möglich' WHERE SPERRKLASSE_IMS=8;
UPDATE T_SPERRKLASSEN SET NAME='Sperre: Voting Rufnummern  (0137x, 0138x)' WHERE SPERRKLASSE_IMS=12;
UPDATE T_SPERRKLASSEN SET NAME='Sperre: nationale Mobilfunkrufnummern (015x,016x, 017x)' WHERE SPERRKLASSE_IMS=13;
UPDATE T_SPERRKLASSEN SET NAME='Sperre: Offlinedienste (0181x ..0189x, 0118xy)' WHERE SPERRKLASSE_IMS=14;
UPDATE T_SPERRKLASSEN SET NAME='Sperre Auslandspremium' WHERE SPERRKLASSE_IMS=15;
UPDATE T_SPERRKLASSEN SET NAME='Sperre: Ausland (00x)' WHERE SPERRKLASSE_IMS=16;
UPDATE T_SPERRKLASSEN SET NAME='Nur nationales Festnetz und Premium-Rate-Dienste möglich' WHERE SPERRKLASSE_IMS=17;
UPDATE T_SPERRKLASSEN SET NAME='Nur nationales Festnetz, nationale Mobilfunkrufnummern u. Premium-Rate-Dienste möglich' WHERE SPERRKLASSE_IMS=18;
UPDATE T_SPERRKLASSEN SET NAME='Sperre: Alle abgehenden Verbindungen (außer Notruf)' WHERE SPERRKLASSE_IMS=20;
UPDATE T_SPERRKLASSEN SET NAME='Sperre: M-net Standard = keine 0900 Premium' WHERE SPERRKLASSE_IMS=60;

-- vorhanden EWSD Sperrklasse mit IMS ID und name versehen
UPDATE T_SPERRKLASSEN SET SPERRKLASSE_IMS=21, NAME='Nur nationales Festnetz, nationale Mobilfunknummern (015x, 016x, 017x) und Ausland (00x) möglich'
    WHERE SPERRKLASSE=102;

-- neue Sperrklasse (akt. nur IMS)
INSERT INTO T_SPERRKLASSEN (ID,SPERRKLASSE,ABGEHEND,NATIONAL,INNOVATIVE_DIENSTE,MABEZ,MOBIL,VPN,PRD,AUSKUNFTSDIENSTE,
        INTERNATIONAL,OFF_LINE,VERSION,PREMIUM_SERVICES_INT,SPERRKLASSE_IMS,NAME)
VALUES (S_T_SPERRKLASSEN_0.nextVal,null,'0','0','0','0','0','0','0','0','0','0',0,'0',19,'Spezielle Sperrklasse sperrt bestimmte berüchtigte Destinationen im Ausland');
