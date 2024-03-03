/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.04.2004
 */
package de.augustakom.common.service.iface;

import java.util.*;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;


/**
 * Interface fuer ServiceLocator-Implementierungen. <br> Ueber einen ServiceLocator kann ein Service ueber einen Namen
 * geladen werden. Welche Service-Implementierung angezogen wird, ist die Aufgabe des ServiceLocators. <br>
 *
 *
 */
@Deprecated
public interface IServiceLocator {

    /**
     * Sucht nach einem Service mit dem angegegenen Namen. <br> Der Parameter <code>serviceContext</code> wird z.Z. noch
     * nicht verwendet - kann fuer evtl. spaeter benoetigte Erweiterungen verwendet werden.
     *
     * @param serviceName    Name des gesuchten Services
     * @param serviceContext
     * @return Instanz des gesuchten Services
     * @throws ServiceNotFoundException Wird in folgenden Faellen geworfen: <ul> <li>Service wurde nicht gefunden
     *                                  <li>Service konnte nicht erzeugt werden (z.B. kein Default-Konstruktor vorhanden
     *                                  <li>Service ist nicht vom Typ IServiceObject </ul>
     */
    public IServiceObject getService(String serviceName, IServiceContext serviceContext)
            throws ServiceNotFoundException;

    /**
     * @param serviceName
     * @param requiredType   Angabe des erwarteten Service-Typs
     * @param serviceContext
     * @return
     * @throws ServiceNotFoundException Wird in den Faellen von IServiceLocator.getService(String, IServiceContext)
     *                                  geworfen. Zusaetzlich koennen folgende Faelle zu dieser Exception fuehren: <ul>
     *                                  <li>Service ist nicht vom Typ <code>requiredType</code> </ul> <br>
     * @see IServiceLocator#getService(String, IServiceContext)
     */
    public <T extends IServiceObject> T getService(String serviceName, Class<T> requiredType, IServiceContext serviceContext)
            throws ServiceNotFoundException;

    /**
     * Sucht nach einem Objekt mit der ID <code>name</code>. <br> In dieser Methode findet keine Ueberpruefung statt, ob
     * das Objekt existiert oder von einem bestimmten Typ ist. <br> Wird kein Objekt mit dieser ID gefunden, wird
     * <code>null</code> zurueck gegeben. <br><br> Die Methode getService(..) sollte bevorzugt werden! <br>
     *
     * @param name Name/ID der gesuchten Bean
     * @return Gefundenes Objekt oder <code>null</code>
     */
    public Object getBean(String name);

    /**
     * Ermittelt alle Beans von einem best. Typ.
     *
     * @param type Typ der gesuchten Beans
     * @return a Map with the matching beans, containing the bean names as keys and the corresponding bean instances as
     * values
     *
     */
    public <T> Map<String, T> getBeansOfType(Class<T> type);

    /**
     * Gibt einen <strong>eindeutigen</strong> Namen fuer den ServiceLocator zurueck. <br>
     *
     * @return Name des ServiceLocators
     */
    public String getServiceLocatorName();

    /**
     * Beendet den ServiceLocator und gibt dadurch alle Resourcen frei.
     */
    public void closeServiceLocator();
}
