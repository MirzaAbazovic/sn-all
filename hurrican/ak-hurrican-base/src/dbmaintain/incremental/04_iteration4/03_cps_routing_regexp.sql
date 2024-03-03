
-- Beispiele
--Routing auf 08216507820
--Routing auf 082165078222
--Routing auf 0821/450129-30
--Routing auf 0821/450129-30 djfskl
--Routing auf 0171/6114626(bei Nichtmelden nach 6 Rufen)


insert into T_CFG_REG_EXP (ID, REF_ID, REF_NAME, REF_CLASS, REQUESTED_INFO, REGULAR_EXP, MATCH_GROUP, DESCRIPTION)
  values (4, null, 'ROUTING_DESTINATION', 'de.augustakom.hurrican.model.billing.Rufnummer', 'PHONE_NUMBER',
  '^\D*\s?\D*\s?(0\d*\s?[\/\\]?\s?\d*[-]?\d*)\n?.*$', 0,
  'Filtert aus einem Text eine Telefonnummer. Sonderzeichen muessen anschliessend ersetzt werden!');



