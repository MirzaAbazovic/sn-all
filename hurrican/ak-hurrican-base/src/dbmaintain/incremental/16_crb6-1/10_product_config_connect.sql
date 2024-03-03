-- Connect-Produkte konfigurieren, damit die Standortadressen aus Taifun ermittelt werden

update T_PRODUKT set CREATE_AP_ADDRESS=0
 where PROD_ID in (450,451,452,453,454,455,456);
