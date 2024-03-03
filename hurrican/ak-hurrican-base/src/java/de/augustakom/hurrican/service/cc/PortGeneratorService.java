/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.12.2009 09:50:27
 */
package de.augustakom.hurrican.service.cc;

import java.io.*;
import java.util.*;

import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorDetails;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorImport;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.impl.ports.AbstractPortConfigurationCheck;

/**
 * Service für die Generierung von neuen Equipments (Ports), z.B. aus einer Import-Datei
 */
public interface PortGeneratorService extends ICCService {

    /**
     * Die zu importierenden Ports werden aus dem angegebenen Excel-File ausgelesen. Das Excel-File muss wie folgt
     * aufgebaut sein:<ol> <li> 1. Zeile: Ueberschrift; <li> 2. bis x-te Zeile: Datenwerte. </ol>
     * Spaltenreihenfolge:<ul> <li>HW_EQN (muss gesetzt sein, sonst wird Zeile ignoriert) <li>V5_PORT
     * <li>RANG_VERTEILER_IN <li>RANG_BUCHT_IN <li>RANG_LEISTE1_IN <li>RANG_STIFT1_IN <li>RANG_LEISTE2_IN
     * <li>RANG_STIFT2_IN <li>RANG_VERTEILER_OUT <li>RANG_BUCHT_OUT <li>RANG_LEISTE1_OUT <li>RANG_STIFT1_OUT
     * <li>RANG_LEISTE2_OUT <li>RANG_STIFT2_OUT </ul> Anschließend werden die zu generierenden Ports durch alle
     * indizierten {@link AbstractPortConfigurationCheck}s auf Vollständigkeit, Eindeutigkeit, Validität etc. überprüft.
     * Falls ein Fehler festgestellt wird, wird eine {@link FindException} mit dem Fehlertext geworfen.
     *
     * @param excelFileToImport    {@link InputStream} mit der zu importierenden Excel-File
     * @param portGeneratorDetails Container für Id der {@link HWBaugruppe} und Flag ob Kombi-Physik anzulegen
     * @return {@link List}e von {@link PortGeneratorImport}-Objekten die die Daten aus der Excel-Datei enthalten mit
     * einem zusätzlichen Flag ob Port bereits vorhanden ({@link PortGeneratorImport#getPortAlreadyExists()})
     * @throws IOException          falls der übergebenen {@link InputStream} nicht gelesen werden kann
     * @throws NullPointerException falls der übergebene {@link InputStream} {@code null} ist
     * @throws FindException        falls ein ausgeführter Check fehlschlägt (mit Meldung des fehlgeschlagenen Checks
     */
    public List<PortGeneratorImport> retrievePorts(InputStream excelFileToImport,
            PortGeneratorDetails portGeneratorDetails) throws IOException, FindException;

    /**
     * Erzeugt fuer die in der Liste <code>portsToGenerate</code> Equipment-Modelle. <br> Wichtig: die Methode speichert
     * die Equipment-Modelle nocht nicht ab! Dies muss vom Client selbst vorgenommen werden!
     *
     * @param portGeneratorDetails Detail-Angaben fuer die Port-Generierung
     * @param portsToGenerate      Liste mit den zu importierenden Ports.
     * @param sessionId            ID der aktuellen User-Session
     * @return Liste mit den generierten Equipment-Modellen.
     * @throws FindException wenn bei der Ermittlung von notwendigen Daten ein Fehler auftritt.
     */
    public List<Equipment> generatePorts(PortGeneratorDetails portGeneratorDetails,
            List<PortGeneratorImport> portsToGenerate, Long sessionId) throws FindException;
}
