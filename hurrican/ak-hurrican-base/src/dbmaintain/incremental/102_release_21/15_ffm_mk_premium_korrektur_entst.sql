-- HUR-24665 Entstoerungen sollen unabhaengig vom Produkt den Activity Typ 'RTL_Entstoerung' bekommen und eine
-- Planned Duration von 90 Minuten aufweisen. Hierzu gibt es die bereits existierenden Mappings auf lediglich die
-- Standorttypen mit BA_FFM_TYP = 'ENTSTOERUNG'. Als konsequenz duerfen die Entstoerungen mit Activit Typ
-- 'RTL_NEU_MK_Premium' ersatzlos entfernt werden.
-- Folgende Produktkonfigurationen werden entfernt:
-- Prod IDs: 54,63,64,66,67,68,69,99,312,313,541

DELETE FROM T_FFM_PRODUCT_MAPPING WHERE FFM_ACTIVITY_TYPE = 'RTL_NEU_MK_Premium' AND BA_FFM_TYP = 'ENTSTOERUNG';
