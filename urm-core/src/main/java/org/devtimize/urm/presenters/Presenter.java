package org.devtimize.urm.presenters;

import org.devtimize.urm.domain.Configuration;
import org.devtimize.urm.domain.DomainClass;
import org.devtimize.urm.domain.Edge;

import java.util.List;

public interface Presenter {

    Representation describe(Configuration configuration, List<DomainClass> domainObjects, List<Edge> edges);

    String getFileEnding();

    /**
     * Factory method for {@link Presenter}
     * @param presenterString
     * @return chosen Presenter
     */
    static Presenter parse(String presenterString) {
        if (presenterString == null || presenterString.equalsIgnoreCase("plantuml")) {
            return new PlantUMLPresenter();
        } else if (presenterString.equalsIgnoreCase("graphviz")) {
            return new GraphvizPresenter();
        }
        return new PlantUMLPresenter();
    }
}
