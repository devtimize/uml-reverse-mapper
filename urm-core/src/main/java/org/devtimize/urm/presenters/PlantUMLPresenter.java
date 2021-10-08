package org.devtimize.urm.presenters;

import org.devtimize.urm.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

/**
 * Created by moe on 06.04.16.
 *
 * More info about syntax: http://de.plantuml.com/classes.html
 */
public class PlantUMLPresenter implements Presenter {

    public static final String FILE_PREAMBLE = "@startuml";
    public static final String FILE_POSTAMBLE = "@enduml";
    public static final String ONE_CARDINAL = "\"1\"";
    public static final String MANY_CARDINAL = "\"*\"";
    private transient List<DomainClass> domainClasses;
    private Configuration configuration;
    private static final Logger log = LoggerFactory.getLogger(PlantUMLPresenter.class);

    private String describeInheritance(List<Edge> edges) {
        return edges.stream()
                .filter(e -> e.type == EdgeType.EXTENDS)
                .map(this::describeInheritance)
                .collect(joining());
    }

    private String describeInheritance(Edge hierarchyEdge) {
        String arrow = "--|>";
        if (hierarchyEdge.target.getClassType() == DomainClassType.INTERFACE
                && hierarchyEdge.source.getClassType() != DomainClassType.INTERFACE) {
            // if target is an interface and source is not, it is officially called
            // realization and uses a dashed line
            arrow = "..|>";
        }

        return String.format("%s %s %s \n",
                hierarchyEdge.source.getClassName(),
                arrow,
                hierarchyEdge.target.getClassName());
    }

    private String describePackages(List<DomainClass> domainClass) {
        return domainClass.stream()
                .collect(groupingBy(DomainClass::getPackageName))
                .entrySet().stream()
                .map(this::describePackage)
                .collect(joining());
    }

    private String describePackage(Map.Entry<String, List<DomainClass>> entry) {
        if(configuration.isShowPackageNames()) {
            return String.format("package %s {\n%s}\n",
                    entry.getKey(),
                    listDomainClasses(entry.getValue()));
        } else {
            return listDomainClasses(entry.getValue());
        }
    }

    private String listDomainClasses(List<DomainClass> domainClasses) {
        return domainClasses.stream()
                .map(this::describeDomainClass)
                .distinct()
                .collect(joining());
    }

    private String describeDomainClass(DomainClass domainClass){
        return String.format("  %s {%s%s%s\n  }\n",
                describeDomainClassType(domainClass),
                configuration.isShowFields() ? describeDomainClassFields(domainClass) : "",
                configuration.isShowConstructors() ? describeDomainClassConstructors(domainClass): "",
                configuration.isShowMethods() ? describeDomainClassMethods(domainClass): "");
    }

    private String describeDomainClassType(DomainClass domainClass) {
        String visi = "";
        if (domainClass.getVisibility() != Visibility.PUBLIC) {
            visi = domainClass.getVisibility().toString();
        }
        String className = domainClass.getUmlName();
        switch (domainClass.getClassType()) {
            case CLASS: return (domainClass.isAbstract() ? "abstract " : "")
                    + visi + "class " + className;
            case INTERFACE: return visi + "interface " + className;
            case ENUM: return visi + "enum " + className;
            case ANNOTATION: return visi + "annotation " + className;
        }
        return className;
    }

    private String describeDomainClassFields(DomainClass domainClass) {
        String description = domainClass.getFields().stream()
                .map(f -> f.getVisibility() + " " + f.getUmlName()
                        + (f.isStatic() ? " {static}" : "") + (f.isAbstract() ? " {abstract}" : ""))
                .collect(Collectors.joining("\n    "));
        return !description.equals("") ? "\n    " + description : "";
    }

    private String describeDomainClassConstructors(DomainClass domainClass) {
        String description = domainClass.getConstructors().stream()
                .map(c -> c.getVisibility() + " " + c.getUmlName())
                .collect(Collectors.joining("\n    "));
        return !description.equals("") ? "\n    " + description : "";
    }

    private String describeDomainClassMethods(DomainClass domainClass) {
        String description = domainClass.getMethods().stream()
                .map(m -> m.getVisibility() + " " + m.getUmlName()
                        + (m.isStatic() ? " {static}" : "") + (m.isAbstract() ? " {abstract}" : ""))
                .collect(Collectors.joining("\n    "));
        return !description.equals("") ? "\n    " + description : "";
    }

    private String describeCompositions(List<Edge> edges) {
        return edges.stream()
                .filter(e -> e.type != EdgeType.EXTENDS)
                .map(this::describeComposition)
                .collect(joining());
    }

    private String describeComposition(Edge compositionEdge) {
        return String.format("%s\n", describeEdge(compositionEdge));
    }

    private String describeEdge(Edge edge) {
        String sourceName = edge.source.getClassName();
        String targetName = edge.target.getClassName();

        String arrow = "--";
        String sourceCardinal = "";
        String targetCardinal = "";
        switch (edge.type) {
            case STATIC_INNER_CLASS:
                arrow = "+..";
                break;
            case INNER_CLASS:
                arrow = "+--";
                break;
            case ONE_TO_ONE:
                sourceCardinal = ONE_CARDINAL;
                targetCardinal = ONE_CARDINAL;
                arrow = "--o";
                break;
            case ONE_TO_MANY:
                sourceCardinal = ONE_CARDINAL;
                targetCardinal = MANY_CARDINAL;
                arrow = "--o";
                break;
            case MANY_TO_ONE:
                sourceCardinal = MANY_CARDINAL;
                targetCardinal = ONE_CARDINAL;
                arrow = "--o";
                break;
            case MANY_TO_MANY:
                sourceCardinal = MANY_CARDINAL;
                targetCardinal = MANY_CARDINAL;
                arrow = "--o";
                break;
            default:
                arrow = "--o";
                break;
        }

        return String.format("%s %s %s %s %s", sourceName, sourceCardinal, arrow, targetCardinal, targetName);
    }

    @Override
    public Representation describe(Configuration configuration, List<DomainClass> domainClasses, List<Edge> edges) {
        this.configuration = configuration;
        this.domainClasses = domainClasses;

        log.info("Preparing PlantUML class diagram..");
        log.info("Classes: " + domainClasses);
        log.info("Edges: " + edges);

        String content = FILE_PREAMBLE + "\n"
                + describePackages(domainClasses)
                + describeCompositions(edges)
                + describeInheritance(edges)
                + FILE_POSTAMBLE;
        return new Representation(content, "puml");
    }

    @Override
    public String getFileEnding() {
        return "puml";
    }

    private static String flip(String s) {
        String reversedString = new StringBuilder(s).reverse().toString();
        return reversedString.replaceAll("<", ">").replaceAll(">", "<");
    }

}
