-- Produktgruppen umbenennen (Prefix 'AK' entfernen)

update T_PRODUKTGRUPPE set PRODUKTGRUPPE='ADSL' where ID=3;
update T_PRODUKTGRUPPE set PRODUKTGRUPPE='ADSLplus' where ID=16;
update T_PRODUKTGRUPPE set PRODUKTGRUPPE='Centrex' where ID=14;
update T_PRODUKTGRUPPE set PRODUKTGRUPPE='Connect' where ID=2;
update T_PRODUKTGRUPPE set PRODUKTGRUPPE='Connect intern' where ID=9;
update T_PRODUKTGRUPPE set PRODUKTGRUPPE='Dial-In' where ID=10;
update T_PRODUKTGRUPPE set PRODUKTGRUPPE='Connect DSL' where ID=7;
update T_PRODUKTGRUPPE set PRODUKTGRUPPE='DSL-Line' where ID=12;
update T_PRODUKTGRUPPE set PRODUKTGRUPPE='Online' where ID=5;
update T_PRODUKTGRUPPE set PRODUKTGRUPPE='Payphone' where ID=13;
update T_PRODUKTGRUPPE set PRODUKTGRUPPE='Phone' where ID=1;
update T_PRODUKTGRUPPE set PRODUKTGRUPPE='SDSL' where ID=4;

