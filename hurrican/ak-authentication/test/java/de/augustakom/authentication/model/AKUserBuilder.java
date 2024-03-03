/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.08.2009 11:22:34
 */
package de.augustakom.authentication.model;

import java.util.*;

import de.augustakom.common.model.EntityBuilder;

/**
 *
 */
@SuppressWarnings("unused")
public class AKUserBuilder extends EntityBuilder<AKUserBuilder, AKUser> {

    private Long id = null;
    private String loginName = "test-" + randomString();
    private String name = "Test User";
    private String firstName = "TestFirstName";
    private String email = "hurrican_developer@m-net.de";
    private String phone = "089/0000-000";
    private String phoneNeutral = "089-0000-0";
    private String fax = "089-0000-001";
    private AKDepartmentBuilder departmentBuilder = null;
    private boolean active = true;
    private Long niederlassungId = null;
    private boolean projektleiter = false;
    private boolean manager = false;
    private Long departmentId = null;

    private Set<AKRoleBuilder> roleBuilders = new HashSet<AKRoleBuilder>();
    private AKTeam team;

    @Override
    protected void initialize() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, 1);
    }

    @Override
    protected void afterBuild(AKUser entity) {
        for (AKRoleBuilder roleBuilder : roleBuilders) {
            getBuilder(AKUserRoleBuilder.class).withUserBuilder(this).withRoleBuilder(roleBuilder).build();
        }
    }

    public AKDepartmentBuilder getDepartmentBuilder() {
        return departmentBuilder;
    }

    public List<AKRoleBuilder> getRoleBuilders() {
        return new ArrayList<AKRoleBuilder>(roleBuilders);
    }

    public AKUserBuilder withRandomId() {
        this.id = getLongId();
        return this;
    }

    public AKUserBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public AKUserBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public AKUserBuilder withLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }

    public AKUserBuilder withRandomLoginName() {
        this.loginName = "test-" + randomString();
        return this;
    }

    public AKUserBuilder withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public AKUserBuilder withDepartmentBuilder(AKDepartmentBuilder departmentBuilder) {
        this.departmentBuilder = departmentBuilder;
        return this;
    }

    public AKUserBuilder addRoleBuilder(AKRoleBuilder roleBuilder) {
        roleBuilders.add(roleBuilder);
        return this;
    }

    public AKUserBuilder withoutRole() {
        roleBuilders.clear();
        return this;
    }

    public AKUserBuilder withProjektleiter(boolean isProjektleiter) {
        this.projektleiter = isProjektleiter;
        return this;
    }

    public AKUserBuilder withManager(boolean isManager) {
        this.manager = isManager;
        return this;
    }

    public AKUserBuilder withActive(boolean isActive) {
        this.active = isActive;
        return this;
    }

    public AKUserBuilder withDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
        return this;
    }

    public AKUserBuilder withNiederlassungId(Long niederlassungId) {
        this.niederlassungId = niederlassungId;
        return this;
    }

    public AKUserBuilder withTeam(AKTeam team) {
        this.team = team;
        return this;
    }

    public AKUserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public Long getDepartmentId() {
        return departmentId;
    }
}
