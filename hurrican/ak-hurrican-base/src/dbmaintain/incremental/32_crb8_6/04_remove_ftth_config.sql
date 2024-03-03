-- Zuordnungen zu Produkten 530/531 (SIP InterTrunk) entfernen
delete from T_PRODUKT_2_TECH_LOCATION_TYPE where prod_id in (530,531);