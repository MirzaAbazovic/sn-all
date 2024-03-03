/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2004 09:29:26
 */
package de.augustakom.common.service.iface;


/**
 * Interface fuer ServiceCommand-Klassen. <br> Eine ServiceCommand-Klasse ist dafuer gedacht, dass
 * Service-Implementierungen die Aufrufe an die Command-Klasse weiterleiten. Dadurch koennen die
 * Service-Implementierungen uebersichtlicher werden, da der Code pro Klasse nicht mehr so umfangreich ist. <br> Da die
 * Command-Klassen natuerlich auch einen gewissen Overhead verursachen, sollte immer genau abgewogen werden, ob die
 * Implementierung in ein Command ausgelagert wird oder ob sie nicht doch innerhalb der Service-Implementierung
 * enthalten sein sollte. <br><br> Die Konfiguration der ServiceCommand-Klassen sollte bevorzugt ueber die
 * Service-Konfigurationsdateien erfolgten - zwingend, wenn Transaktionen benoetigt werden! Alternativ dazu kann auch
 * die Methode <code>prepare(String, Object)</code> aufgerufen werden, um z.B. die DAO-Klassen zu uebergeben.
 *
 *
 */
public interface IServiceCommand {

    /**
     * Uebergibt dem ServiceCommand ein Objekt und speichert dieses unter dem Key <code>name</code> ab. <br> Das
     * uebergebene Objekt ist nur fuer das Command-Objekt sichtbar, dem es uebergeben wurde! <br> <br> Um Objekte
     * zwischen verschiedenen Commands auszutauschen, kann die Methode <code>setCommandContextParameter</code> der
     * Klasse AKServiceCommandChain verwendet werden.
     *
     * @param name  Name des Objekts (sollte in der Command-Klasse als Konstante deklariert sein).
     * @param value das Objekt fuer die Command-Klasse
     */
    public void prepare(String name, Object value);

    /**
     * Fuehrt das ServiceCommand aus.
     *
     * @return Result des Commands oder <code>null</code>.
     * @throws Exception wenn waehrend der Ausfuehrung ein Fehler auftritt. Die Exception sollte bevorzugt vom Typ
     *                   <code>ServiceCommandException</code> sein.
     */
    public Object execute() throws Exception;

    /**
     * Gibt den Namen des ServiceCommands zurueck.
     *
     * @return
     */
    public String getServiceCommandName();

}


