--
-- SQL-Befehle, um eine Auftragsmigration rueckgaengig zu machen
--

-- HURRICAN
update t_auftrag_daten set prodak_order__no=null where auftrag_id=?;
delete from t_auftrag_2_tech_ls where auftrag_id=?;


-- TAIFUN
delete from MLB_BILL_ITEM_200711 where CUSTOMER__NO=?;
delete from MLB_BILL_200711 where CUSTOMER__NO=?;

delete from AUFTRAG__CONNECT where AUFTRAG_NO=?;
delete from MLB_CHARGED_ITEM_INIT where ORDER__NO=?;
delete from MLB_CHARGED_ITEM where ORDER__NO=?;
delete from AUFTRAGPOS where ORDER__NO=?;
delete from DIS_DISCOUNT where ORDER__NO=?;
delete from AUFTRAG where AUFTRAG__NO=?;


-- aus allen Hurrican-Auftraege zu neuen Connect-Produkten die Taifun-Verbindung entfernen
update t_auftrag_daten set prodak_order__no=null where prod_id between 450 and 457;
delete from t_faktura where fnr_id>=8258;
delete from t_faktura_no where faktura_monat=110;
delete from t_faktura_nr where faktura_monat=110;

-- darf  N I C H T  in PRODUKTIVER Umgebung ausgefuehrt werden!!!
update t_rechnungsanschrift set r_info__no=null, ext_debitor_id=null; -- where id not in (5598);




delete from AUFTRAG__CONNECT where AUFTRAG_NO=1054732;
delete from MLB_CHARGED_ITEM_INIT where ORDER__NO=1054732;
delete from MLB_CHARGED_ITEM where ORDER__NO=1054732;
delete from AUFTRAGPOS where ORDER__NO=1054732;
delete from DIS_DISCOUNT where ORDER__NO=1054732;
delete from AUFTRAG where AUFTRAG__NO=1054732;

