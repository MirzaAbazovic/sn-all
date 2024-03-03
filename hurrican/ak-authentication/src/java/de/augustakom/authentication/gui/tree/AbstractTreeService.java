/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 14:46:46
 */
package de.augustakom.authentication.gui.tree;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.exceptions.TreeException;
import de.augustakom.common.gui.swing.AKJDefaultMutableTreeNode;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.service.iface.IServiceObject;

/**
 * Service-Implementierung, um den Admin-Tree aufbauen und verwalten zu koennen.
 *
 * @param <E> Typ der Kind-Elemente
 * @param <T> Typ der Daten eines Knoten
 */
public abstract class AbstractTreeService<E, T> implements IServiceObject, Serializable {

    private static final Logger LOGGER = Logger.getLogger(AbstractTreeService.class);
    private static final long serialVersionUID = -974674055750513942L;

    private String frameClass = null;
    private String modelClass = null;
    private List<?> contextList = null;

    /**
     * Liest alle Children des Objekts <code>parent</code> aus. Dabei wird der Parameter <code>filter</code>
     * beruecksichtigt.
     *
     * @param parent Objekt, dessen Children geladen werden sollen
     * @param filter Filter-Objekt fuer das Laden der Children
     * @return Liste von Objekten, die als Children eines anderen Objekts dienen.
     */
    protected abstract List<E> getChildren(Object parent, Object filter) throws TreeException;

    /**
     * Fuellt die Eigenschaften des TreeNodes <code>nodeToFill</code> mit den Daten aus <code>data</code>. <br> Wichtig:
     * Das Objekt <code>data</code> muss immer von dem Typ sein, fuer den der TreeService verantwortlich ist. Ist der
     * TreeService z.B. dafuer zustaendig, alle Daten von AKDepartment zu laden, so muss als <code>data</code> ein
     * Objekt vom AKDepartment uebergeben werden.
     * <p/>
     * <strong>Wichtig: In dieser Methode wird dem TreeNode kein UserObject uebergeben!</strong>
     *
     * @param nodeToFill TreeNode, der die Daten erhalten soll
     * @param data       Objekt, das die Daten fuer den TreeNode enthaelt
     * @throws TreeException wenn ein Fehler auftritt.
     */
    protected abstract void fillNode(AdminTreeNode nodeToFill, T data) throws TreeException;

    /**
     * Gibt ein Internal-Frame zurueck, ueber das das Datenobjekt angezeigt/geaendert werden kann.
     *
     * @param model Objekt, dessen Frame gesucht wird
     * @param node  TreeNode, der das Objekt besitzt.
     * @return Frame, ueber das das Objekt angezeigt/geaendert werden kann.
     * @throws TreeException wenn bei der Suche nach dem Frame ein Fehler auftritt.
     */
    protected AKJInternalFrame getFrame4Object(T model, AKJDefaultMutableTreeNode node) throws TreeException {
        if ((frameClass != null) && (modelClass != null)) {
            try {
                AKJInternalFrame frameClazz = getAkjInternalFrame(model, node);
                if (frameClazz != null)
                    return frameClazz;
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        else {
            String objName = (model != null) ? model.getClass().getName() : "null";
            throw new TreeException(TreeException.MSG_FRAME_NOT_FOUND, new Object[] { objName });
        }
        return null;
    }

    private AKJInternalFrame getAkjInternalFrame(T model, AKJDefaultMutableTreeNode node) throws ClassNotFoundException {
        Class<?> frameClazz = Class.forName(frameClass);
        try {
            return createInternalFrame(frameClazz, new Object[] { model, node });
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        try {
            return createInternalFrame(frameClazz, new Object[] {});
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

    /**
     * @return the {@code classToConstruct} by supplied args or if this is failed by default constructor
     * @throws Exception falls beim Instantiieren ein Fehler auftritt
     */
    private AKJInternalFrame createInternalFrame(Class<?> classToConstruct, Object[] constructorArgs)
            throws Exception {
        Constructor<?>[] constructors = classToConstruct.getConstructors();
        Constructor<?> c = null;
        constructor:
        for (Constructor<?> constructor : constructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length != constructorArgs.length) {
                continue;
            }
            for (int i = 0; i < parameterTypes.length; i++) {
                if (!parameterTypes[i].isAssignableFrom(constructorArgs[i].getClass())) {
                    break constructor;
                }
            }
            c = constructor;
        }
        if (c != null) {
            Object obj = c.newInstance(constructorArgs);
            return (obj instanceof AKJInternalFrame) ? (AKJInternalFrame) obj : null;
        }
        return null;
    }

    /**
     * Gibt eine Liste von Action-Objekten zurueck, die auf das Objekt <code>object</code> 'angewendet' werden koennen.
     * <br> Das Objekt wird der Action mit dem Key <code>SystemConstants.ACTION_PROPERTY_USEROBJECT</code> uebergeben.
     * Der TreeNode mit dem Key <code>SystemConstants.ACTION_PROPERTY_TREENODE</code>. <br><br> In der Liste koennen
     * sich auch Objekte vom Typ <code>javax.swing.JSeparator</code> befinden. Dadurch koennen Oberflaechen erkennen, an
     * welcher Stelle ein Separator eingefuegt werden soll.
     *
     * @param object   Objekt, dessen Kontexte/Actions gelesen werden sollen
     * @param treeNode TreeNode, der das Objekt besitzt.
     * @return Liste von Action-Objekten (never {@code null}).
     * @throws TreeException wenn beim Auslesen der Actions ein Fehler auftritt.
     */
    protected List<?> getContextList4Object(Object object, AKJDefaultMutableTreeNode treeNode) throws TreeException {
        if (contextList != null) {
            List<Object> result = new ArrayList<>();
            for (Object context : contextList) {
                if (context instanceof Action) {
                    Action action = (Action) context;
                    action.putValue("user.object", object);
                    action.putValue("tree.node", treeNode);
                    result.add(context);
                }
                else if ((context instanceof String) && StringUtils.equals((String) context, JSeparator.class.getName())) {
                    result.add(new JSeparator());
                }
            }

            return result;
        }
        return Collections.emptyList();
    }

    /**
     * Setzt den Klassennamen des Model, das fuer das Erzeugen des Frames benoetigt wird.
     */
    public void setModelClass(String modelClass) {
        this.modelClass = modelClass;
    }

    /**
     * Setzt den Klassennamen des Frames, das fuer das Objekt verwendet werden soll.
     *
     * @param frameClass The frameClass to set.
     */
    public void setFrameClass(String frameClass) {
        this.frameClass = frameClass;
    }

    /**
     * Setzt eine Liste mit Objekten, die als Kontexte fuer das Objekt dienen. <br> Die Objekte muessen vom Typ
     * <code>javax.swing.Action</code> sein. Um einen Separator anzuzeigen, kann zusaetzlich
     * <code>javax.swing.JSeparator</code> verwendet werden.
     *
     * @param contextList Liste mit Klassennamen
     */
    public void setContextList(List<?> contextList) {
        this.contextList = contextList;
    }
}
