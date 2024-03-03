-- add new flag for products which are able to determine an hurrican order bundle over the billing
-- main/sub order logic
ALTER TABLE T_PRODUKT ADD BUENDEL_BILLING_HAUPTAUFTRAG CHAR(1) DEFAULT '0' NOT NULL;
COMMENT ON COLUMN T_PRODUKT.BUENDEL_BILLING_HAUPTAUFTRAG
IS 'Flag gibt an, ob aus der Billing-Haupt-/Unterauftragslogik ein Hurrican-Auftragsbündel ermittelt wird.';

-- enable the flag for the TV-Signallieferungsprodukten
UPDATE T_PRODUKT
SET BUENDEL_BILLING_HAUPTAUFTRAG = '1'
WHERE PROD_ID IN (521, 522) AND ANSCHLUSSART LIKE 'TV Signal%';

