update T_PRODUKT 
set ANSCHLUSSART = 'DSL + ISDN', PROD_NAME_PATTERN = 'DSL {DOWNSTREAM} ISDN' 
where PROD_ID = 420;

update T_PRODUKT 
set ANSCHLUSSART = 'DSL + analog', PROD_NAME_PATTERN = 'DSL {DOWNSTREAM} analog' 
where PROD_ID = 421;

update T_PRODUKT 
set ANSCHLUSSART = 'DSL only', PROD_NAME_PATTERN = 'DSL only {DOWNSTREAM}' 
where PROD_ID = 440;

update T_PRODUKT 
set ANSCHLUSSART = 'FTTX Telefon', PROD_NAME_PATTERN = 'FTTX Telefon' 
where PROD_ID = 511;

update T_PRODUKT 
set ANSCHLUSSART = 'FTTX DSL + Fon', PROD_NAME_PATTERN = 'FTTX DSL + Fon {DOWNSTREAM}' 
where PROD_ID = 513;

update T_PRODUKT 
set ANSCHLUSSART = 'FTTX DSL', PROD_NAME_PATTERN = 'FTTX DSL {DOWNSTREAM}' 
where PROD_ID = 512;