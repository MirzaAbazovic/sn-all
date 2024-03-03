package de.bitconex.tmf.party.repository;

import com.mongodb.BasicDBObject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface MongoCollectionRepository<T> extends MongoRepository<T, String> {
    @Query(value = "{ }", fields = "?0")
    List<T> findAllWithAttributeSelection(BasicDBObject fields);

    @Query(value = "{ _id: ?0 }", fields = "?1")
    T findByIdWithAttributeSelection(String id, BasicDBObject fields);
}
