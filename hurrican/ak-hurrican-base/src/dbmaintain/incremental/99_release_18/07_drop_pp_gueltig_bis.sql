-- SBC IP Sets ohne gueltig_bis, da ueberfluessig
ALTER TABLE T_SIP_SBC_IP_SET DROP COLUMN GUELTIG_BIS;
ALTER TABLE T_SIP_SBC_IP_SET RENAME COLUMN GUELTIG_VON TO GUELTIG_AB;