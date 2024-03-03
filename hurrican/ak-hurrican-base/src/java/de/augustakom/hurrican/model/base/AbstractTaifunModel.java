/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2011 12:32:47
 */
package de.augustakom.hurrican.model.base;

import java.io.*;
import javax.persistence.*;

import de.augustakom.common.model.AbstractObservable;


/**
 * Abstrakte Klasse fuer alle Modell-Klassen für Taifun-Entitäten.
 *
 *
 */
@MappedSuperclass
public abstract class AbstractTaifunModel extends AbstractObservable implements Serializable {

}


