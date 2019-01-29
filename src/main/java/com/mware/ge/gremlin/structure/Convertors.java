package com.mware.ge.gremlin.structure;

import org.apache.tinkerpop.gremlin.structure.Direction;

public class Convertors {
    public static com.mware.ge.Direction toBcDirection(Direction direction) {
        return com.mware.ge.Direction.valueOf(direction.name());
    }
}
