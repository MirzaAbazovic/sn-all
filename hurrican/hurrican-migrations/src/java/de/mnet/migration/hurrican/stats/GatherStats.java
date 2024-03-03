/**
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2012 12:00
 */
package de.mnet.migration.hurrican.stats;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.persistence.*;
import javax.sql.*;
import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import de.mnet.migration.common.result.MigrationStatus;
import de.mnet.migration.common.util.CollectionUtil;
import de.mnet.migration.common.util.ColumnName;
import de.mnet.migration.common.util.InitializeLog4J;
import de.mnet.migration.common.util.ReflectionMapper;
import de.mnet.migration.common.util.ReflectionUtil;


/**
 * Klasse, um Statistiken ueber gelaufene Migrationen zu erstellen
 */
public class GatherStats {
    private static final Logger LOGGER = Logger.getLogger(GatherStats.class);

    protected static List<String> getBaseConfiguration() {
        return CollectionUtil.list(
                "de/mnet/migration/common/resources/base-migration.xml",
                "de/mnet/migration/hurrican/common/resources/base-migration.xml"
        );
    }

    public static void main(String[] args) {
        InitializeLog4J.initializeLog4J("log4j-migration");

        LOGGER.info("startMigration() - Starting to gather stats");

        ConfigurableApplicationContext context;
        try {
            context = new ClassPathXmlApplicationContext(getBaseConfiguration().toArray(new String[] { }), true, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("Error during initialization of Application Context: " + e.getMessage(), e);
        }

        List<NaviMigResult> hurricanResult = getResultFrom("Hurrican", "hurricanDataSource", context);

        List<NaviMigResult> all = new ArrayList<>(hurricanResult);
        Collections.sort(all, new NaviMigResultComparator());

        try {
            writeStats(all);
            writeTimes(all);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        context.close();
    }


    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "DM_DEFAULT_ENCODING", justification = "Default Encoding genuegt")
    private static void writeTimes(List<NaviMigResult> all) throws IOException, IllegalAccessException {
        ICsvMapWriter writer = new CsvMapWriter(new FileWriter("target/migrationTimes.csv"), CsvPreference.EXCEL_PREFERENCE);
        try {
            List<String> header = new ArrayList<>();
            Map<String, ? super Object> data = new HashMap<>();
            long other = 0;
            for (NaviMigResult result : all) {
                if (result.duration >= 10) {
                    String name = result.system + ":" + result.migrationName;
                    header.add(name);
                    data.put(name, result.duration);
                }
                else {
                    other += result.duration;
                }
            }
            if (other > 0) {
                header.add("Andere");
                data.put("Andere", other);
            }
            String[] headerArr = header.toArray(new String[header.size()]);
            writer.writeHeader(headerArr);
            writer.write(data, headerArr);
        }
        finally {
            writer.close();
        }
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "DM_DEFAULT_ENCODING", justification = "Default Encoding genuegt")
    private static void writeStats(List<NaviMigResult> all) throws IOException, IllegalAccessException {
        ICsvMapWriter writer = new CsvMapWriter(new FileWriter("target/migrationStats.csv"), CsvPreference.EXCEL_PREFERENCE);
        try {
            List<String> header = new ArrayList<>();
            List<Field> fields = ReflectionUtil.filterStaticAndFinal(ReflectionUtil.getAllFields(NaviMigResult.class, NaviMigResult.class));
            for (Field field : fields) {
                header.add(field.getName().toUpperCase());
            }
            List<Map<String, ? super Object>> dataList = new ArrayList<>();
            for (NaviMigResult result : all) {
                Map<String, ? super Object> data = new HashMap<>();
                dataList.add(data);
                for (Field field : fields) {
                    Object object = field.get(result);
                    data.put(field.getName().toUpperCase(), object == null ? "<null>" : object.toString());
                }
            }
            String[] headerArr = header.toArray(new String[header.size()]);
            writer.writeHeader(headerArr);
            for (Map<String, ? super Object> data : dataList) {
                writer.write(data, headerArr);
            }
        }
        finally {
            writer.close();
        }
    }


    private static List<NaviMigResult> getResultFrom(String system, String dataSourceName,
            ConfigurableApplicationContext context) {
        DataSource dataSource = (DataSource) context.getBean(dataSourceName);
        ReflectionMapper<NaviMigResult> mapper = new ReflectionMapper<>();
        mapper.setEntityClass(NaviMigResult.class.getName());
        List<NaviMigResult> result = new JdbcTemplate(dataSource).query("SELECT * FROM HURRICAN_MIG_RESULT", mapper);
        Collections.sort(result, new NaviMigResultComparator());
        for (Iterator<NaviMigResult> iterator = result.iterator(); iterator.hasNext(); ) {
            NaviMigResult naviMigResult = iterator.next();
            if (naviMigResult.migrationName.matches(".*-[0-9]+") ||
                    (naviMigResult.updateTimestamp == null) || (naviMigResult.insertTimestamp == null)) {
                iterator.remove();
                continue;
            }
            naviMigResult.system = system;
            Long durationSec = (naviMigResult.updateTimestamp.getTime() - naviMigResult.insertTimestamp.getTime()) / 1000;
            naviMigResult.duration = durationSec / 60;
            if (durationSec > 0) {
                naviMigResult.transformsPerSec = naviMigResult.counter / durationSec;
            }
            else {
                naviMigResult.transformsPerSec = 0L;
            }
            naviMigResult.date = String.format("%1$td.%1$tm.%1$tY", result.get(result.size() - 1).updateTimestamp);
        }
        return result;
    }


    public static class NaviMigResultComparator implements Comparator<NaviMigResult>, Serializable {
        private static final long serialVersionUID = -8104461568065248055L;

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(NaviMigResult o1, NaviMigResult o2) {
            if ((o1.system != null) && (o2.system == null)) {
                return -1;
            }
            else if ((o1.system == null) && (o2.system != null)) {
                return 1;
            }
            else if ((o1.system != null) && (o2.system != null)) {
                Integer sys = o1.system.compareTo(o2.system);
                if (sys != null) {
                    return sys;
                }
            }
            return o1.id.compareTo(o2.id);
        }
    }


    public static class NaviMigResult {
        @Transient
        public String system;
        public Long id;
        public String migrationName;
        public String migrationSuite;
        @ColumnName("SUCCESS")
        public String migrationStatus;
        public Long counter;
        public Long migrated;
        public Long info;
        public Long warnings;
        public Long badData;
        public Long errors;
        public Long skipped;
        public Date insertTimestamp;
        public Date updateTimestamp;
        @Transient
        public String date;
        @Transient
        public Long duration;
        @Transient
        public Long transformsPerSec;

        public MigrationStatus getMigrationStatus() {
            return MigrationStatus.valueOf(migrationStatus);
        }
    }
}
