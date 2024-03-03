package de.bitconex.tmf.resource_catalog.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bitconex.tmf.resource_catalog.mapper.*;
import de.bitconex.tmf.resource_catalog.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataLoaderService {
    private final MongoTemplate mongoTemplate;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final ResourceCandidateMapper resourceCandidateMapper = new ResourceCandidateMapperImpl();
    private final ResourceCategoryMapper resourceCategoryMapper = new ResourceCategoryMapperImpl();
    private final ResourceSpecificationMapper resourceSpecificationMapper = new ResourceSpecificationMapperImpl();
    private final Logger logger = LoggerFactory.getLogger(DataLoaderService.class);

    public DataLoaderService(MongoTemplate mongoTemplate, @Qualifier("gridFsTemplate") ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.mongoTemplate = mongoTemplate;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    public void loadData() {
        try {
            // data is processed from the bottom up, because of the references
            logger.info("Seeding data from json...");
            var dataFile = resourceLoader.getResource("classpath:data.json");
            InputStream inputStream = dataFile.getInputStream();
            JsonNode rootNode = objectMapper.readTree(inputStream);

            JsonNode resourceCatalogNodes = rootNode.get("resourceCatalog");
            for (JsonNode resourceCatalogNode : resourceCatalogNodes) {
                var resourceCatalog = objectMapper.treeToValue(resourceCatalogNode, ResourceCatalog.class);
                ModelUtils.setIdAndHref(resourceCatalog, HrefTypes.ResourceCatalog.getHrefType());

                // process categories
                JsonNode resourceCategoryNodes = resourceCatalogNode.get("categories");
                List<ResourceCategoryRef> resourceCategories = new ArrayList<>(); // save refs to add them to resource catalog
                for (JsonNode resourceCategoryNode : resourceCategoryNodes) {
                    // process candidates
                    JsonNode resourceCandidateNodes = resourceCategoryNode.get("candidates");
                    List<ResourceCandidate> resourceCandidates = new ArrayList<>(); // save them to update them with category
                    for (JsonNode resourceCandidateNode : resourceCandidateNodes) {
                        ResourceCandidate resourceCandidate = objectMapper.treeToValue(resourceCandidateNode, ResourceCandidate.class);
                        ModelUtils.setIdAndHref(resourceCandidate, HrefTypes.ResourceCandidate.getHrefType());
                        // process specification
                        ResourceSpecification resSpec = objectMapper.treeToValue(resourceCandidateNode.get("specification"), ResourceSpecification.class);
                        ModelUtils.setIdAndHref(resSpec, HrefTypes.ResourceSpecification.getHrefType());
                        mongoTemplate.save(resSpec, CollectionNames.RESOURCE_SPECIFICATION);

                        resourceCandidate.setResourceSpecification(resourceSpecificationMapper.toResourceSpecificationRef(resSpec));
                        mongoTemplate.save(resourceCandidate, CollectionNames.RESOURCE_CANDIDATE);
                        resourceCandidates.add(resourceCandidate);
                    }

                    ResourceCategory resourceCategory = objectMapper.treeToValue(resourceCategoryNode, ResourceCategory.class);
                    ModelUtils.setIdAndHref(resourceCategory, HrefTypes.ResourceCategory.getHrefType());
                    resourceCategory.setResourceCandidate(resourceCandidates.stream().map(resourceCandidateMapper::toResourceCandidateRef).toList());
                    mongoTemplate.save(resourceCategory, CollectionNames.RESOURCE_CATEGORY);

                    ResourceCategoryRef resourceCategoryRef = resourceCategoryMapper.toResourceCategoryRef(resourceCategory);

                    // update resource candidates with category
                    for (ResourceCandidate resCandidate : resourceCandidates) {
                        resCandidate.setCategory(List.of(resourceCategoryRef));
                        mongoTemplate.save(resCandidate, CollectionNames.RESOURCE_CANDIDATE);
                    }
                    resourceCategories.add(resourceCategoryRef);
                }

                resourceCatalog.setCategory(resourceCategories);
                mongoTemplate.save(resourceCatalog, CollectionNames.RESOURCE_CATALOG);

            }

            logger.info("Seeding data from json completed.");
        } catch (IOException e) {
            logger.error("Error while seeding data from json: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void createGenericData() {
        // if there is no ResourceCatalog with name "Generic" create one
        Criteria criteria = Criteria.where("name").is("Generic");
        var catalog = mongoTemplate.findOne(Query.query(criteria), ResourceCatalog.class, CollectionNames.RESOURCE_CATALOG);

        if (catalog == null) {
            ResourceCatalog resourceCatalog = new ResourceCatalog();
            resourceCatalog.setName("Generic");
            mongoTemplate.save(resourceCatalog, CollectionNames.RESOURCE_CATALOG);
        }

        var category = mongoTemplate.findOne(Query.query(criteria), ResourceCategory.class, CollectionNames.RESOURCE_CATEGORY);
        if (category == null) {
            ResourceCategory resourceCategory = new ResourceCategory();
            resourceCategory.setName("Generic");
            mongoTemplate.save(resourceCategory, CollectionNames.RESOURCE_CATEGORY);
        }

        var candidate = mongoTemplate.findOne(Query.query(criteria), ResourceCandidate.class, CollectionNames.RESOURCE_CANDIDATE);
        if (candidate == null) {
            ResourceCandidate resourceCandidate = new ResourceCandidate();
            resourceCandidate.setName("Generic");
            mongoTemplate.save(resourceCandidate, CollectionNames.RESOURCE_CANDIDATE);
        }
    }
}
