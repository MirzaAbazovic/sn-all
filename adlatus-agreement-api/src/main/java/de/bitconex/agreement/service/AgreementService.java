package de.bitconex.agreement.service;

import de.bitconex.agreement.model.Agreement;
import de.bitconex.agreement.model.AgreementCreate;
import de.bitconex.agreement.model.AgreementUpdate;

import java.util.List;

public interface AgreementService {

    List<Agreement> getAllAgreements();

    Agreement getAgreement(String agreementId);

    Agreement createAgreement(AgreementCreate agreementCreate);

    void deleteAgreement(String agreementId);

    Agreement patchAgreement(String agrementId, AgreementUpdate agreementUpdate);
}
