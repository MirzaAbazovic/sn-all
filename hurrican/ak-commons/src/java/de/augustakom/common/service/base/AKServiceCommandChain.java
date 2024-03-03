/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2005 08:17:04
 */
package de.augustakom.common.service.base;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.aop.framework.Advised;

import de.augustakom.common.service.exceptions.DefaultServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.service.iface.IServiceCommandChainAware;
import de.augustakom.common.tools.aop.AOPInstanceTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.messages.IWarningAware;


/**
 * Implementierung einer Chain, um mehrere IServiceCommand-Objekte hintereinander auszufuehren.
 *
 *
 */
public class AKServiceCommandChain implements IWarningAware {

    private static final Logger LOGGER = Logger.getLogger(AKServiceCommandChain.class);

    private List<IServiceCommand> chain = null;
    private AKWarnings warnings = null;
    private IServiceCommand lastExecuted = null;
    private Map<Object, Object> commandContext = null;
    private Map<Object, Object> commandResults = null;

    private long chainId = -1;

    /**
     * Default-Konstruktor.
     */
    public AKServiceCommandChain() {
        super();
        init();
    }

    /* Initialisiert die Chain. */
    private void init() {
        chain = new ArrayList<>();
        chainId = System.currentTimeMillis();
        commandContext = new HashMap<>();
        commandResults = new HashMap<>();
    }

    /**
     * Fuegt der Chain ein Service-Command hinzu.
     *
     * @param cmd das ServiceCommand-Objekt, das der Chain hinzugefuegt werden soll.
     * @throws IllegalArgumentException
     */
    public void addCommand(IServiceCommand cmd) {
        if (cmd != null) {
            chain.add(cmd);
        }
        else {
            throw new IllegalArgumentException("Als Command darf kein NULL-Objekt uebergeben werden!");
        }
    }

    /**
     * Prueft, ob der Chain Commands zugeordnet sind.
     *
     * @return
     */
    public boolean hasCommands() {
        return ((chain != null) && (!chain.isEmpty()));
    }

    /**
     * Gibt das zuletzt ausgefuehrte ServiceCommand-Objekt zurueck.
     *
     * @return Objekt vom Typ <code>IServiceCommand</code>.
     *
     */
    public IServiceCommand getLastExecuted() {
        return lastExecuted;
    }

    /**
     * Speichert das Objekt <code>value</code> als Parameter in dem Command-Context ab. <br> Objekte, die ueber diese
     * Methode gesetzt werden, koennen von allen ServiceCommands der Chain referenziert werden.
     *
     * @param key   Key, unter dem das Objekt gespeichert werden soll.
     * @param value zu speicherndes Objekt
     *
     */
    public void setCommandContextParameter(Object key, Object value) {
        commandContext.put(key, value);
    }

    /**
     * Gibt den Command-Context Parameter zurueck, der unter dem Key <code>key</code> gespeichert wurde.
     *
     * @param key
     * @return
     *
     */
    public Object getCommandContextParameter(Object key) {
        return commandContext.get(key);
    }

    /**
     * Ueber diese Methode koennen die Command-Klassen ein Result in der Chain speichern. <br> Diese Results koennen von
     * Client der Chain wieder angefordert und weiter verwendet werden.
     *
     * @param key
     * @param value
     *
     */
    public void addCommandResult(Object key, Object value) {
        commandResults.put(key, value);
    }

    /**
     * Ermittelt ein Command-Result unter dem Key <code>key</code> und gibt den zugehoerigen Wert zurueck.
     *
     * @param key
     *
     */
    public Object getCommandResult(Object key) {
        return commandResults.get(key);
    }

    /**
     * @return Die Results der ausgefuehrten Commands in einer Liste.
     * @throws ServiceCommandException wenn waehrend der Chain-Ausfuehrung ein Fehler auftritt.
     *
     * @see executeChain(boolean) Im Gegensatz zu der executeChain-Methode mit boolean-Parameter wertet die Chain die
     * Command-Results nicht direkt aus.
     */
    public List<Object> executeChain() throws ServiceCommandException {
        return executeChain(false);
    }

    /**
     * Startet die Chain-Ausfuehrung.
     *
     * @param checkServiceCmdResult ist das Flag gesetzt, wird davon ausgegangen, dass die Commands ein Result vom Typ
     *                              <code>ServiceCommandResult</code> liefern. Ist dieses Result nicht vorhanden bzw.
     *                              der Status <> OK, wird eine Exception generiert.
     * @return Die Results der ausgefuehrten Commands in einer Liste.
     * @throws ServiceCommandException wenn waehrend der Chain-Ausfuehrung ein Fehler auftritt.
     */
    public List<Object> executeChain(boolean checkServiceCmdResult) throws ServiceCommandException {
        if (!hasCommands()) {
            throw new DefaultServiceCommandException("Chain besitzt keine auszufuehrenden Commands!");
        }

        try {
            List<Object> result = new ArrayList<>();
            warnings = new AKWarnings();

            for (IServiceCommand cmd : chain) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("CHAIN-Execution (ID: " + chainId + "): " + cmd.getServiceCommandName());
                }

                try {
                    if (AOPInstanceTools.isInstanceof(cmd, IServiceCommandChainAware.class)) {
                        Object implementation = AOPInstanceTools.getRealImplementation(cmd);
                        ((IServiceCommandChainAware) implementation).setServiceCommandChain(this);
                    }

                    // ServiceCommand ausfuehren
                    Object cmdResult = cmd.execute();
                    if (cmdResult != null) {
                        result.add(cmdResult);
                    }

                    // CommandResult auswerten
                    if (checkServiceCmdResult) {
                        if (cmdResult instanceof ServiceCommandResult) {
                            ServiceCommandResult res = (ServiceCommandResult) cmdResult;
                            if (!res.isOk()) {
                                throw new DefaultServiceCommandException("Command not successfully finished: " +
                                        cmd.getClass().getSimpleName() + "\n" +
                                        "Error: " + res.getMessage());
                            }
                        }
                        else {
                            throw new DefaultServiceCommandException(
                                    "Result of command is not of requested type ServiceCommandResult! " +
                                            "Command class: " + cmd.getClass()
                            );
                        }
                    }

                    // Evtl. generierte Warnungen des Commands speichern
                    if (cmd instanceof IWarningAware) {
                        warnings.addAKWarnings(((IWarningAware) cmd).getWarnings());
                    }
                    else if ((cmd instanceof Advised) &&
                            (((Advised) cmd).getTargetSource().getTarget() instanceof IWarningAware)) {
                        // benoetigt, falls ServiceCommand ueber ein Proxy angesprochen wird.
                        warnings.addAKWarnings(
                                ((IWarningAware) ((Advised) cmd).getTargetSource().getTarget()).getWarnings());
                    }
                }
                finally {
                    lastExecuted = cmd;
                }
            }

            return result;
        }
        catch (ServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            throw new DefaultServiceCommandException(e.getMessage(), e);
        }
    }

    @Override
    public AKWarnings getWarnings() {
        return warnings;
    }
}
