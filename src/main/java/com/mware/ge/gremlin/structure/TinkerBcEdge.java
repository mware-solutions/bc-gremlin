package com.mware.ge.gremlin.structure;

import com.mware.ge.Authorizations;
import com.mware.ge.Visibility;
import org.apache.tinkerpop.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.Iterator;
import java.util.stream.Stream;

public class TinkerBcEdge extends TinkerBcElement implements Edge {
    protected com.mware.ge.Edge bcEdge;

    public TinkerBcEdge(TinkerBcGraph graph, com.mware.ge.Edge bcEdge, Authorizations authorizations) {
        super(graph, bcEdge, authorizations);
        this.bcEdge = bcEdge;
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction) {
        switch (direction) {
            case OUT:
                return IteratorUtils.of(new TinkerBcVertex(graph, bcEdge.getVertex(com.mware.ge.Direction.OUT, authorizations), authorizations));
            case IN:
                return IteratorUtils.of(new TinkerBcVertex(graph, bcEdge.getVertex(com.mware.ge.Direction.IN, authorizations), authorizations));
            default:
                return IteratorUtils.of(
                        new TinkerBcVertex(graph, bcEdge.getVertex(com.mware.ge.Direction.OUT, authorizations), authorizations),
                        new TinkerBcVertex(graph, bcEdge.getVertex(com.mware.ge.Direction.IN, authorizations), authorizations)
                );
        }
    }

    @Override
    public <V> Property<V> property(String key, V value) {
        ElementHelper.validateProperty(key, value);
        bcEdge.setProperty(key, value, Visibility.EMPTY, authorizations);
        return new TinkerBcEdgeProperty<V>(this, key, value);
    }

    @Override
    public <V> Iterator<Property<V>> properties(String... propertyKeys) {
        return Stream.of(propertyKeys)
                .map(propKey -> (Property<V>) new TinkerBcEdgeProperty<V>(this, propKey, (V) bcEdge.getPropertyValue(propKey)))
                .iterator();
    }

    @Override
    public <V> Property<V> property(String key) {
        if(bcEdge.getProperty(key) != null) {
            return new TinkerBcEdgeProperty<V>(this, key, (V) bcEdge.getPropertyValue(key));
        } else
            return Property.empty();
    }

    @Override
    public String label() {
        return bcEdge.getLabel();
    }

    public String toString() {
        return StringFactory.edgeString(this);
    }
}
