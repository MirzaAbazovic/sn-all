/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.11.2010 10:31:34
 */
package de.mnet.migration.common.util;

import java.lang.reflect.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import javax.annotation.*;
import javax.persistence.*;
import javax.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * Persisting via JDBC using special Oracle SQL "INSERT ALL ..." and hibernate annotations of the mapped entity. Only
 * annotated getters are used for the mapping! The mapper is taking into consideration the following annotations with
 * the specified parameters: <ul> <li>@Entity</li> <li>@Table(name = "...")</li> <li>@Column(name = "...")</li>
 * <li>@MappedSuperclass</li> <li>@Embeddable</li> <li>@Embedded</li> <li>@EmbeddedId</li> </ul>
 *
 *
 */
public class JdbcInsertAllMapper {
    @Resource(name = "targetDataSource")
    private DataSource dataSource;

    private static final ConcurrentMap<Class<?>, List<MethodInfo>> getter =
            new ConcurrentHashMap<Class<?>, List<MethodInfo>>();


    public Long getId(Messages messages, String sequence) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return Long.valueOf(jdbcTemplate.queryForObject("SELECT " + sequence + ".NEXTVAL FROM DUAL", Long.class));
    }


    /**
     * Insert a single entity using a "INSERT INTO ..." statement
     */
    public void persist(Messages messages, Object entity) {
        StringBuilder sql = new StringBuilder("INSERT ");
        List<Object> params = new ArrayList<Object>();
        map(messages, entity, sql, params);

        new JdbcTemplate(dataSource).update(sql.toString(), params.toArray());
    }


    /**
     * Insert multiple entities at once using a "INSERT ALL ..." statement
     */
    public void persist(Messages messages, Object[] entities) {
        int index = 0;
        while (index < entities.length) {
            StringBuilder sql = new StringBuilder("INSERT ALL ");
            List<Object> params = new ArrayList<Object>();
            for (; (index < entities.length) && (params.size() < 800); ++index) { // dirty, let's hope the next entity does not have more than 200 columns
                if (entities[index] != null) {
                    map(messages, entities[index], sql, params);
                }
            }
            sql.append(" SELECT * FROM DUAL");

            if (!params.isEmpty()) {
                new JdbcTemplate(dataSource).update(sql.toString(), params.toArray());
            }
        }
    }


    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            value = "REC_CATCH_EXCEPTION",
            justification = "Various exceptions are thrown and caught in one catch block")
    private void map(Messages messages, Object entity, StringBuilder sql, List<Object> params) {
        List<String> cols = new ArrayList<String>();
        List<Pair<MethodInfo, Object>> methods = new ArrayList<Pair<MethodInfo, Object>>();
        getMethods(messages, entity, methods);
        for (Pair<MethodInfo, Object> pair : methods) {
            Method method = pair.getFirst().method;
            if (pair.getFirst().column != null) {
                try {
                    Object obj = method.invoke(pair.getSecond());
                    if ((obj != null) && LocalDateTime.class.isAssignableFrom(obj.getClass())) {
                        obj = Date.from(((LocalDateTime) obj).atZone(ZoneId.systemDefault()).toInstant());
                    }
                    params.add(obj);
                    cols.add(pair.getFirst().column);
                }
                catch (Exception e) {
                    messages.ERROR.add("Could not get value from method " + method.getName() +
                            " of object of class " + entity.getClass().getName()).throwIt();
                }
            }
        }
        sql.append(" INTO ");
        sql.append(entity.getClass().getAnnotation(Table.class).name());
        sql.append(" (");
        for (int i = 0; i < cols.size(); ++i) {
            sql.append(cols.get(i));
            if (i != cols.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(") VALUES (");
        for (int i = 0; i < cols.size(); ++i) {
            sql.append("?");
            if (i != cols.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(") ");
    }


    public void getMethods(Messages messages, Object entity, List<Pair<MethodInfo, Object>> list) {
        List<MethodInfo> methods = getter.get(entity.getClass());
        if (methods == null) {
            methods = getMethodInfo(entity);
            getter.put(entity.getClass(), methods);
        }
        for (MethodInfo methodInfo : methods) {
            if (methodInfo.embedded) {
                try {
                    Object obj = methodInfo.method.invoke(entity);
                    if (obj != null) {
                        getMethods(messages, obj, list);
                    }
                }
                catch (Exception e) {
                    messages.ERROR.add("Could not get value from method " + methodInfo.method.getName() +
                            " of object of class " + entity.getClass().getName()).throwIt();
                }
            }
            else {
                list.add(Pair.create(methodInfo, entity));
            }
        }
    }


    private List<MethodInfo> getMethodInfo(Object entity) {
        List<MethodInfo> methods;
        methods = new ArrayList<MethodInfo>();
        List<Method> pureMethods = getAllGetters(Object.class, entity.getClass());
        ReflectionUtil.filterTransient(pureMethods);
        for (Method method : pureMethods) {
            Embedded embedded = method.getAnnotation(Embedded.class);
            EmbeddedId embeddedId = method.getAnnotation(EmbeddedId.class);
            Column column = method.getAnnotation(Column.class);
            if ((embedded != null) || (embeddedId != null) || (column != null)) {
                method.setAccessible(true);
                methods.add(new MethodInfo(method, (embeddedId != null) || (embedded != null),
                        column != null ? column.name() : null));
            }
        }
        return methods;
    }


    public static List<Method> getAllGetters(Class<?> upperBound, Class<?> actualClass) {
        List<Method> methods = new ArrayList<Method>();
        while ((actualClass != null) && upperBound.isAssignableFrom(actualClass)) {
            Entity entity = actualClass.getAnnotation(Entity.class);
            Embeddable embeddable = actualClass.getAnnotation(Embeddable.class);
            MappedSuperclass mapped = actualClass.getAnnotation(MappedSuperclass.class);
            if ((entity != null) || (mapped != null) || (embeddable != null)) {
                for (Method method : actualClass.getDeclaredMethods()) {
                    if (method.getName().startsWith("get") && !method.getName().contains("$")) {
                        methods.add(method);
                    }
                }
            }
            actualClass = actualClass.getSuperclass();
        }
        return methods;
    }


    private static class MethodInfo {
        public Method method;
        public boolean embedded;
        public String column;

        public MethodInfo(Method method, boolean embedded, String column) {
            this.method = method;
            this.embedded = embedded;
            this.column = column;
        }
    }
}
