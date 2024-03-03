/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.02.2015
 */
package de.mnet.wbci.citrus.builder;

import java.util.*;
import javax.sql.*;
import javax.validation.*;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.augustakom.hurrican.model.billing.factory.TaifunDataFactory;
import de.mnet.wbci.model.Firma;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.FirmaTestBuilder;
import de.mnet.wbci.model.builder.PersonTestBuilder;
import de.mnet.wbci.model.builder.StandortBuilder;
import de.mnet.wbci.model.builder.StrasseBuilder;
import de.mnet.wbci.validation.groups.V1RequestVa;
import de.mnet.wbci.validation.groups.V1RequestVaKueMrn;
import de.mnet.wbci.validation.groups.V1RequestVaKueOrn;
import de.mnet.wbci.validation.groups.V1RequestVaRrnp;

/**
 * Wrapper for the {@link TaifunDataFactory} to verify that the generated random data will fit to the WBCI
 * specifications.
 *
 *
 */
public class WbciTaifunDatafactory extends TaifunDataFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(WbciTaifunDatafactory.class);

    private final WbciCdmVersion wbciCdmVersion;
    private final GeschaeftsfallTyp geschaeftsfallTyp;
    private final Class<?> validationGroup;
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private int count;

    public WbciTaifunDatafactory(DataSource taifunDataSource, DataSource hurricanDataSource, WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp geschaeftsfallTyp) {
        super(taifunDataSource, hurricanDataSource);
        this.wbciCdmVersion = wbciCdmVersion;
        this.geschaeftsfallTyp = geschaeftsfallTyp;
        this.validationGroup = lookupValidationGroup(geschaeftsfallTyp);
        count = 0;
    }

    private Class<?> lookupValidationGroup(GeschaeftsfallTyp geschaeftsfallTyp) {
        if (geschaeftsfallTyp != null) {
            switch (geschaeftsfallTyp) {
                case VA_KUE_MRN:
                    return V1RequestVaKueMrn.class;
                case VA_KUE_ORN:
                    return V1RequestVaKueOrn.class;
                case VA_RRNP:
                    return V1RequestVaRrnp.class;
                default:
                    break;
            }
        }
        return V1RequestVa.class;
    }

    @Override
    public GeneratedTaifunData createKunde(boolean privat, Long geoId, boolean createKundenBetreuer) {
        GeneratedTaifunData generatedTaifunData = super.createKunde(privat, geoId, createKundenBetreuer);
        try {
            checkKunde(generatedTaifunData.getKunde());
            checkAddress(generatedTaifunData.getAddress());
            return generatedTaifunData;
        }
        catch (InvalidRandomDataException e) {
            count++;
            LOGGER.debug("unvalid random data generated! ... try again");
            if (count < 50) {
                return this.createKunde(privat, geoId, createKundenBetreuer);
            }
            else {
                throw new CitrusRuntimeException("unable to create valid taifun data: " + e.getMessage(), e);
            }
        }
    }

    void checkKunde(Kunde kunde) throws InvalidRandomDataException {
        PersonOderFirma pof;
        if ("GEWERBLICH".equals(kunde.getKundenTyp())) {
            Firma firma = new FirmaTestBuilder().buildValid(wbciCdmVersion, geschaeftsfallTyp);
            firma.setFirmenname(kunde.getName());
            firma.setFirmennamenZusatz(kunde.getVorname());
            pof = firma;
        }
        else {
            Person person = new PersonTestBuilder().buildValid(wbciCdmVersion, geschaeftsfallTyp);
            person.setNachname(kunde.getName());
            person.setVorname(kunde.getVorname());
            pof = person;
        }
        Set<ConstraintViolation<PersonOderFirma>> constraints = validator.validate(pof, validationGroup);
        if (CollectionUtils.isNotEmpty(constraints)) {
            throw new InvalidRandomDataException("checkKunde", constraints);
        }
    }

    void checkAddress(Adresse address) throws InvalidRandomDataException {
        Standort standort = new StandortBuilder()
                .withOrt(address.getOrt())
                .withPostleitzahl(address.getPlzTrimmed())
                .withStrasse(new StrasseBuilder()
                        .withStrassenname(address.getStrasse())
                        .withHausnummer(address.getNummer())
                        .build())
                .build();

        Set<ConstraintViolation<Standort>> constraints = validator.validate(standort, validationGroup);
        if (CollectionUtils.isNotEmpty(constraints)) {
            throw new InvalidRandomDataException("checkAddress", constraints);
        }
    }

    /**
     * Special Exception which will be thrown if an value is not confirm to the WBCI specification.
     */
    static class InvalidRandomDataException extends Exception {
        private static final long serialVersionUID = 8406526192254649318L;

        public InvalidRandomDataException(String check, Set<? extends ConstraintViolation> constraints) {
            super(getErrorMessage(check, constraints));
        }

        private static String getErrorMessage(String check, Set<? extends ConstraintViolation> constraints) {
            StringBuilder sb = new StringBuilder(check).append(" created the following errors: ");
            constraints.stream().forEach(c -> sb.append(String.format("[%s#%s] %s, ",
                    c.getRootBeanClass().getSimpleName(),
                    c.getPropertyPath().toString(),
                    c.getMessage())));
            return StringUtils.removeEnd(sb.toString(), ", ");
        }
    }
}
