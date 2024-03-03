/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.06.2004 08:50:26
 */
package de.augustakom.authentication.service.impl;

import java.util.*;
import java.util.function.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.dao.AKCompBehaviorDAO;
import de.augustakom.authentication.dao.AKGUIComponentDAO;
import de.augustakom.authentication.dao.AKRoleDAO;
import de.augustakom.authentication.model.AKCompBehavior;
import de.augustakom.authentication.model.AKGUIComponent;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.model.temp.AKCompBehaviorSummary;
import de.augustakom.authentication.model.view.AKCompBehaviorView;
import de.augustakom.authentication.service.AKGUIService;
import de.augustakom.authentication.service.AuthenticationTx;
import de.augustakom.authentication.service.exceptions.AKGUIServiceException;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.tools.collections.CollectionTools;

/**
 * Implementierung von AKGUIService. <br>
 *
 *
 */
@AuthenticationTx
public class AKGUIServiceImpl implements AKGUIService {

    private static final Logger LOGGER = Logger.getLogger(AKGUIServiceImpl.class);

    @Resource(name = "de.augustakom.authentication.dao.AKGUIComponentDAO")
    private AKGUIComponentDAO guiComponentDao;
    @Resource(name = "de.augustakom.authentication.dao.AKRoleDAO")
    private AKRoleDAO roleDao;
    @Resource(name = "de.augustakom.authentication.dao.AKCompBehaviorDAO")
    private AKCompBehaviorDAO compBehaviorDao;

    @Override
    public AKGUIComponent findGUIComponent(String name, String parentClass, Long applicationId) throws AKGUIServiceException {
        try {
            return guiComponentDao.find(name, parentClass, applicationId);
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public AKCompBehavior findBehavior4Role(AKRole role, AKGUIComponent guiComponent) throws AKGUIServiceException {
        try {
            if (guiComponent != null) {
                return compBehaviorDao.find(guiComponent.getId(), role.getId());
            }
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void saveGUIComponent(AKGUIComponent guiComp) throws AKGUIServiceException {
        try {
            guiComponentDao.save(guiComp);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKGUIServiceException(AKGUIServiceException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteGUIComponent(AKGUIComponent guiComp) throws AKGUIServiceException {
        try {
            guiComponentDao.delete(guiComp);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKGUIServiceException(AKGUIServiceException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveComponentBehavior(AKCompBehavior behavior) throws AKGUIServiceException {
        if (behavior != null) {
            if ((behavior.getId() != null) && behavior.isVisible() && !behavior.isExecutable()) {
                try {
                    // Standardverhalten --> Loeschen, falls vorhanden
                    compBehaviorDao.delete(behavior);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    // Exception nicht weiterleiten
                }
            }
            else {
                try {
                    // Verhalten in DB eintragen
                    compBehaviorDao.save(behavior);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    throw new AKGUIServiceException(AKGUIServiceException._UNEXPECTED_ERROR, e);
                }
            }
        }
    }

    @Override
    public AKCompBehaviorSummary[] evaluateRights(Long sessionId, AKCompBehaviorSummary[] toEvaluate)
            throws AKGUIServiceException {
        if ((toEvaluate == null) || (toEvaluate.length <= 0)) {
            return new AKCompBehaviorSummary[] { };
        }

        try {
            // 1. Suche nach Rollen ueber Session-ID
            List<AKRole> roles = getRoles(sessionId);
            Long appId = ((!roles.isEmpty()) ? (roles.get(0)).getApplicationId() : null);

            boolean userIsAdmin = false;
            Long[] roleIds = new Long[roles.size()];
            for (int i = 0; i < roleIds.length; i++) {
                AKRole role = roles.get(i);
                roleIds[i] = role.getId();

                if (role.isAdmin()) {
                    userIsAdmin = true;
                    break;
                }
            }

            if (userIsAdmin) {
                setCompBehaviors(toEvaluate, true, true);  // Admins 'duerfen alles'...
                return toEvaluate;
            }

            // 2. Suche nach GUI-Komponente
            List<String> compNames = new ArrayList<>();
            List<String> parentNames = new ArrayList<>();
            for (int i = 0; i < toEvaluate.length; i++) {
                AKCompBehaviorSummary compToEval = toEvaluate[i];
                if (compToEval != null) {
                    compNames.add(compToEval.getComponentName());

                    String parent = compToEval.getParentClass();
                    if (!parentNames.contains(parent)) {
                        parentNames.add(compToEval.getParentClass());
                    }
                }
            }

            if (compNames.isEmpty()) {
                return new AKCompBehaviorSummary[] { };
            }

            // 3. Rollenrechte der Komponenten suchen
            List<AKCompBehaviorView> views =
                    compBehaviorDao.findBehaviorViews(compNames, parentNames, appId, roleIds);

            SelectBehaviorViewPredicate predicate = new SelectBehaviorViewPredicate();
            for (int i = 0; i < toEvaluate.length; i++) {
                AKCompBehaviorSummary compToEval = toEvaluate[i];
                predicate.init(compToEval.getComponentName(), compToEval.getParentClass());

                boolean managed = false;
                boolean visible = false;
                boolean executable = false;

                // Behavior-Views zur aktuellen GUI-Komponente suchen
                Collection<AKCompBehaviorView> behaviors = CollectionTools.select(views, predicate);
                if ((behaviors != null) && (!behaviors.isEmpty())) {
                    managed = true;
                    Iterator<AKCompBehaviorView> behavIt = behaviors.iterator();
                    while (behavIt.hasNext()) {
                        AKCompBehaviorView cbView = behavIt.next();
                        visible = (visible) ? visible : cbView.isVisible();
                        executable = (executable) ? executable : cbView.isExecutable();

                        if (visible && executable) {
                            break;  // mehr Rechte gibt es z.Z. nicht!
                        }
                    }
                }

                // 4. Rollenrechte anwenden
                if (managed) {
                    setCompBehavior(compToEval, executable, visible);
                }
                else {
                    setCompBehavior(compToEval, false, true);
                }
            }

            return toEvaluate;
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            setCompBehaviors(toEvaluate, false, true);
            throw new AKGUIServiceException(AKGUIServiceException.ERROR_EVALUATE_COMPONENT_RIGHTS, ex);
        }
    }

    /*
     * Definiert das Verhalten der Komponenten <code>components</code>
     * @param components
     * @param executable
     * @param visible
     */
    private void setCompBehaviors(AKManageableComponent[] components, boolean executable, boolean visible) {
        for (int i = 0; i < components.length; i++) {
            AKManageableComponent mc = components[i];
            mc.setManagementCalled(true);
            mc.setComponentExecutable(executable);
            mc.setComponentVisible(visible);
        }
    }

    /*
     * Definiert das Verhalten einer best. Komponente.
     * @param comp
     * @param executable
     * @param visible
     */
    private void setCompBehavior(AKManageableComponent comp, boolean executable, boolean visible) {
        comp.setManagementCalled(true);
        comp.setComponentExecutable(executable);
        comp.setComponentVisible(visible);
    }

    /* Sucht nach Rollen ueber die Session-ID. */
    private List<AKRole> getRoles(final Long sessionId) throws AKGUIServiceException {
        try {
            return roleDao.findBySession(sessionId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    static class SelectBehaviorViewPredicate implements Predicate<AKCompBehaviorView> {
        private String componentName = null;
        private String parentClassName = null;

        /**
         * @param componentName   the name of the component which should be checked in this {@link Predicate}
         * @param parentClassName the name of the parent class which should be checked in this {@link Predicate}
         */
        public void init(String componentName, String parentClassName) {
            this.componentName = componentName;
            this.parentClassName = parentClassName;
        }

        @Override
        public boolean test(AKCompBehaviorView view) {
            return StringUtils.equals(view.getCompName(), componentName) && StringUtils.equals(view.getCompParent(), parentClassName);
        }
    }
}
