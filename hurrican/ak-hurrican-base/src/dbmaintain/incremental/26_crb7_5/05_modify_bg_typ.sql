update T_HW_BAUGRUPPEN_TYP set NAME='MA5652G_VDSL2' where ID=37;
update T_HW_BAUGRUPPEN_TYP set NAME='O-881V-P_VDSL2' where ID=172;
update T_HW_BAUGRUPPEN_TYP set NAME='VDTH' where ID=174;
update T_HW_BAUGRUPPEN_TYP set NAME='VDGE' where ID=175;
update T_HW_BAUGRUPPEN_TYP set NAME='ASPB' where ID=176;
update T_HW_BAUGRUPPEN_TYP set HW_SCHNITTSTELLE_NAME='VDSL2' where HW_SCHNITTSTELLE_NAME='VDSL';

update T_EQUIPMENT set HW_SCHNITTSTELLE='VDSL2' where HW_SCHNITTSTELLE='VDSL';
update T_EQUIPMENT set RANG_SS_TYPE='VDSL2' where RANG_SS_TYPE='VDSL';
update T_PHYSIKTYP set NAME='FTTB_VDSL2' where ID=800;
update T_PHYSIKTYP set NAME='FTTC_VDSL2' where ID=804;
update T_PHYSIKTYP set HW_SCHNITTSTELLE='VDSL2' where HW_SCHNITTSTELLE='VDSL';




