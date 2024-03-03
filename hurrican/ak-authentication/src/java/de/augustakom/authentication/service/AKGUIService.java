/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.06.2004 07:46:33
 */
package de.augustakom.authentication.service;


import de.augustakom.authentication.model.AKCompBehavior;
import de.augustakom.authentication.model.AKGUIComponent;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.model.temp.AKCompBehaviorSummary;
import de.augustakom.authentication.service.exceptions.AKGUIServiceException;

/**
 * Interface fuer einen GUI-Service. <br> Ueber den AKGUIService kann ueberprueft werden, ob ein Oberflaechenelement
 * (z.B. ein Button) fuer einen Benutzer sichtbar und/oder verwendbar sein soll.
 *
 *
 */
public interface AKGUIService extends IAuthenticationService {

    /**
     * Sucht nach einer bestimmten GUI-Komponente ueber deren Namen, den Parent und der Applikation.
     *
     * @param name          Name der GUI-Komponente
     * @param parentClass   Name der Parent-Klasse
     * @param applicationId ID der Applikation
     * @return AKGUIComponent oder <code>null</code>
     * @throws AKGUIServiceException wenn bei der Abfrage ein Fehler auftritt.
     */
    public AKGUIComponent findGUIComponent(String name, String parentClass, Long applicationId)
            throws AKGUIServiceException;

    /**
     * Sucht nach der Verhaltensdefinition einer GUI-Komponente fuer eine bestimmte Rolle.
     *
     * @param role
     * @param guiComponent
     * @return AKCompBehavior oder <code>null</code>
     * @throws AKGUIServiceException wenn bei der Abfrage ein Fehler auftritt.
     */
    public AKCompBehavior findBehavior4Role(AKRole role, AKGUIComponent guiComponent)
            throws AKGUIServiceException;

    /**
     * Speichert die Eigenschaften einer GUI-Komponente (entweder durch Neuanlage oder Update).
     *
     * @param guiComp zu speicherndes Objekt
     * @throws AKGUIServiceException wenn beim Speichern ein Fehler auftritt.
     */
    public void saveGUIComponent(AKGUIComponent guiComp) throws AKGUIServiceException;

    /**
     * Loescht den Eintrag fuer eine GUI-Komponente aus der Verwaltungs-DB. Auch alle evtl. eingestellten
     * Verhaltensweisen fuer die Komponente werden dadurch geloescht.
     *
     * @param guiComp zu loeschende GUI-Komponente
     * @throws AKGUIServiceException wenn beim Loeschen ein Fehler auftritt.
     */
    public void deleteGUIComponent(AKGUIComponent guiComp) throws AKGUIServiceException;

    /**
     * Speichert das Verhalten einer GUI-Komponente (entweder durch Neuanlage oder Update). <br> <br> Besonderheit:
     * Besitzt das Objekt die Eigenschaften <code>visible=true</code> und <code>executable=false</true> wird der Eintrag
     * nicht gespeichert bzw. ein bestehender Eintrag geloescht. <br> Grund: Diese Kombination ist der Standard fuer
     * eine GUI-Komponente. Um in der DB nicht unnoetige Eintraege zu halten werden deshalb diese Eintraege geloescht.
     * Die Funktion ist trotzdem garantiert.
     *
     * @param behavior zu speicherndes Objekt
     * @throws AKGUIServiceException wenn beim Speichern ein Fehler auftritt.
     */
    public void saveComponentBehavior(AKCompBehavior behavior) throws AKGUIServiceException;

    /**
     * Wertet die Berechtigungen des angemeldeten Benutzers (identifiziert ueber die Session-ID) fuer die angegebenen
     * GUI-Komponenten aus und gibt deren Eigenschaften (visible und enabled) zurueck. <br> Die Komponente wird ueber
     * ihren Namen (<code>toEvaluate.getComponentName()</code>) und einen Klassennamen identifiziert. <br> Der
     * Klassenname ist meist der Name eines Panels oder Frames, in dem die Komponente platziert ist. Diese Daten sind in
     * den Objekten vom Typ AKCompBehaviorSummary definiert. Die Berechtigungen werden ebenfalls in diesem Objekt
     * gespeichert. <br><br> Zur Anwendung der Rollenrechte auf den GUI-Komponenten steht die Hilfsklasse
     * <code>AKCompBehaviorTools</code> zur Verfuegung.
     *
     * @param sessionId  Session-ID des Users
     * @param toEvaluate Array mit den zu pruefenden GUI-Komponenten.
     * @return das uebergebene Array mit den zu pruefenden GUI-Komponenten erweitert um die definitiven Berechtigungen
     * (never {@code null}).
     * @throws AKGUIServiceException
     *
     */
    public AKCompBehaviorSummary[] evaluateRights(Long sessionId, AKCompBehaviorSummary[] toEvaluate)
            throws AKGUIServiceException;

}


