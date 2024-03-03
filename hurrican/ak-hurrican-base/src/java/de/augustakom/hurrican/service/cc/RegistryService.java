/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2005 13:23:02
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.Registry;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Registry-Service. <br> Ueber diesen Service koennen diverse 'Registry'-Informationen vom CC-System abgefragt werden.
 */
public interface RegistryService extends ICCService {

    /**
     * Registry-ID fuer einen String-Wert, der den Pfad zum Exportfile fuer die Auftragsdaten fuer das Kundenportal
     * enthaelt.
     */
    Long REGID_EXPORTFILE_KUNDENPORTAL = 20L;
    /**
     * Registry-ID fuer den Scanview-Datenbank für die Report-Archivierung
     */
    Long REGID_SCANVIEW_DATABASE = 27L;
    /**
     * Registry-ID fuer die Karenzzeit zur Ermittlung des Freigabedatums der IP Adressen
     */
    Long REGID_IP_FREIGABE_KARENZZEIT = 60L;
    /**
     * Registry-ID fuer das IPV6 Protokollflag im Zuweisungsdialog
     */
    Long REGID_IPV6_ASSIGNMENT_FLAG = 61L;
    /**
     * Registry-ID fuer den SAP-Buchungskreis
     */
    Long REGID_SAP_BUCHUNGSKREIS = 100L;
    /**
     * Registry-ID fuer SAP-User
     */
    Long REGID_SAP_USER = 101L;
    /**
     * Registry-ID fuer SAP-Passwort
     */
    Long REGID_SAP_PASSWORT = 102L;
    /**
     * Registry-ID fuer den DSL Downstream Schwellwert
     */
    Long REGID_DSL_DOWNSTREAM_THRESHOLD = 250L;
    /**
     * Registry-ID fuer den Default-Wert der EMail-Adresse fuer Signatur-Infos
     */
    Long REGID_SIGNATURE_EMAIL = 1006L;
    /**
     * Registry-ID fuer den Scheduler-Namen fuer eine RMI-Verbindung
     */
    Long REGID_SCHEDULER_NAME = 1100L;
    /**
     * Registry-ID fuer den Zugriff auf den Namen des Kopier-Jobs.
     */
    Long REGID_SCHEDULER_COPY_INVOICE_JOB = 1110L;
    /**
     * Registry-ID fuer den Zugriff auf den Namen des Rechnungs-Export Job.
     */
    Long REGID_SCHEDULER_EXPORT_EVN_JOB = 1111L;
    /**
     * Registry-ID fuer den Zugriff auf den Namen des Rechnungs-Export Job.
     */
    Long REGID_SCHEDULER_EXPORT_ARCHIVE_JOB = 1112L;
    /**
     * Registry-ID fuer den Zugriff auf den Namen des Ressourcenmonitor-Job.
     */
    Long REGID_SCHEDULER_RESSOURCEN_MONITOR_JOB = 1150L;
    /**
     * Registry-ID fuer den Zugriff auf die Email-Adressen fuer die Alarmierung des Rangierungsmonitors.
     */
    Long REGID_RESSOURCEN_MONITOR_ALARM_RANG = 1151L;
    /**
     * Registry-ID fuer den Zugriff auf die Email-Adressen fuer die Alarmierung des EQ-Monitors.
     */
    Long REGID_RESSOURCEN_MONITOR_ALARM_EQ = 1152L;
    /**
     * Registry-ID fuer den Schwellwert Standardprojete/Grossprojekte.
     */
    Long REGID_IA_BUDGET_PROJEKTE_SCHWELLWERT = 1200L;
    /**
     * Registry-ID fuer die Auswahl der Ebene 3 fuer PD als Standardprojekt.
     */
    Long REGID_IA_BUDGET_EBENE3_PD_STANDARDPROJEKTE_GK = 1201L;
    /**
     * Registry-ID fuer die Auswahl der Ebene 3 fuer PD als Grossprojekt.
     */
    Long REGID_IA_BUDGET_EBENE3_PD_GROSSPROJEKTE_GK = 1202L;
    /**
     * Registry-ID des Postfaches fuer den Versand der automatisch generierten PDFs.
     */
    Long REGID_IA_BUDGET_PD_VERSAND_PDF = 1203L;
    /**
     * Registry-ID des Postfaches fuer den Versand der automatisch generierten PDFs.
     */
    Long REGID_IA_BUDGET_PD_BEREICH_NAME = 1204L;
    /**
     * Registry-ID des Postfaches fuer den Versand der automatisch generierten PDFs.
     */
    Long REGID_IA_BUDGET_PD_BEREICH_KOSTENSTELLE = 1205L;
    /**
     * Registry-ID fuer die EMail-Adressen fuer Job-Benachrichtungen im CPS-Bereich.
     */
    Long REGID_CPS_MAIL_4_JOBS = 4050L;
    /**
     * Registry-ID fuer die EMail-Adressen fuer Job-Benachrichtungen beim HVT-Umzug.
     */
    Long REGID_HVT_UMZUG_INFO_MAIL = 4052L;
    /**
     * Registry-ID fuer die EMail-Adressen fuer Fehlermeldungen bei der Prozessierung von AUTO_EXPIRE Leistungen.
     */
    Long REGID_AUTO_EXPIRE_MAIL = 4053L;
    /**
     * Registry-ID fuer einen String-Wert, der die ;-getrennten EMail-Adressen fuer die Abteilung DISPO enthaelt.
     */
    Long REGID_EMAILS_DISPO = 5000L;
    /**
     * Registry-ID fuer einen String-Wert, der die ;-getrennten EMail-Adressen fuer die Abteilung Fibu enthaelt.
     */
    Long REGID_EMAIL_FIBU = 5003L;
    /**
     * Registry-ID fuer einen String-Wert, der die eMail-Adressen fuer die Mitteilung 'Inverssuche' an AMs enthaelt.
     */
    Long REGID_EMAIL_INVERS_AM = 5004L;
    /**
     * Registry-ID deren IntValue die max Ueberwachungsdauer der DSLAMProfile von TAL 18000 Auftraegen enthaelt
     */
    Long REGID_DSLAMPROFILEMONITOR_DURATION = 70L;
    /**
     * Registry-ID fuer die SMS-Absenderkennung (From)
     */
    long REGID_SMS_FROM = 80L;
    /**
     * Registry-ID fuer die Customer-Email-Absenderkennung (bei WITA/WBCI-Rueckmeldung)  (From)
     */
    Long REGID_CUSTOMER_EMAIL_FROM = 81L;
    /**
     * Registry-ID fuer eine Hurrican-Absenderadresse (From)
     */
    long REGID_MAIL_FROM_HURRICAN = 5050L;
    /**
     * Registry-ID fuer die WBCI-Eskalationsreport-Empfängeradresse (To)
     */
    long REGID_WBCI_ESCALATION_TO = 5051L;
    /**
     * Registry-ID fuer die FFM Materialrueckmeldungs-Empfaengeradresse (To)
     */
    long REGID_FFM_MATERIAL_FEEDBACK_TO = 5052L;
    /**
     * Registry-ID fuer das FFM-Kündigung-Zeitfenster (Von)
     */
    long REGID_FFM_TIMESLOT_KUENDIGUNG_FROM = 5060L;
    /**
     * Registry-ID fuer das FFM-Kündigung-Zeitfenster (Bis)
     */
    long REGID_FFM_TIMESLOT_KUENDIGUNG_TO = 5061L;
    /**
     * Registry-ID fuer das FFM-Default-Zeitfenster (Von)
     */
    long REGID_FFM_TIMESLOT_DEFAULT_FROM = 5062L;
    /**
     * Registry-ID fuer das FFM-Default-Zeitfenster (Bis)
     */
    long REGID_FFM_TIMESLOT_DEFAULT_TO = 5063L;
    /**
     * Registry-ID fuer das manuell übersteuerte FFM-Zeitfenster (Von)
     */
    long REGID_FFM_TIMESLOT_MANUAL_FROM = 5064L;
    /**
     * Registry-ID fuer das manuell übersteuerte FFM-Zeitfenster (Bis)
     */
    long REGID_FFM_TIMESLOT_MANUAL_TO = 5065L;
    /**
     * Registry-ID fuer das FFM-Zeitfenster (Von)
     */
    long REGID_FFM_TIMESLOT_INT_AND_IK_FROM = 5066L;
    /**
     * Registry-ID fuer das FFM-Zeitfenster (Bis)
     */
    long REGID_FFM_TIMESLOT_INT_AND_IK_TO = 5067L;

    /**
     * Gibt den String-Wert zurueck, der fuer die Registry-ID <code>id</code> hinterlegt wurde.
     *
     * @param id Registry-ID
     * @return String oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt oder kein Registry-Eintrag mit der angegebenen ID
     *                       vorhanden ist..
     */
    String getStringValue(Long id) throws FindException;

    /**
     * Gibt den Integer-Wert zurueck, der fuer die Registry-ID <code>id</code> hinterlegt wurde.
     *
     * @param id Registry-ID
     * @return Integer-Objekt oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt oder kein Registry-Eintrag mit der angegebenen ID
     *                       vorhanden ist.
     */
    Integer getIntValue(Long id) throws FindException;

    /**
     * Laedt alle Registry-Objekte
     *
     * @return Liste mit Registry-Objekten
     * @throws FindException falls die Suche einen Fehler liefert.
     */
    List<Registry> findRegistries() throws FindException;

    /**
     * Speichert ein Registry-Objekt
     *
     * @param toSave zu speicherndes Registry-Objekt
     * @throws StoreException falls Fehler beim Speichern auftritt
     */
    void saveRegistry(Registry toSave) throws StoreException;

}
