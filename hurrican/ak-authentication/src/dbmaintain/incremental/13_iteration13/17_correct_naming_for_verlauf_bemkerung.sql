-- delete old sctk
DELETE FROM COMPBEHAVIOR WHERE COMP_ID IN (SELECT ID FROM GUICOMPONENT
    WHERE NAME IN ('verlauf.abteilung.status.SCTK', 'verlauf.bemerkung.SCTK', 'verlauf.wiedervorlage.SCTK'));
DELETE FROM GUICOMPONENT WHERE NAME IN ('verlauf.abteilung.status.SCTK', 'verlauf.bemerkung.SCTK', 'verlauf.wiedervorlage.SCTK');

-- update sdh/stconnect
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.abteilung.status.1',
        DESCRIPTION = 'Status des Bauauftrags bzw. der Projektierung von ST Connect'
    WHERE NAME = 'verlauf.abteilung.status.ST Connect';
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.bemerkung.1',
        DESCRIPTION = REGEXP_REPLACE(
            (SELECT DESCRIPTION FROM GUICOMPONENT WHERE NAME = 'verlauf.bemerkung.SDH'), '(.*)SDH(.*)', '\1ST Connect\2')
    WHERE NAME = 'verlauf.bemerkung.ST Connect';
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.wiedervorlage.1',
        DESCRIPTION = 'Wiedervorlage für den Bauauftrags bzw. die Projektierung bei ST Connect'
    WHERE NAME = 'verlauf.wiedervorlage.ST Connect';
DELETE FROM COMPBEHAVIOR WHERE COMP_ID IN (SELECT ID FROM GUICOMPONENT
    WHERE NAME IN ('verlauf.abteilung.status.SDH', 'verlauf.bemerkung.SDH', 'verlauf.wiedervorlage.SDH'));
DELETE FROM GUICOMPONENT WHERE NAME IN ('verlauf.abteilung.status.SDH', 'verlauf.bemerkung.SDH', 'verlauf.wiedervorlage.SDH');

-- update ips/stonline
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.abteilung.status.2',
        DESCRIPTION = 'Status des Bauauftrags bzw. der Projektierung von ST Online'
    WHERE NAME = 'verlauf.abteilung.status.ST Online';
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.bemerkung.2',
        DESCRIPTION = REGEXP_REPLACE(
            (SELECT DESCRIPTION FROM GUICOMPONENT WHERE NAME = 'verlauf.bemerkung.IPS'), '(.*)IPS(.*)', '\1ST Online\2')
    WHERE NAME = 'verlauf.bemerkung.ST Online';
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.wiedervorlage.2',
        DESCRIPTION = 'Wiedervorlage für den Bauauftrags bzw. die Projektierung bei ST Online'
    WHERE NAME = 'verlauf.wiedervorlage.ST Online';
DELETE FROM COMPBEHAVIOR WHERE COMP_ID IN (SELECT ID FROM GUICOMPONENT
    WHERE NAME IN ('verlauf.abteilung.status.IPS', 'verlauf.bemerkung.IPS', 'verlauf.wiedervorlage.IPS'));
DELETE FROM GUICOMPONENT WHERE NAME IN ('verlauf.abteilung.status.IPS', 'verlauf.bemerkung.IPS', 'verlauf.wiedervorlage.IPS');

-- update ewsd/stvoice
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.abteilung.status.3',
        DESCRIPTION = 'Status des Bauauftrags bzw. der Projektierung von ST Voice'
    WHERE NAME = 'verlauf.abteilung.status.ST Voice';
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.bemerkung.3',
        DESCRIPTION = REGEXP_REPLACE(
            (SELECT DESCRIPTION FROM GUICOMPONENT WHERE NAME = 'verlauf.bemerkung.EWSD'), '(.*)EWSD(.*)', '\1ST Voice\2')
    WHERE NAME = 'verlauf.bemerkung.ST Voice';
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.wiedervorlage.3',
        DESCRIPTION = 'Wiedervorlage für den Bauauftrags bzw. die Projektierung bei ST Voice'
    WHERE NAME = 'verlauf.wiedervorlage.ST Voice';
DELETE FROM COMPBEHAVIOR WHERE COMP_ID IN (SELECT ID FROM GUICOMPONENT
    WHERE NAME IN ('verlauf.abteilung.status.EWSD', 'verlauf.bemerkung.EWSD', 'verlauf.wiedervorlage.EWSD'));
DELETE FROM GUICOMPONENT WHERE NAME IN ('verlauf.abteilung.status.EWSD', 'verlauf.bemerkung.EWSD', 'verlauf.wiedervorlage.EWSD');

-- update dispo
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.abteilung.status.4',
        DESCRIPTION = 'Status des Bauauftrags bzw. der Projektierung von DISPO'
    WHERE NAME = 'verlauf.abteilung.status.DISPO';
UPDATE GUICOMPONENT SET NAME = 'verlauf.bemerkung.4' WHERE NAME = 'verlauf.bemerkung.DISPO';
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.wiedervorlage.4',
        DESCRIPTION = 'Wiedervorlage für den Bauauftrags bzw. die Projektierung bei DISPO'
    WHERE NAME = 'verlauf.wiedervorlage.DISPO';

-- update sct/fieldservice
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.abteilung.status.5',
        DESCRIPTION = 'Status des Bauauftrags bzw. der Projektierung von FieldService'
    WHERE NAME = 'verlauf.abteilung.status.FieldService';
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.bemerkung.5',
        DESCRIPTION = REGEXP_REPLACE(
            (SELECT DESCRIPTION FROM GUICOMPONENT WHERE NAME = 'verlauf.bemerkung.SCT'), '(.*)SCT(.*)', '\1FieldService\2')
    WHERE NAME = 'verlauf.bemerkung.FieldService';
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.wiedervorlage.5',
        DESCRIPTION = 'Wiedervorlage für den Bauauftrags bzw. die Projektierung bei FieldService'
    WHERE NAME = 'verlauf.wiedervorlage.FieldService';
DELETE FROM COMPBEHAVIOR WHERE COMP_ID IN (SELECT ID FROM GUICOMPONENT
    WHERE NAME IN ('verlauf.abteilung.status.SCT', 'verlauf.bemerkung.SCT', 'verlauf.wiedervorlage.SCT'));
DELETE FROM GUICOMPONENT WHERE NAME IN ('verlauf.abteilung.status.SCT', 'verlauf.bemerkung.SCT', 'verlauf.wiedervorlage.SCT');

-- update scv/am
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.abteilung.status.7',
        DESCRIPTION = 'Status des Bauauftrags bzw. der Projektierung von AM'
    WHERE NAME = 'verlauf.abteilung.status.AM';
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.bemerkung.7',
        DESCRIPTION = REGEXP_REPLACE(
            (SELECT DESCRIPTION FROM GUICOMPONENT WHERE NAME = 'verlauf.bemerkung.SCV'), '(.*)SCV(.*)', '\1AM\2')
    WHERE NAME = 'verlauf.bemerkung.AM';
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.wiedervorlage.7',
        DESCRIPTION = 'Wiedervorlage für den Bauauftrags bzw. die Projektierung bei AM'
    WHERE NAME = 'verlauf.wiedervorlage.AM';
DELETE FROM COMPBEHAVIOR WHERE COMP_ID IN (SELECT ID FROM GUICOMPONENT
    WHERE NAME IN ('verlauf.abteilung.status.SCV', 'verlauf.bemerkung.SCV', 'verlauf.wiedervorlage.SCV'));
DELETE FROM GUICOMPONENT WHERE NAME IN ('verlauf.abteilung.status.SCV', 'verlauf.bemerkung.SCV', 'verlauf.wiedervorlage.SCV');

-- update np
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.abteilung.status.11',
        DESCRIPTION = 'Status des Bauauftrags bzw. der Projektierung von NP'
    WHERE NAME = 'verlauf.abteilung.status.NP';
UPDATE GUICOMPONENT SET NAME = 'verlauf.bemerkung.11' WHERE NAME = 'verlauf.bemerkung.NP';
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.wiedervorlage.11',
        DESCRIPTION = 'Wiedervorlage für den Bauauftrags bzw. die Projektierung bei NP'
    WHERE NAME = 'verlauf.wiedervorlage.NP';

-- update extern
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.abteilung.status.12',
        DESCRIPTION = 'Status des Bauauftrags bzw. der Projektierung von Extern'
    WHERE NAME = 'verlauf.abteilung.status.Extern';
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.bemerkung.12',
        DESCRIPTION = 'TextArea für die Bauauftrags-
bzw. Projektierungsbemerk.
von Extern'
    WHERE NAME = 'verlauf.bemerkung.Extern';
UPDATE GUICOMPONENT
    SET NAME = 'verlauf.wiedervorlage.12',
        DESCRIPTION = 'Wiedervorlage für den Bauauftrags bzw. die Projektierung bei Extern'
    WHERE NAME = 'verlauf.wiedervorlage.Extern';
