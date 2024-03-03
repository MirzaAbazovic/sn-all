/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.02.2008 12:43:07
 */
package de.augustakom.authentication.utils;

import org.apache.commons.lang.StringUtils;

import de.augustakom.authentication.model.temp.AKCompBehaviorSummary;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKParentAware;


/**
 * Utils-Klasse fuer die Auswertung von Benutzerberechtigungen auf GUI-Komponenten.
 *
 *
 */
public class AKCompBehaviorTools {

    /**
     * Ermittelt den Parent-Namen einer Komponente
     *
     * @param comp          Komponente, deren Parent ermittelt werden soll
     * @param defaultParent Name des Default-Parents.
     * @return Name des Parents
     */
    public static String getParentName(AKManageableComponent comp, String defaultParent) {
        if (StringUtils.isBlank(defaultParent) && (comp instanceof AKParentAware)) {
            return ((AKParentAware) comp).getParentClassName();
        }
        return defaultParent;
    }

    /**
     * Erstellt aus den uebergebenen GUI-Komponenten und dem Parent ein Array von Objekten des Typs
     * <code>AKCompBehaviorSummary</code>.
     *
     * @param parent (optional) Angabe des zu verwendenden Parents
     * @param comps  GUI-Komponenten, aus denen AKCompBehaviorSummary Objekte erzeugt werden sollen.
     * @return Array mit den erzeugten AKCompBehaviorSummary Objekten.
     *
     */
    public static AKCompBehaviorSummary[] createCompBehaviorSummary(String parent, AKManageableComponent[] comps) {
        if (comps != null) {
            AKCompBehaviorSummary[] cbs = new AKCompBehaviorSummary[comps.length];
            for (int i = 0; i < comps.length; i++) {
                AKManageableComponent mc = comps[i];

                AKCompBehaviorSummary summary = new AKCompBehaviorSummary();
                summary.setComponentName(mc.getComponentName());
                summary.setParentClass(getParentName(mc, parent));
                cbs[i] = summary;
            }
            return cbs;
        }
        return new AKCompBehaviorSummary[] { };
    }

    /**
     * Ermittelt zu jeder GUI-Komponente aus <code>components</code> den entsprechenden Eintrag aus
     * <code>behaviors</code> und wendet die ermittelten Rechte auf die GUI-Komponente an.
     *
     * @param components  Array mit den zu ueberpruefenden GUI-Komponenten
     * @param parentClass (optional) Angabe der Parent-Klasse der GUI-Komponente
     * @param behaviors   Array mit den Benutzerberechtigungen
     *
     */
    public static void assignUserRights(AKManageableComponent[] components, String parentClass,
            AKCompBehaviorSummary[] behaviors) {
        if (components != null) {
            for (AKManageableComponent compToManage : components) {
                AKCompBehaviorSummary summary =
                        getCompBehavior(behaviors, compToManage.getComponentName(), parentClass);

                if (summary != null) {
                    setCompBehavior(compToManage,
                            summary.isComponentExecutable(), summary.isComponentVisible());
                }
                else {
                    setCompBehavior(compToManage, false, true);
                }
            }
        }
    }

    /*
     * Definiert das Verhalten einer best. Komponente.
     * @param comp
     * @param executable
     * @param visible
     */
    private static void setCompBehavior(AKManageableComponent comp, boolean executable, boolean visible) {
        comp.setManagementCalled(true);
        comp.setComponentExecutable(executable);
        comp.setComponentVisible(visible);
    }

    /*
     * Gibt zu das Berechtigungsobjekt zu der Komponente mit dem Namen
     * 'compName' und dem Parant 'parent' zurueck.
     * @param behaviors
     * @param compName
     * @param parent (optional)
     * @return
     */
    private static AKCompBehaviorSummary getCompBehavior(AKCompBehaviorSummary[] behaviors,
            String compName, String parent) {
        for (AKCompBehaviorSummary sum : behaviors) {
            if (StringUtils.equals(compName, sum.getComponentName())) {
                if ((parent != null) && StringUtils.equals(parent, sum.getParentClass())) {
                    return sum;
                }
                return sum;
            }
        }
        return null;
    }

}


