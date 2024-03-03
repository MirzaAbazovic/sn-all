-- Prio von Physiktypzuordnung FTTX Telefon auf FTTC_VDSL2 auf niedrigste Prio zurueck setzen
update t_produkt_2_physiktyp set priority=0 where prod_id=511 and physiktyp=804;
