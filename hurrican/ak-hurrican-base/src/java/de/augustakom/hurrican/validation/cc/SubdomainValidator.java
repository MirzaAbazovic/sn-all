/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.01.2012 14:07:38
 */
package de.augustakom.hurrican.validation.cc;

import java.util.regex.*;

import de.augustakom.common.tools.messages.AKWarning;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterprise;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.MVSService;
import de.augustakom.hurrican.validation.cc.DomainValid.DomainValidator;

/**
 * Ueberprueft eine Subdomain auf ihre Gueltigkeit hin.
 *
 *
 * @since Release 11
 */
public class SubdomainValidator extends DomainValidator {

    /**
     * @param mvsService
     */
    public SubdomainValidator(MVSService mvsService) {
        super(mvsService);
    }

    /**
     * @see de.augustakom.hurrican.validation.cc.DomainValid.DomainValidator#getNotSet()
     */
    @Override
    protected String getNotSet() {
        return "Subdomain ist nicht gesetzt!";
    }

    /**
     * @see de.augustakom.hurrican.validation.cc.DomainValid.DomainValidator#getBiggerThanItShouldBe()
     */
    @Override
    protected String getBiggerThanItShouldBe() {
        return "Subdomain ist länger als die erlaubten 150 Zeichen!";
    }

    /**
     * @see de.augustakom.hurrican.validation.cc.DomainValid.DomainValidator#getPatternMissmatch()
     */
    @Override
    protected String getPatternMissmatch() {
        return "Die Subdomain darf maximal 150 Zeichen beinhalten. Es dürfen "
                + "darüberhinaus keine Sonderzeichen, Leerzeichen, Umlaute oder Großbuchstaben enhalten sein.";
    }

    /**
     * @see de.augustakom.hurrican.validation.cc.DomainValid.DomainValidator#getDomainPattern()
     */
    @Override
    protected Pattern getDomainPattern() {
        return Pattern.compile("^" + DOMAIN_LABEL_REGEX + "\\s*$");
    }

    public AKWarning validate(String domain, AuftragMVSEnterprise enterprise) throws FindException {
        AKWarning warning = null;

        try {
            checkNotSet(domain);
            checkBiggerThanAllowed(domain, 150);
            checkIsValidDomain(domain);
            checkSubdomainAlreadyUsedInEnterpriseDomain(domain, enterprise);
        }
        catch (IllegalArgumentException e) {
            warning = new AKWarning(this, e.getMessage());
        }
        return warning;
    }

    protected void checkSubdomainAlreadyUsedInEnterpriseDomain(String domain, AuftragMVSEnterprise enterprise)
            throws FindException {
        if (getMvsService().isSubdomainAlreadyUsedInEnterpriseDomain(enterprise, domain)) {
            throw new IllegalArgumentException(
                    String.format("Die angegebene Subdomain (%s) existiert bereits!", domain));
        }
    }

} // end
