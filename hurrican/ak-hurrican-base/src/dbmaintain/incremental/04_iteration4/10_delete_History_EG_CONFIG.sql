delete from t_eg_config where gueltig_bis < sysdate;
ALTER TABLE t_eg_config DROP COLUMN GUELTIG_VON;
ALTER TABLE t_eg_config DROP COLUMN GUELTIG_BIS;

delete from t_port_forward where gueltig_bis < sysdate;
ALTER TABLE T_PORT_FORWARD DROP COLUMN GUELTIG_VON;
ALTER TABLE T_PORT_FORWARD DROP COLUMN GUELTIG_BIS;
