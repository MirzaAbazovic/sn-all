##############################################################################
#
# Inhalt von migration_kup
#
##############################################################################

In diesem Ordner sind SQL-Skripte fuer die Migration der Datenbank KuP nach
Hurrican aufgenommen.


Migrations-Ablauf:
==================

System	Script/Befehl					Auswirkung
ORA		01_base_hersteller.sql			Legt die notwendigen Technik-Lieferanten an
ORA     02_modify_hvt.sql               Aendert die bestehende HVT-Tabelle
ORA     03_base_reference.sql           Legt verschiedene Referenz-Daten an
ORA     04_base_physictypes.sql         Legt neue / notwendige Physiktypen an
ORA     


