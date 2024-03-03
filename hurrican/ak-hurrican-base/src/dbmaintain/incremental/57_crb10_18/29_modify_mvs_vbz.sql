-- MVS Enterprise+Site - VBZ Prefix changed to CX
update t_produkt set TDN_KIND_OF_USE_PRODUCT='C', TDN_KIND_OF_USE_TYPE='X' where PROD_ID in (535,536);

-- auch MVS Site Auftrag ans Portal exportieren
update t_produkt set EXPORT_KDP_M='1' where PROD_ID=536;
