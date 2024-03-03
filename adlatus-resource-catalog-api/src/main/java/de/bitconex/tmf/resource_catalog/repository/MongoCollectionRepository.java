package de.bitconex.tmf.resource_catalog.repository;

import com.mongodb.BasicDBObject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Map;

@NoRepositoryBean
public interface MongoCollectionRepository<T> extends MongoRepository<T, String> {

    @Query(fields = "?0", value = "{}")
    List<T> findAllWithAttributeSelection(BasicDBObject fields);

    @Query(value = "{ _id: ?0 }", fields = "?1")
    T findByIdWithAttributeSelection(String id, BasicDBObject fields);

}
