alter table users add projektleiter char(1);
update users set projektleiter='0';
alter table users modify projektleiter not null;

update users set projektleiter='1' where DEP_ID=11;

update users set projektleiter='1' where loginname='vpn.team.muc';

update users set projektleiter='1' where loginname='weissmi';
update users set projektleiter='1' where loginname='Garbe';
update users set projektleiter='1' where loginname='WendtSi';
update users set projektleiter='1' where loginname='Dhamigu';
update users set projektleiter='1' where loginname='weiss';
update users set projektleiter='1' where loginname='Steilan';
update users set projektleiter='1' where loginname='hanke';
update users set projektleiter='1' where loginname='koehler';
update users set projektleiter='1' where loginname='effenbergerkl';
update users set projektleiter='1' where loginname='mujic';
update users set projektleiter='1' where loginname='landgrafhe';
update users set projektleiter='1' where loginname='hammerschmidt';
update users set projektleiter='1' where loginname='walinski';
update users set projektleiter='1' where loginname='pacher';
update users set projektleiter='1' where loginname='schuetzal';
update users set projektleiter='1' where loginname='shu';
update users set projektleiter='1' where loginname='ecksteinma';



