/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2004 15:27:01
 */
package de.augustakom.authentication.service;


/**
 * Interface zur Definition aller verfuegbaren Service-Namen innerhalb des Authentication-Services.
 *
 *
 */
public interface AKAuthenticationServiceNames {

    /**
     * Bean-Name fuer den Zugriff auf die Data-Source.
     */
    public static final String DATA_SOURCE = "authentication.dataSource";

    /**
     * ServiceName fuer den Zugriff auf den AKLoginService. <br> Der LoginService ist vom Typ
     * <code>de.augustakom.authentication.service.AKLoginService</code>
     */
    public static final String LOGIN_SERVICE =
            "de.augustakom.authentication.service.AKLoginService";

    /**
     * ServiceName fuer den Zugriff auf den AKUserService. <br> Der UserService ist vom Typ
     * <code>de.augustakom.authentication.service.AKUserService</code>
     */
    public static final String USER_SERVICE =
            "de.augustakom.authentication.service.AKUserService";

    /**
     * ServiceName fuer den Zugriff auf den AKDepartmentService. <br> Der DepartmentService ist vom Typ
     * <code>de.augustakom.authentication.service.AKDepartmentService</code>
     */
    public static final String DEPARTMENT_SERVICE =
            "de.augustakom.authentication.service.AKDepartmentService";

    /**
     * ServiceName fuer den Zugriff auf den AKRoleService. <br> Der RoleService ist vom Typ
     * <code>de.augustakom.authentication.service.AKRoleService</code>
     */
    public static final String ROLE_SERVICE =
            "de.augustakom.authentication.service.AKRoleService";

    /**
     * ServiceName fuer den Zugriff auf den AKTeamService. <br> Der TeamService ist vom Typ
     * <code>de.augustakom.authentication.service.AKTeamService</code>
     */
    public static final String TEAM_SERVICE =
            "de.augustakom.authentication.service.AKTeamService";

    /**
     * ServiceName fuer den Zugriff auf den AKApplicationService. <br> Der ApplicationService ist vom Typ
     * <code>de.augustakom.authentication.service.AKApplicationService</code>
     */
    public static final String APPLICATION_SERVICE =
            "de.augustakom.authentication.service.AKApplicationService";

    /**
     * ServiceName fuer den Zugriff auf den AKUserSessionService. <br> Der UserSessionService ist vom Typ
     * <code>de.augustakom.authentication.service.AKUserService</code>
     */
    public static final String USERSESSION_SERVICE =
            "de.augustakom.authentication.service.AKUserService";

    /**
     * ServiceName fuer den Zugriff auf den AKAccountService. <br> Der AccountService ist vom Typ
     * <code>de.augustakom.authentication.service.AKAccountService</code>
     */
    public static final String ACCOUNT_SERVICE =
            "de.augustakom.authentication.service.AKAccountService";

    /**
     * ServiceName fuer den Zugriff auf den AKDbService. <br> Der DbService ist vom Typ
     * <code>de.augustakom.authentication.service.AKDbService</code>
     */
    public static final String DB_SERVICE =
            "de.augustakom.authentication.service.AKDbService";

    /**
     * ServiceName fuer den Zugriff auf den AKGUIService. <br> Der GUIService ist vom Typ
     * <code>de.augustakom.authentication.service.AKGUIService</code>
     */
    public static final String GUI_SERVICE =
            "de.augustakom.authentication.service.AKGUIService";

    /**
     * ServiceName fuer den Zugriff auf den AKBereichService. <br> Der AKBereichService ist vom Typ
     * <code>de.augustakom.authentication.service.AKBereichService</code>
     */
    public static final String BEREICH_SERVICE = "de.augustakom.authentication.service.AKBereichService";
}
