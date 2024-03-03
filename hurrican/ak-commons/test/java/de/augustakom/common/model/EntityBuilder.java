/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.08.2009 13:30:57
 */
package de.augustakom.common.model;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * Base entity builder.
 * <p/>
 * Kann eine Datenbank-Entity erstellen. Ist <em>nicht</em> Thread safe.
 * <p/>
 * 2 Generic-Parameter: BUILDER: Um command-chaining zu ermoeglichen ENTITY: Der persistente Typ, der durch den Builder
 * gebaut wird
 *
 *
 */
public abstract class EntityBuilder<BUILDER extends EntityBuilder<BUILDER, ENTITY>, ENTITY>
        implements ApplicationContextAware {

    private static final Logger LOGGER = Logger.getLogger(EntityBuilder.class);
    private static final Random RANDOM = new Random();

    private static final List<Class<?>> excludeParents;

    static {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(EntityBuilder.class);
        list.add(Observable.class);
        list.add(Object.class);
        excludeParents = Collections.unmodifiableList(list);
    }

    private static AtomicLong ID = new AtomicLong(Long.MAX_VALUE - 1000000);
    private static int depth = 0;

    private SessionFactory sessionFactory = null;
    private ApplicationContext applicationContext = null;

    protected final Class<ENTITY> entityClass;
    private final Map<String, Field> entityFields;
    private final Map<String, Field> builderFields;
    private final Map<String, String> idFieldMap;
    private boolean isBuilding = false;
    private boolean persist = true;
    private ENTITY lastBuilt = null;

    /**
     * Constructor ^-- useful comment for Constructor
     */
    public EntityBuilder() {
        this.entityClass = getEntityClass();
        this.entityFields = new HashMap<String, Field>();
        this.builderFields = new HashMap<String, Field>();
        this.idFieldMap = new HashMap<String, String>();
        // Recursively add all fields of the entity and the builder to the maps
        addFields(entityFields, entityClass);
        addFields(builderFields, this.getClass());
        // Since not all id fields might follow a common naming convention, we build another map
        for (Map.Entry<String, Field> builderFieldEntry : builderFields.entrySet()) {
            if (builderFieldEntry.getValue().isAnnotationPresent(ReferencedEntityId.class)) {
                String idField = builderFieldEntry.getValue().getAnnotation(ReferencedEntityId.class).value();
                String builderFieldName = builderFieldEntry.getKey();
                if (builderFieldName.endsWith("Builder")) {
                    builderFieldName = builderFieldName.substring(0, builderFieldName.length() - 7);
                }
                idFieldMap.put(idField, builderFieldName);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected Class<ENTITY> getEntityClass() {
        return (Class<ENTITY>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    private void addFields(Map<String, Field> fields, Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            int mod = field.getModifiers();
            if (!Modifier.isFinal(mod) && !Modifier.isStatic(mod) && !Modifier.isTransient(mod)) {
                field.setAccessible(true);
                fields.put(field.getName(), field);
            }
        }
        Class<?> parentClass = clazz.getSuperclass();
        if (!excludeParents.contains(parentClass)) {
            addFields(fields, parentClass);
        }
    }


    /**
     * Called after {@link #init()}. Override in subclasses to perform custom builder initialization.
     */
    protected void initialize() {
        // Perform custom builder initialization
    }


    /**
     * This is called by Spring after the bean was constructed and puts builders into all builder fields.
     * <p/>
     * Can also be called from test code when builder was created via new XxxBuilder().
     */
    @PostConstruct
    public BUILDER init() {
        try {
            SessionFactoryAware sessionFactoryAware = this.getClass().getAnnotation(SessionFactoryAware.class);
            if (sessionFactoryAware != null) {
                setSessionFactory((SessionFactory) applicationContext.getBean(sessionFactoryAware.value()));
            }

            for (Map.Entry<String, Field> builderFieldEntry : builderFields.entrySet()) {
                if ((builderFieldEntry.getKey().endsWith("Builder") &&
                        (builderFieldEntry.getValue().getAnnotation(DontCreateBuilder.class) == null))) {
                    Class<? extends EntityBuilder<?, ?>> builderClass;
                    UseBuilder useBuilder = builderFieldEntry.getValue().getAnnotation(UseBuilder.class);
                    if (useBuilder != null) {
                        builderClass = useBuilder.value();
                    }
                    else {
                        @SuppressWarnings("unchecked")
                        Class<? extends EntityBuilder<?, ?>> clazz =
                                (Class<? extends EntityBuilder<?, ?>>) builderFieldEntry.getValue().getType();
                        builderClass = clazz;
                    }
                    EntityBuilder<?, ?> builder = getBuilder(builderClass);
                    builderFieldEntry.getValue().set(this, builder);
                }
            }

            initialize();
        }
        catch (Exception e) {
            LOGGER.error("Error in builder initialization", e);
            throw new RuntimeException("Error in builder initialization", e);
        }
        @SuppressWarnings("unchecked")
        BUILDER result = (BUILDER) this;
        return result;
    }


    /**
     * Overwrite in extending classes to check if all important values are set, and if not, set them.
     * <p/>
     * This can be useful for example for other entities, which are usually set via withXxx, so that there is no need to
     * create temporary dummy objects if the object is set by the user, but we still have the chance to create a default
     * entity object if it was not set.
     */
    protected void beforeBuild() {
        // Check if all values are set.
        // If not, this is the last chance to set defaults
    }


    /**
     * Overwrite in extending classes to do some work after the entity was built.
     */
    protected void afterBuild(ENTITY entity) {
        // Do some work after the entity was built
    }


    /**
     * For formatting debugging output
     */
    private String getDepth(boolean increment) {
        if (!increment) {
            depth--;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            builder.append("    ");
        }
        if (increment) {
            depth++;
        }
        return builder.toString();
    }

    /**
     * Constructs the entity with the properties set from the builder and saves the entity in the database. Will call
     * {@link #beforeBuild()} before actually setting the properties from the builder.
     *
     * @return the built entity, never {@code null}
     * @throws RuntimeException if an error occurred
     */
    public ENTITY build() {
        LOGGER.info(getDepth(true) + "build() - Starting to create " + (persist ? "persistent" : "NON-PERSISTENT")
                + " object of type " + entityClass.getName());
        isBuilding = true;
        beforeBuild();
        ENTITY entity;
        Serializable newId = "<id>";
        try {
            Constructor<ENTITY> constructor = this.entityClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            entity = constructor.newInstance();
            for (Map.Entry<String, Field> entityFieldEntry : entityFields.entrySet()) {
                boolean success = false;
                String entityFieldName = entityFieldEntry.getKey();
                Field builderField = builderFields.get(entityFieldName);
                Field entityField = entityFieldEntry.getValue();

                if ((builderField != null) && (builderField.get(this) != null)) {
                    entityField.set(entity, builderField.get(this));
                    success = true;
                }
                else if (entityFieldName.endsWith("Id") || idFieldMap.containsKey(entityFieldName)) {
                    // If building something that needs an Id, and we have no Id field in the
                    // builder, we look for an entity field in the builder
                    Object referencedEntity = null;
                    if (idFieldMap.containsKey(entityFieldName)) {
                        entityFieldName = idFieldMap.get(entityFieldName);
                    }
                    else {
                        entityFieldName = entityFieldName.substring(0, entityFieldName.length() - 2);
                    }
                    // reload builder field with new entity field name
                    builderField = builderFields.get(entityFieldName);
                    if (builderField != null) {
                        referencedEntity = builderField.get(this);
                    }
                    // If the referenced entity is still null, we try to find a builder for it and build it
                    if (referencedEntity == null) {
                        referencedEntity = createEntityFromBuilder(builderField, entityFieldName);
                    }
                    success = setEntity(entity, entityFieldName, entityField, referencedEntity);
                }
                else {
                    Object referencedEntity = createEntityFromBuilder(builderField, entityFieldName);
                    success = setEntity(entity, entityFieldName, entityField, referencedEntity);
                }

                if (!success) {
                    LOGGER.debug("Field " + entityFieldEntry.getKey() + " of class " + entityClass.getName() +
                            " not set in Builder. Setting to null.");
                    entityFieldEntry.getValue().set(entity, null);
                }
            }
            if (persist && (sessionFactory != null)) {
                // Save in database
                newId = sessionFactory.getCurrentSession().save(entity);
                LOGGER.info("Built " + this.getClass().getSimpleName() + " with ID " + newId.toString());
                sessionFactory.getCurrentSession().flush();
            }
        }
        catch (Exception e) {
            LOGGER.error("Exception creating new instance of class " + entityClass.getName(), e);
            throw new RuntimeException("Exception creating new instance of class " + entityClass.getName(), e);
        }
        lastBuilt = entity;
        isBuilding = false;
        afterBuild(entity);
        LOGGER.info(getDepth(false) + "build() - Created " + (persist ? "persistent" : "NON-PERSISTENT")
                + " object of type " + entityClass.getName() + " with ID " + newId.toString());
        return entity;
    }


    /**
     * Creates an entity by getting the builder from the builder field with name 'entityFieldName + "Builder"' and
     * calling it's get() method. Also sets the entity in the builder if the builder has a field for the entity.
     *
     * @return the built entity. May return {@code null} on error.
     */
    private Object createEntityFromBuilder(Field builderField, String entityFieldName)
            throws SecurityException, IllegalArgumentException, IllegalAccessException {
        Object referencedEntity = null;
        Field referencedEntityBuilderField = builderFields.get(entityFieldName + "Builder");
        if (referencedEntityBuilderField != null) {
            referencedEntityBuilderField.setAccessible(true);
            EntityBuilder<?, ?> referencedEntityBuilder =
                    (EntityBuilder<?, ?>) referencedEntityBuilderField.get(this);
            if (referencedEntityBuilder != null) {
                if (!referencedEntityBuilder.isBuilding()) {
                    referencedEntity = referencedEntityBuilder.get();
                    if (builderField != null) { // set the entity in the builder if possible
                        builderField.set(this, referencedEntity);
                    }
                }
                else {
                    LOGGER.warn("createEntityFromBuilder() - Referenced EntityBuilder was building. Could not get referenced entity.");
                }
            }
        }
        return referencedEntity;
    }


    /**
     * Sets the entityId field of the entity from the ID field of the referenced entity, iff referencedEntity is not
     * {@code null}.
     *
     * @return true on success, otherwise false
     */
    private boolean setEntity(ENTITY entity, String entityFieldName, Field entityField, Object referencedEntity) throws IllegalAccessException {
        if (referencedEntity != null) {
            if (Number.class.isAssignableFrom(entityField.getType())) {
                String idFieldName = "id";
                Field referencedEntityBuilderField = builderFields.get(entityFieldName + "Builder");
                if (referencedEntityBuilderField != null) {
                    IdFieldName idFieldNameAnnotation = referencedEntityBuilderField.getAnnotation(IdFieldName.class);
                    if (idFieldNameAnnotation != null) {
                        idFieldName = idFieldNameAnnotation.value();
                    }
                }
                Field referencedEntityIdField = getField(referencedEntity.getClass(), idFieldName);
                referencedEntityIdField.setAccessible(true);
                Object referencedId = referencedEntityIdField.get(referencedEntity);
                entityField.set(entity, referencedId);
            }
            else {
                entityField.set(entity, referencedEntity);
            }
            return true;
        }
        return false;
    }


    /**
     * Get a field of an object. Check only declared fields. If not found, look in superclass. Repeat until found or no
     * superclass available.
     */
    private Field getField(Class<?> clazz, String fieldName) {
        Field field = null;
        while ((field == null) && (clazz != null)) {
            try {
                field = clazz.getDeclaredField(fieldName);
            }
            catch (Exception e) {
                // if field is not found, do nothing but check superclass
            }
            clazz = clazz.getSuperclass();
        }
        return field;
    }


    /**
     * Get the last built entity. May be {@code null}.
     */
    public ENTITY getLast() {
        return lastBuilt;
    }


    /**
     * Get the last built entity; if no entity was built, builds an entity and returns this.
     */
    public ENTITY get() {
        if (lastBuilt == null) {
            if (isBuilding) {
                throw new IllegalStateException("Builder is building. Cannot get entity.");
            }
            build();
        }
        return lastBuilt;
    }


    /**
     * Set the builder to not persist the entity in the database when building
     */
    public BUILDER setPersist(boolean persist) {
        this.persist = persist;
        @SuppressWarnings("unchecked")
        BUILDER result = (BUILDER) this;
        return result;
    }


    /**
     * Return if the builder is set to persist the entity in the database
     */
    public boolean getPersist() {
        return persist;
    }


    /**
     * This is set to true even before beforeBuild() is called, and is set to false only after afterBuild() has
     * returned.
     *
     * @return true iff this builder is in the process of building an entity.
     */
    public boolean isBuilding() {
        return isBuilding;
    }


    /**
     * Get a builder of a certain type from the application context of this builder.
     * <p/>
     * If no application context is set, just creates a new instance of the requested builder.
     */
    public <T extends EntityBuilder<?, ?>> T getBuilder(Class<T> clazz) {
        T result = null;
        if (applicationContext != null) {
            result = applicationContext.getBean(clazz.getName(), clazz);
        }
        else {
            try {
                result = clazz.newInstance();
            }
            catch (Exception e) {
                LOGGER.error("Could not instantiate builder with class " + clazz.getName());
            }
        }
        return result;
    }


    /**
     * Returns a random String
     */
    protected static String randomString() {
        return UUID.randomUUID().toString();
    }

    /**
     * Returns a random String
     */
    protected static String randomString(int maxLength) {
        String uuID = UUID.randomUUID().toString();
        return StringUtils.left(uuID, maxLength);
    }

    /**
     * Returns a Long between Long.MAX_VALUE - 1000000 and Long.MAX_VALUE
     */
    public static Long getLongId() {
        return ID.getAndIncrement();
    }

    /**
     * Returns a random Integer
     */
    public static Integer randomInt(int min, int max) {
        return RANDOM.nextInt((max - min) + 1) + min;
    }

    /**
     * Returns a random Long
     */
    protected static Long randomLong(int min) {
        Long x = Long.valueOf(randomInt(min, Integer.MAX_VALUE));
        return x;
    }

    protected static Long randomLong(int min, int max) {
        Long x = Long.valueOf(randomInt(min, max));
        return x;
    }

    /**
     * Set the session factory (=> Spring). Creates a hibernate template from the session factory.
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
