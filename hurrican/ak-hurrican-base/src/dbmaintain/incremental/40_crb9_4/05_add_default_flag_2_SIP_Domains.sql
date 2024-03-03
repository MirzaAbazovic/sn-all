-- Produkt2SIPDomain um default Flag ergaenzen
ALTER TABLE T_PROD_2_SIP_DOMAIN ADD DEFAULT_DOMAIN CHAR(1);

COMMENT ON COLUMN T_PROD_2_SIP_DOMAIN.DEFAULT_DOMAIN IS 'Zeigt an, dass dies die Default Domäne des Switches ist.';

