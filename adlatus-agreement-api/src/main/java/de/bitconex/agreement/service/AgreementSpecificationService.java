package de.bitconex.agreement.service;

import de.bitconex.agreement.model.AgreementSpecification;
import de.bitconex.agreement.model.AgreementSpecificationCreate;
import de.bitconex.agreement.model.AgreementSpecificationUpdate;

import java.util.List;

public interface AgreementSpecificationService {

    List<AgreementSpecification> getAllAgreementSpecifications();

    AgreementSpecification getAgreementSpecification(String agreementSpecificationId);

    AgreementSpecification createAgreementSpecification(AgreementSpecificationCreate agreementSpecificationCreate);

    void deleteAgreementSpecification(String agreementSpecificationId);

    AgreementSpecification patchAgreementSpecification(String agreementSpecificationId, AgreementSpecificationUpdate agreementSpecificationUpdate);
}
