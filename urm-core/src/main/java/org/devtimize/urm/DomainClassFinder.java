package org.devtimize.urm;

import com.google.common.collect.Sets;
import org.devtimize.urm.domain.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;

public class DomainClassFinder {

    private static final Logger logger = Logger.getLogger(DomainClassFinder.class.getName());

    private static final String URM_PACKAGE = "org.devtimize.urm";
    public static boolean ALLOW_FINDING_INTERNAL_CLASSES;

    public static ClassLoader[] classLoaders;

    public static List<Class<?>> findClasses(Configuration configuration, final URLClassLoader classLoader) {
        List<Class<?>> scannedClasses = getClasses(classLoader, configuration)
                .stream()
                .filter(DomainClassFinder::isNotPackageInfo)
                .filter(DomainClassFinder::isNotAnonymousClass)
                .filter((Class<?> clazz) -> !configuration.getIgnoreClasses().contains(clazz.getName()) &&
                        !configuration.getIgnoreClasses().contains(clazz.getSimpleName()))
                .sorted(Comparator.comparing(Class::getName))
                .collect(Collectors.toList());
        if(scannedClasses == null) {
            scannedClasses = EMPTY_LIST;
        }
        logger.info(String.format("Found classes as per the configuration: %s", scannedClasses));
        return scannedClasses;
    }

    private static boolean isNotPackageInfo(Class<?> clazz) {
        return !clazz.getSimpleName().equals("package-info");
    }

    private static boolean isNotAnonymousClass(Class<?> clazz) {
        return !clazz.getSimpleName().equals("");
    }

    private static Set<Class<?>> getClasses(URLClassLoader classLoader, Configuration configuration) {
        List<ClassLoader> classLoadersList = initializeClassLoaders(classLoader);
        FilterBuilder filter = prepareFilter(configuration);

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoaders))
                .filterInputsBy(filter)
                .addClassLoaders(classLoadersList)
        );
        return Sets.union(reflections.getSubTypesOf(Object.class),
                reflections.getSubTypesOf(Enum.class));
    }

    private static FilterBuilder prepareFilter(Configuration configuration) {
        FilterBuilder filter = new FilterBuilder();

        for(String packageName : configuration.getScanPackages()) {
            filter.include(FilterBuilder.prefix(packageName));
        }

        for(String packageName : configuration.getIgnorePackages()) {
            filter.exclude(FilterBuilder.prefix(packageName));
        }

        if (!isAllowFindingInternalClasses()) {
            filter.exclude(FilterBuilder.prefix(URM_PACKAGE));
        }
        return filter;
    }

    private static List<ClassLoader> initializeClassLoaders(URLClassLoader classLoader) {
        List<ClassLoader> classLoadersList = new LinkedList<>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());
        if (classLoader != null) {
            classLoadersList.add(classLoader);
        }

        classLoaders = classLoadersList.toArray(new ClassLoader[0]);
        return classLoadersList;
    }

    public static boolean isAllowFindingInternalClasses() {
        return ALLOW_FINDING_INTERNAL_CLASSES |= Boolean.parseBoolean(
                System.getProperty("DomainClassFinder.allowFindingInternalClasses", "false"));
    }

    private DomainClassFinder() {
        // private constructor for utility class
    }
}
