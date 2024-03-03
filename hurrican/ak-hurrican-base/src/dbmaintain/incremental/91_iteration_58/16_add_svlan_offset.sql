-- SVLAN_OFFSET hinzufuegen (Alcatel Olts berechnen das SVLANOlt fuer CVLANs, welche als Protokoll IPoE mitbringen, teils
-- mit Offset teils ohne)
ALTER TABLE T_HW_RACK_OLT ADD (SVLAN_OFFSET NUMBER(10));

UPDATE T_HW_RACK_OLT set SVLAN_OFFSET = 2000 WHERE RACK_ID IN (SELECT DISTINCT OLT.RACK_ID FROM T_HW_RACK_OLT OLT
                                                                        INNER JOIN T_HW_RACK RACK ON RACK.ID = OLT.RACK_ID
                                                                        INNER JOIN T_HVT_TECHNIK T ON T.ID = RACK.HW_PRODUCER
                                                                        WHERE T.ID = 6);
