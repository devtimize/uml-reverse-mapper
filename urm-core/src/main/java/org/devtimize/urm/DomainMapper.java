package org.devtimize.urm;

import org.devtimize.urm.domain.DomainClass;
import org.devtimize.urm.domain.Edge;
import org.devtimize.urm.domain.Configuration;
import org.devtimize.urm.presenters.Presenter;
import org.devtimize.urm.presenters.Representation;
import org.devtimize.urm.scanners.FieldScanner;
import org.devtimize.urm.scanners.HierarchyScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DomainMapper {

    private static final Logger log = LoggerFactory.getLogger(DomainMapper.class);
    private FieldScanner fieldScanner;
    private HierarchyScanner hierarchyScanner;
    private List<Class<?>> classes;
    private Presenter presenter;
    private Configuration configuration;

    DomainMapper(Presenter presenter, final List<Class<?>> classes) {
        init(presenter, classes);
    }

    private void init(Presenter presenter, List<Class<?>> classes) {
        this.presenter = presenter;
        this.classes = classes;
        fieldScanner = new FieldScanner(classes);
        hierarchyScanner = new HierarchyScanner(classes);
    }

    public Representation describeDomain() throws ClassNotFoundException {
        List<Edge> edges = new ArrayList<>();
        edges.addAll(fieldScanner.getEdges());
        edges.addAll(hierarchyScanner.getEdges());
        List<DomainClass> domainObjects = classes.stream().map(DomainClass::new).collect(Collectors.toList());
        return presenter.describe(configuration, domainObjects, edges);
    }

    public List<Class<?>> getClasses() {
        return classes;
    }

    public DomainMapper(Presenter presenter, Configuration configuration, URLClassLoader classLoader)
                                                                        throws ClassNotFoundException {
        this.configuration = configuration;
        List<Class<?>> allClasses = DomainClassFinder.findClasses(configuration, classLoader);
        init(presenter, allClasses);
    }
}
