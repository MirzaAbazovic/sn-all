package de.bitconex.tmf.resource_catalog.utility;

import com.mongodb.BasicDBObject;
import de.bitconex.tmf.resource_catalog.configuration.ApplicationContextProvider;
import de.bitconex.tmf.resource_catalog.repository.MongoCollectionRepository;
import org.bson.types.ObjectId;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.lang.reflect.Field;
import java.net.URI;
import java.sql.Date;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ModelUtils {

    public static void setIdAndHref(Object object, String path) {
        try {
            var fields = object.getClass().getDeclaredFields();

            // find id and href fields
            var modelId = Arrays.stream(fields)
                    .filter(field -> field.getName().equals("id"))
                    .findFirst()
                    .orElseThrow();
            var modelHref = Arrays.stream(fields)
                    .filter(field -> field.getName().equals("href"))
                    .findFirst()
                    .orElseThrow();

            // make them accessible (to be able to set values)
            modelId.setAccessible(true);
            modelHref.setAccessible(true);


            String objId = new ObjectId().toString();
            modelId.set(object, objId);

            Environment env = ApplicationContextProvider.getContext().getEnvironment();

            URI href = URI.create(env.getProperty("app.host") + env.getProperty("app.api-base-path") + path + "/" + objId);
            modelHref.set(object, href);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public static <T> List<T> getCollectionItems(Class<T> tClass, String fields, Integer offset, Integer limit, Map<String, String> filters) {
        List<T> list;

        filters.remove("fields");
        filters.remove("offset");
        filters.remove("limit");

        MongoTemplate mongoTemplate = ApplicationContextProvider.getContext().getBean(MongoTemplate.class);
        Query query = new Query();
        addFieldsToQuery(query, fields);
        if (!filters.isEmpty()) {
            Criteria criteria = new Criteria();
            filters.forEach((key, value) -> {
                Object criteriaValue = value;
                if (value.equalsIgnoreCase("true"))
                    criteriaValue = true;
                else if (value.equalsIgnoreCase("false"))
                    criteriaValue = false;
                var offsetDateTime = parseOffsetDateTime(value);
                if (offsetDateTime != null) {
                    var date = Date.from(offsetDateTime.toInstant());
                    var dateOffset = offsetDateTime.getOffset().toString();
                    criteriaValue = new BasicDBObject();
                    ((BasicDBObject) criteriaValue).put("dateTime", date);
                    ((BasicDBObject) criteriaValue).put("offset", dateOffset);
                }
                criteria.and(key).is(criteriaValue);
            });
            query.addCriteria(criteria);
        }

        list = mongoTemplate.find(query, tClass);
        var listSize = list.size();

        if (offset != null || limit != null) {
            if (offset == null || offset < 0)
                offset = 0;

            if (limit == null || limit < 0)
                limit = listSize;

            var startIndex = Math.min(offset, listSize);
            var endIndex = Math.min(offset + limit, listSize);
            list = list.subList(startIndex, endIndex);
        }
        return list;
    }


    public static <T> T getCollectionItem(Class<T> tClass, String id, String fields) {
        MongoTemplate mongoTemplate = ApplicationContextProvider.getContext().getBean(MongoTemplate.class);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        addFieldsToQuery(query, fields);
        return mongoTemplate.findOne(query, tClass);
    }

    public static void patchEntity(Object oldEntity, Object patchedEntity) {
        Field[] fields = oldEntity.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.get(patchedEntity) == null) // if field is null in update object, copy old field
                    field.set(patchedEntity, field.get(oldEntity));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void addFieldsToQuery(Query query, String fields) {
        if (fields != null && !fields.isEmpty()) {
            fields = fields + ",id,href";
            List<String> fieldList = Arrays.stream(fields.split(","))
                    .map(String::trim)
                    .toList();
            query.fields().include(fieldList.toArray(String[]::new));
        }
    }

    private static OffsetDateTime parseOffsetDateTime(String input) {
        var inputToParse = input.trim();
        inputToParse = inputToParse.replace("\"", "");
        inputToParse = inputToParse.replace(" ", "+");
        try {
            return OffsetDateTime.parse(inputToParse);
        } catch (Exception e) {
            return null;
        }
    }
}
