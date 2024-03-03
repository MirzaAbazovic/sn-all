/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.07.2004 10:29:49
 */
package de.augustakom.common.gui.iface;

import java.util.*;


/**
 * Listener-Interface fuer (GUI-)Komponenten, die ueber eine Aenderung der Menu-Struktur benachrichtigt werden sollen.
 * <br> Die Benachrichtigung erfolgt z.Z. nur aus Komponenten heraus, die das Interface <code>AKMenuOwner</code>
 * implementieren.
 */
public interface AKMenuOwnerListener extends EventListener {

    /**
     * Wird aufgerufen, wenn sich die Menu-Struktur des Menu-Owner aendert. <br>
     *
     * @param menuOwner Menu-Owner, dessen Menu-Struktur sich geaendert hat.
     */
    void menuChanged(AKMenuOwner menuOwner);

}


