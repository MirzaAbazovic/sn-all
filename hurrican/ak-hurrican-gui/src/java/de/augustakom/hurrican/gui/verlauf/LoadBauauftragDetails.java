/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.03.2010 10:31:49
 */
package de.augustakom.hurrican.gui.verlauf;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.view.AbstractBauauftragView;
import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 * Interface für Bauauftrag Panels, um ihre Details zu laden.
 *
 * @param <V> Typ des Bauauftrag-View, der die Daten des aktuellen Bauauftrags enthält
 * @param <T> Typ des Value-Object mit den geladenen Details zum Bauauftrag
 */
public interface LoadBauauftragDetails<V extends AbstractBauauftragView, T> {

    /**
     * Veranlasst ein Bauauftrag Panel dazu, die Detaildaten zu laden.<br/> Die Ergebnis-Daten werden als value-Object
     * zurückgeliefert. <p><b>Achtung:</b> Diese Methode darf keine GUI-Methoden aufrufen, da sie normalerweise in einem
     * Workerthread ausgeführt wird, d.h. <b>nicht</b> im EDT Thread.</p>
     *
     * @param selectedView mit den aktuellen Daten des Bauauftrags
     * @throws ServiceNotFoundException falls ein Fehler beim Laden des Service zum Laden der Daten auftritt
     * @throws ServiceNotFoundException falls ein Fehler Laden der Daten auftritt
     */
    public T loadDetails(V selectedView) throws ServiceNotFoundException, FindException;

    /**
     * Veranlasst das Bauauftrag Panel dazu, die Daten des ausgewählten Views der Tabelle und die {@link
     * #loadDetails(V)} geladenen Daten zu setzen, so dass die Detail-Daten angezeigt werden.<br/>
     *
     * @param selectedView der aktuelle View, zu dem die gefundenen Daten gehören
     * @param result       mit den geladenen Daten.
     * @throws HurricanGUIException wird geworfen, wenn bei der Suche ein Fehler auftritt.
     */
    public void updateGuiByDetails(V selectedView, T result);

    /**
     * Setzt die Details eines Bauauftrags auf die Standardwerte (meist leer) zurück.
     */
    public void clearDetails();
}


