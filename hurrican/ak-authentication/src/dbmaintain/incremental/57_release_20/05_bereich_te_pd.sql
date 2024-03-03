Insert into BEREICH
   (ID, NAME, BEREICHSLEITER, VERSION)
 Values
   (S_BEREICH_0.nextval, 'TE-PD', (SELECT id from Users where name = 'Luther' and firstname = 'Christoph'), 0);

update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Luther' and firstname = 'Christoph';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Weiß' and firstname = 'Michael';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Tebbe' and firstname = 'Alexander';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Haas' and firstname = 'Stefan';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Elfeky' and firstname = 'Alexander';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Chaoui' and firstname = 'Mohammed';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Tichy' and firstname = 'Ulrike';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Pacher' and firstname = 'Stefan';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Mujic' and firstname = 'Albin';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Hammerschmidt' and firstname = 'Ralf';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Kemmeter' and firstname = 'Stefan';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Sandgathe' and firstname = 'Peter';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Hübscher' and firstname = 'Cornelia';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Landgraf' and firstname = 'Heiko';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Brkljacic' and firstname = 'Igor';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Matheis' and firstname = 'Tobias';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Schütz' and firstname = 'Alexander';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Shu' and firstname = 'Min-Tzien';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Albrecht' and firstname = 'Stephan';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Dahms' and firstname = 'Tino';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Pfister' and firstname = 'Thomas';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Enthaler' and firstname = 'Daniel';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Tirpitz' and firstname = 'Gardis';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Fischer' and firstname = 'Simon';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Fischer' and firstname = 'Carl-Heinz';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'König' and firstname = 'Ulrike';
update users set bereich = (select id from Bereich where name = 'TE-PD') where name = 'Leßmann' and firstname = 'Moritz';
