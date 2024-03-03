package de.bitconex.agreement.service.impl;

import de.bitconex.agreement.mapper.AgreementSpecificationMapper;
import de.bitconex.agreement.mapper.AgreementSpecificationMapperImpl;
import de.bitconex.agreement.model.AgreementSpecification;
import de.bitconex.agreement.model.AgreementSpecificationCreate;
import de.bitconex.agreement.model.AgreementSpecificationUpdate;
import de.bitconex.agreement.repository.AgreementSpecificationRepository;
import de.bitconex.agreement.service.AgreementSpecificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AgreementSpecificationServiceImpl implements AgreementSpecificationService {

    private final AgreementSpecificationRepository agreementSpecificationRepository;

    private final AgreementSpecificationMapper agreementSpecificationMapper = new AgreementSpecificationMapperImpl();

    public AgreementSpecificationServiceImpl(AgreementSpecificationRepository agreementSpecificationRepository) {
        this.agreementSpecificationRepository = agreementSpecificationRepository;
    }

    @Override
    public List<AgreementSpecification> getAllAgreementSpecifications() {
        return agreementSpecificationRepository.findAll();
    }

    @Override
    public AgreementSpecification getAgreementSpecification(String agreementSpecificationId) {
        return agreementSpecificationRepository.findById(agreementSpecificationId).orElse(null);
    }

    @Override
    public AgreementSpecification createAgreementSpecification(AgreementSpecificationCreate agreementSpecificationCreate) {
        AgreementSpecification agreementSpecification = agreementSpecificationMapper.toAgreementSpecification(agreementSpecificationCreate);

        agreementSpecification.setId(UUID.randomUUID().toString());
        return agreementSpecificationRepository.save(agreementSpecification);
    }

    @Override
    public void deleteAgreementSpecification(String agreementSpecificationId) {
        agreementSpecificationRepository.deleteById(agreementSpecificationId);
    }

    @Override
    public AgreementSpecification patchAgreementSpecification(String agreementSpecificationId, AgreementSpecificationUpdate agreementSpecificationUpdate) {
        AgreementSpecification agreementSpecificationToUpdate = getAgreementSpecification(agreementSpecificationId);

        if (agreementSpecificationToUpdate == null) {
            return null;
        }

        agreementSpecificationToUpdate = agreementSpecificationMapper.toAgreementSpecification(agreementSpecificationUpdate);
        // We make sure ID doesn't get overwritten with null
        agreementSpecificationToUpdate.setId(agreementSpecificationId);

        return agreementSpecificationRepository.save(agreementSpecificationToUpdate);
    }
}
