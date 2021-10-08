package org.devtimize.urm.domain;

public enum EdgeType {

    ONE_TO_ONE, MANY_TO_ONE, MANY_TO_MANY, ONE_TO_MANY, EXTENDS, INNER_CLASS, STATIC_INNER_CLASS;

    public static EdgeType resolveEdgeType(EdgeType source, EdgeType target) {
        if (source.equals(target)) {
            switch (source) {
                case ONE_TO_ONE:
                case MANY_TO_MANY:
                    return source;
                case MANY_TO_ONE:
                case ONE_TO_MANY:
                    return MANY_TO_MANY;
                case EXTENDS:
                case INNER_CLASS:
                case STATIC_INNER_CLASS:
                    throw new RuntimeException("impossible! source: " + source + " | target: " + target);
            }
        }
        if (source.equals(ONE_TO_ONE)) {
            return target;
        } else if (target.equals(ONE_TO_ONE)) {
            return source;
        }
        throw new RuntimeException("impossible! source: " + source + " | target: " + target);
    }

    public boolean isCardinality() {
        return this == ONE_TO_ONE || this == ONE_TO_MANY
                || this == MANY_TO_ONE || this == MANY_TO_MANY;
    }
}
