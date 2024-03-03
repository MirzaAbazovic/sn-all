package de.bitconex.agreement.service.impl;

import de.bitconex.agreement.mapper.AgreementMapper;
import de.bitconex.agreement.mapper.AgreementMapperImpl;
import de.bitconex.agreement.model.Agreement;
import de.bitconex.agreement.model.AgreementCreate;
import de.bitconex.agreement.model.AgreementUpdate;
import de.bitconex.agreement.repository.AgreementRepository;
import de.bitconex.agreement.service.AgreementService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AgreementServiceImpl implements AgreementService {

    private final AgreementRepository agreementRepository;


    private final AgreementMapper agreementMapper = new AgreementMapperImpl();

    public AgreementServiceImpl(AgreementRepository agreementRepository) {
        this.agreementRepository = agreementRepository;
    }


    @Override
    public List<Agreement> getAllAgreements() {
        return agreementRepository.findAll();
    }

    @Override
    public Agreement getAgreement(String agreementId) {
        return agreementRepository.findById(agreementId).orElse(null);
    }

    @Override
    public Agreement createAgreement(AgreementCreate agreementCreate) {
        Agreement agreement = agreementMapper.toAgreement(agreementCreate);

        agreement.setId(UUID.randomUUID().toString());
        agreement.setInitialDate(OffsetDateTime.now());

        return agreementRepository.save(agreement);
    }

    @Override
    public void deleteAgreement(String agreementId) {
        agreementRepository.deleteById(agreementId);
    }

    @Override
    public Agreement patchAgreement(String agreementId, AgreementUpdate agreementUpdate) {
        Agreement agreementToUpdate = getAgreement(agreementId);

        if (agreementToUpdate == null) {
            return null;
        }

        agreementToUpdate = agreementMapper.toAgreement(agreementUpdate);
        // We make sure ID doesn't get overwritten with null
        agreementToUpdate.setId(agreementId);

        return agreementRepository.save(agreementToUpdate);
    }
}
