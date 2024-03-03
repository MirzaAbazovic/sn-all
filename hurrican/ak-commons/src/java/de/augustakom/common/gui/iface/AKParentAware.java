/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.02.2005 08:41:17
 */
package de.augustakom.common.gui.iface;


/**
 * Interface fuer GUI-Komponenten, die ihre Parent-Klasse genau kennen.
 *
 *
 */
public interface AKParentAware {

    /**
     * Uebergibt der Komponente die Parent-Klasse.
     *
     * @param parentClazz
     */
    public void setParentClass(Class<?> parentClazz);

    /**
     * Uebergibt der Komponente den Klassennamen der Parent-Klasse.
     *
     * @param className
     */
    public void setParentClassName(String className);

    /**
     * Gibt den Klassennamen der Parent-Klasse zurueck.
     *
     * @return
     */
    public String getParentClassName();

}


