alter table T_AUFTRAG_VOIP_DN_PLAN add (GUELTIG_AB_TMP DATE);

update T_AUFTRAG_VOIP_DN_PLAN set GUELTIG_AB_TMP = trunc(GUELTIG_AB);

alter table T_AUFTRAG_VOIP_DN_PLAN drop column GUELTIG_AB;

alter table T_AUFTRAG_VOIP_DN_PLAN rename column GUELTIG_AB_TMP to GUELTIG_AB;

alter table T_AUFTRAG_VOIP_DN_PLAN modify GUELTIG_AB not null;
