--
-- Produktkonfiguration IPSec Site-to-Site aendern

update T_PRODUKT set ACCOUNT_VORS=null, LI_NR=null where PROD_ID=444;
update T_PRODUKT set ENDSTELLEN_TYP=0 where PROD_ID=444;
update T_PRODUKT set VPN_PHYSIK=0 where PROD_ID=444;
update T_PRODUKT set VERTEILUNG_DURCH=11 where PROD_ID=444;
update T_PRODUKT set IS_PARENT=0 where PROD_ID=444;
