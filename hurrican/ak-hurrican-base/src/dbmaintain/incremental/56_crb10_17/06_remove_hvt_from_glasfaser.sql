-- HVT-Zuordnung von Produkten 512/513 entfernen 
delete from t_produkt_2_tech_location_type where prod_id in (512,513) and tech_location_type_ref_id = 11000;
