package de.bitconex.tmf.party.repository;


import de.bitconex.tmf.party.models.Organization;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends MongoCollectionRepository<Organization> {

}