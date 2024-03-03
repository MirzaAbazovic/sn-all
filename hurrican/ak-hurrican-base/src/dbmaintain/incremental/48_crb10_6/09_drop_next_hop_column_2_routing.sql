-- Enferne die Spalte NEXT_HOP_ID aus T_EG_ROUTING. Dort sollen keine IP-Adressen referenziert werden sondern Freitext gespeichert.

ALTER TABLE T_EG_ROUTING DROP COLUMN NEXT_HOP_ID;