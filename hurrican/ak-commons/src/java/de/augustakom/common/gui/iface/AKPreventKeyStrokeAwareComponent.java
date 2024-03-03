/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.10.2007 09:10:08
 */
package de.augustakom.common.gui.iface;


/**
 * Interface fuer Text-Komponenten, die einen bestimmten Tastendruck verhindern sollen.
 *
 *
 */
public interface AKPreventKeyStrokeAwareComponent {

    /**
     * Verhindert, dass ein bestimmter Tastendruck eine Auswirkung in der TextComponent hat. Das Event wird komplett
     * konsumiert. <br> Als Parameter wird das zu unterdrueckende Event angegeben. <br> Beispiele: <br> - 'pressed
     * ENTER' um die Enter-Taste zu verhindern <br> - 'typed d' um Eingaben von 'd' zu verhindern <br>
     *
     * @param keyStrokeToPrevent das zu unterdrueckende Tasten-Event.
     *
     */
    public void preventKeyStroke(String keyStrokeToPrevent);

}


