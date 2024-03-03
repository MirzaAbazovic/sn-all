/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.09.2009
 */
package de.augustakom.common.tools.lang;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class PropertyUtil {

    private static final Logger LOGGER = Logger.getLogger(PropertyUtil.class);

    public static final String PROPERTY_VALUE__TO_BE_DEFINED = "<HAS_TO_BE_DEFINED>";

    /**
     * The config that should be loaded can be overridden with this system property
     */
    public static final String SYSTEM_PROPERTY_TO_OVERRIDE_PROPERTY_MECHANISM = "use.config";

    /**
     * private cache if the properties are read in multiple times, speeds up the initial configuration loading
     */
    private static final Map<String, Properties> cachedProperties = Collections
            .synchronizedMap(new HashMap<>());

    /**
     * set this to true to enable display of scanned files to system.out
     */
    private static volatile boolean debug = false;

    public static boolean isDebug() {
        return debug;
    }

    /**
     * Set debug mode on
     */
    public static void setDebug(boolean debug) {
        PropertyUtil.debug = debug;
    }

    /**
     * Convenience method to call {@link #loadPropertyHierarchy(List, String, boolean)} with single fileName
     *
     * @param fileName base filename of property file which will be scanned
     * @param appendix common appendix of property files to scan
     * @param useCache if true, no file scan will be made, although given properties will be overridden
     */
    public static Properties loadPropertyHierarchy(String fileName, String appendix, boolean useCache) {
        return loadPropertyHierarchy(Arrays.asList(fileName), appendix, useCache);
    }

    /**
     * get Properties from a given resource name f and a given appendix for the current system. The system classloader
     * is also taken into account for locating the properties.
     * <p/>
     * The loading of property files is additive, entries can be overwritten by files/resources, which are loaded
     * later.
     * <p/>
     *
     * @param fileNames list of base filenames of property files which will be scanned in order (least important is
     *                  first)
     * @param appendix  common appendix of property files to scan (e.g. 'properties')
     * @param useCache  if true, no file scan will be made, although given properties will be overridden
     */
    public static Properties loadPropertyHierarchy(List<String> fileNames, String appendix, boolean useCache) {

        String cacheKey = Arrays.toString(fileNames.toArray());
        if (appendix != null) {
            cacheKey = cacheKey + "." + appendix;
        }

        if (useCache) {
            Properties cachedProps = cachedProperties.get(cacheKey);
            if (cachedProps != null) {
                return cachedProps;
            }
        }

        Properties props = new Properties();
        for (String fileBaseName : fileNames) {
            loadConfigSystem(props, fileBaseName, appendix);
        }

        RecursivePropertyResolver resolver = new RecursivePropertyResolver();
        resolver.resolve(props);

        performSanityCheck(cacheKey, props);
        if (useCache) {
            cachedProperties.put(cacheKey, props);
        }

        if (LOGGER.isInfoEnabled()) {
            final String properties = props.entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .map(s -> s.contains("password") ? s.split("=")[0] + "=" + "*****" : s)
                    .sorted()
                    .reduce("", (String z, String s) -> (z + s + "\n"));
            LOGGER.info(properties);
        }

        return props;
    }

    private static void performSanityCheck(String configName, Properties props) {
        Set<String> missingKeys = new HashSet<>();
        // sanity check: check for values with spaces at the end
        Enumeration<?> propertyEnum = props.propertyNames();
        while (propertyEnum.hasMoreElements()) {
            String key = (String) propertyEnum.nextElement();
            String value = props.getProperty(key);
            if (value != null) {
                // don't show lines that end with "\n", that is ok and sometimes needed
                if (!value.trim().equals(value) && !value.endsWith("\n")) {
                    String message = "The value \"" + value + "\" for key " + key
                            + " seems to end with a space. Is this really intended??";
                    if (debug) {
                        LOGGER.debug(message);
                    }
                    else {
                        LOGGER.info("loadConfigSystem() - " + message);
                    }
                }
                if (PROPERTY_VALUE__TO_BE_DEFINED.equals(value)) {
                    missingKeys.add(key);
                }
            }
        }
        if (!missingKeys.isEmpty()) {
            StringBuilder errorString = new StringBuilder(missingKeys.size()
                    + " key(s) have value '" + PROPERTY_VALUE__TO_BE_DEFINED + "', which means that your config '"
                    + configName + "' is incomplete\n");
            errorString.append("Missing Keys are:\n");
            for (String key : missingKeys) {
                errorString.append(key).append("\n");
            }
            if (debug) {
                System.err.println(errorString);
            }
            else {
                LOGGER.error("loadConfigSystem() - " + errorString);
            }
            throw new RuntimeException(errorString.toString());
        }
    }

    private static void loadConfigSystem(Properties props, String fileBaseName, String appendixIn) {
        String appendix = appendixIn;
        Set<String> lookedAt = new HashSet<>();

        if ((appendix != null) && (appendix.charAt(0) != '.')) {
            appendix = "." + appendix;
        }

        // load with default appendix
        loadAll(props, fileBaseName, "default", appendix, lookedAt);

        // load without appendix
        loadAll(props, fileBaseName, null, appendix, lookedAt);

        // override with a defined config
        String configToUse = System.getProperty(SYSTEM_PROPERTY_TO_OVERRIDE_PROPERTY_MECHANISM);
        if (StringUtils.isNotBlank(configToUse)) {
            loadAll(props, fileBaseName, configToUse, appendix, lookedAt);
        }

        // override properties set in system properties
        overrideWithSystemProperties(props);
    }

    private static void overrideWithSystemProperties(Properties props) {
        Properties sysProps = System.getProperties();
        for (String key : sysProps.stringPropertyNames()) {
            // override property only if it is already defined
            String oldValue = props.getProperty(key);
            if (oldValue != null) {
                props.setProperty(key, sysProps.getProperty(key));
                String infoString = "overriding existing property " + key + " with system value";
                LOGGER.info("overrideWithSystemProperties() - " + infoString);
            }
        }

    }

    /**
     * Load properties from a file
     */
    private static void load(Properties p, String filename, Set<String> lookedAtFilenames) {
        try {
            File f = new File(filename);

            log("load() - Trying to load properties from file " + f.getCanonicalPath());
            if (f.canRead()) {
                if (isAlreadyRead(f.getCanonicalPath(), lookedAtFilenames)) {
                    log("load() - file already read");
                    return;
                }

                FileInputStream file = new FileInputStream(f);

                try {
                    p.load(file);
                    if (debug) {
                        LOGGER.debug("Properties loaded from file " + f.getCanonicalPath());
                    }
                    else {
                        LOGGER.info("load() - Properties loaded from file " + f.getCanonicalPath());
                    }
                    addRead(f.getCanonicalPath(), lookedAtFilenames);
                }
                finally {
                    file.close();
                }
            }
            else {
                log("load() - file not readable");
            }
        }
        catch (RuntimeException ignored) {
            log("load() - ignoring RuntimeException " + ignored
                    + "; it's OK for this to fail, since we're trying multiple files");
        }
        catch (Exception ignored) {
            log("load() - ignoring Exception " + ignored
                    + "; it's OK for this to fail, since we're trying multiple files");
        }
    }


    private static void log(String message) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(message);
        }
        if (debug) {
            LOGGER.debug(message);
        }
    }


    /**
     * try to load something from the class loader, e.g. in jars
     */
    private static void loadFromClassLoader(Properties p, String filename, Set<String> lookedAt) {
        log("load() - Trying to load properties from resource " + filename);

        String resourceName = "resource:" + filename;
        if (isAlreadyRead(resourceName, lookedAt)) {
            LOGGER.debug("load() - file already read");
            return;
        }

        try {
            // try system classloader
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);

            if (in != null) {
                try {
                    p.load(in);
                    if (debug) {
                        LOGGER.debug("Properties loaded from resource (system) " + filename);
                    }
                    else {
                        LOGGER.info("load() - Properties loaded from resource (system) " + filename);
                    }
                    addRead(resourceName, lookedAt);
                }
                finally {
                    in.close();
                }
            }
            else {
                log("load() - file not present/readable");
            }
        }
        catch (IOException ignored) {
            String message = "(ignored) error loading properties from resource (system) " + filename + ":";
            if (debug) {
                System.err.println(message);
            }
            LOGGER.error("load() - " + message, ignored);
        }
    }

    /**
     * add to Properties p the properties in a file/resource f, having a given postfix and a given appendix, using the
     * system classloader
     */
    private static void loadAll(Properties p, String f, String postfixIn, String appendix, Set<String> lookedAt) {
        String postfix = postfixIn;
        String user = System.getProperty("user.name").toLowerCase();
        String dir = System.getProperty("user.dir");
        String fileSeparator = System.getProperty("file.separator");
        boolean runsInTomcat = System.getProperty("catalina.base") != null;
        String catalinaConf = System.getProperty("catalina.base") + fileSeparator + "conf";

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("loadAll() - Checking '" + f + appendix + "' for property postfix <" + postfix + ">");
        }
        if (debug) {
            LOGGER.debug("loadAll() - Checking '" + f + appendix + "' for property postfix <" + postfix + ">");
        }

        if (postfix == null) {
            postfix = "";
        }
        else {
            postfix = "." + postfix;
        }

        // first try classloader
        loadFromClassLoader(p, f + postfix + appendix, lookedAt);
        loadFromClassLoader(p, user + "/." + f + postfix + appendix, lookedAt);
        loadFromClassLoader(p, user + '/' + f + postfix + appendix, lookedAt);
        loadFromClassLoader(p, "." + f + postfix + "." + user + appendix, lookedAt);
        loadFromClassLoader(p, "." + f + postfix + ".user." + user + appendix, lookedAt);
        loadFromClassLoader(p, f + postfix + "." + user + appendix, lookedAt);
        loadFromClassLoader(p, f + postfix + ".user." + user + appendix, lookedAt);

        // and then files
        load(p, dir + fileSeparator + "." + f + postfix + appendix, lookedAt);
        load(p, dir + fileSeparator + f + postfix + appendix, lookedAt);
        load(p, f + postfix + appendix, lookedAt);
        if (runsInTomcat) {
            load(p, catalinaConf + fileSeparator + "." + f + postfix + appendix, lookedAt);
            load(p, catalinaConf + fileSeparator + f + postfix + appendix, lookedAt);
        }
    }

    private static boolean isAlreadyRead(String canonicalfilename, Set<String> lookedAt) {
        if (lookedAt.contains(canonicalfilename)) {
            return true;
        }
        return false;
    }

    private static void addRead(String canonicalfilename, Set<String> lookedAt) {
        lookedAt.add(canonicalfilename);
    }

    public static void main(String[] args) {
        LOGGER.setLevel(Level.TRACE);
        Properties props;
        if (args.length >= 2) {
            props = loadPropertyHierarchy(args[0], args[1], false);
        }
        else {
            props = loadPropertyHierarchy("Application", "properties", false);
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("main() - found " + props.size() + " entries");
        }
        System.out.println(props.toString());
    }

}
