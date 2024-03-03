/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.10.2007 10:00:21
 */
package de.augustakom.hurrican.service.cc.impl.command;

import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.dao.cc.EndstelleDAO;
import de.augustakom.hurrican.dao.cc.RangierungDAO;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;


/**
 * Abstracte Klasse fuer Commands, die freie Rangierungen ermitteln und dieser einer Endstelle zuordnen sollen.
 *
 *
 */
public abstract class AbstractAssignRangierungCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(AbstractAssignRangierungCommand.class);

    /**
     * Key fuer die prepare-Methode, um dem Command die Auftrags-ID zu uebergeben.
     */
    public static final String ENDSTELLE_ID = "endstelle.id";
    /**
     * Key fuer die prepare-Methode, um dem Command ein ServiceCallback-Objekt zu uebergeben.
     */
    public static final String SERVICE_CALLBACK = "service.callback";

    // DAOs
    private RangierungDAO rangierungDAO = null;
    private EndstelleDAO endstelleDAO = null;

    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    protected HVTService hvtService;

    /**
     * Ermittelt die Endstelle mit der ID <code>endstelleId</code>. Zusaetzlich werden hier noch folgende Daten
     * geprueft: <br> - Anschlussart der Endstelle ist HVT <br> - Endstelle ist einem HVT zugeordnet <br> - Endstelle
     * besitzt noch keine Rangierung <br> - zugeordneter HVT ist freigegeben <br>
     *
     * @param endstelleId ID der zu ladenden Endstelle
     * @return die geladene Endstelle
     * @throws FindException wenn die Endstelle nicht geladen werden konnte oder einer der oben genannten Checks fehlt
     *                       schlaegt.
     *
     */
    protected Endstelle loadEndstelle(Long endstelleId) throws FindException {
        try {
            EndstellenService esSrv = getCCService(EndstellenService.class);
            Endstelle endstelle = esSrv.findEndstelle(endstelleId);
            if (endstelle == null) {
                throw new FindException("Endstelle konnte nicht geladen werden! ID: " + endstelleId);
            }

            if ((endstelle.getAnschlussart() == null) ||
                    !(NumberTools.isIn(endstelle.getAnschlussart(),
                            new Long[] { Anschlussart.ANSCHLUSSART_HVT, Anschlussart.ANSCHLUSSART_KVZ, Anschlussart.ANSCHLUSSART_FTTB,
                                    Anschlussart.ANSCHLUSSART_FTTH, Anschlussart.ANSCHLUSSART_FTTX }
                    ))) {
                throw new FindException("Die Anschlussart der Endstelle ist ungültig. " +
                        "Es kann keine Physik vergeben werden. Bitte wählen Sie den Anschluss über Switch 1 od. 2 aus " +
                        "oder ändern Sie die Anschlussart.");
            }

            if (checkKvzSperre()) {
                hvtService.validateKvzSperre(endstelle);
            }

            // Pruefen, ob bereits eine Rangierung zugeordnet ist (wenn ja --> Exception)
            checkExistingRangierung(endstelle);

            return endstelle;
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    protected boolean checkKvzSperre() {
        return true;
    }

    /*
     * Ueberprueft, ob der Endstelle bereits eine Rangierung zugeordnet ist. Ist dies der
     * Fall, wird eine FindException geworfen.
     */
    protected void checkExistingRangierung(Endstelle endstelle) throws FindException {
        if (endstelle.hasRangierung()) {
            throw new FindException("Der Endstelle ist bereits eine Physik zugeordnet!");
        }
    }

    /**
     * @return Returns the rangierungDAO.
     */
    public RangierungDAO getRangierungDAO() {
        return rangierungDAO;
    }

    /**
     * @param rangierungDAO The rangierungDAO to set.
     */
    public void setRangierungDAO(RangierungDAO rangierungDAO) {
        this.rangierungDAO = rangierungDAO;
    }

    /**
     * @return Returns the endstelleDAO.
     */
    public EndstelleDAO getEndstelleDAO() {
        return endstelleDAO;
    }

    /**
     * @param endstelleDAO The endstelleDAO to set.
     */
    public void setEndstelleDAO(EndstelleDAO endstelleDAO) {
        this.endstelleDAO = endstelleDAO;
    }

    /**
     * Injected
     */
    public void setHvtService(HVTService hvtService) {
        this.hvtService = hvtService;
    }

}


