package de.bitconex.tmf.party.repository;


import de.bitconex.tmf.party.models.Individual;
import org.springframework.stereotype.Repository;

@Repository
public interface IndividualRepository extends MongoCollectionRepository<Individual> {

}
