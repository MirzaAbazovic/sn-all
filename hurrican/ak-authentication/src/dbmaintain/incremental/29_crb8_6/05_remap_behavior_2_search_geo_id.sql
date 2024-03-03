-- Button zum Suchen der Strassen nach Button zum Suchen der Geo IDs migriert.
-- Rollen des alten Buttons auf neuen Button übernehmen.

update GUICOMPONENT set NAME='search.geo.id' where NAME='strasse.suchen' and PARENT='de.augustakom.hurrican.gui.auftrag.AuftragEndstellenPanel';