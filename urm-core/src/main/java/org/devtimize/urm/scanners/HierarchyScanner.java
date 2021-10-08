package org.devtimize.urm.scanners;

import org.devtimize.urm.domain.DomainClass;
import org.devtimize.urm.domain.Edge;
import org.devtimize.urm.domain.EdgeType;

import java.util.ArrayList;
import java.util.List;

public class HierarchyScanner extends AbstractScanner {

    public HierarchyScanner(List<Class<?>> classes) {
        super(classes);
    }

    public List<Edge> getEdges() {
        List<Edge> edges = new ArrayList<>();
        for (Class<?> clazz : classes) {
            // show implemented interfaces
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> interfaze : interfaces) {
                if (isDomainClass(interfaze)) {
                    DomainClass child = new DomainClass(clazz);
                    DomainClass parent = new DomainClass(interfaze);
                    edges.add(new Edge(child, parent, EdgeType.EXTENDS));
                }
            }
            // show superclass
            Class<?> superclass = clazz.getSuperclass();
            if (isDomainClass(superclass)) {
                DomainClass child = new DomainClass(clazz);
                DomainClass parent = new DomainClass(superclass);
                edges.add(new Edge(child, parent, EdgeType.EXTENDS));
            }
        }
        return edges;
    }
}
