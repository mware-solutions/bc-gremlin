package com.mware.bigconnect.gremlin.structure;

import com.mware.core.model.properties.BcProperties;
import com.mware.ge.Authorizations;
import com.mware.ge.EdgeBuilderBase;
import com.mware.ge.Visibility;
import org.apache.tinkerpop.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.Iterator;
import java.util.stream.Stream;

public class TinkerBcVertex extends TinkerBcElement implements Vertex {
    private final com.mware.ge.Vertex bcVertex;

    public TinkerBcVertex(TinkerBcGraph graph, com.mware.ge.Vertex geVertex, Authorizations authorizations) {
        super(graph, geVertex, authorizations);
        this.bcVertex = geVertex;
    }

    @Override
    public Edge addEdge(String label, Vertex inVertex, Object... keyValues) {
        if (null == inVertex) throw Graph.Exceptions.argumentCanNotBeNull("inVertex");
        ElementHelper.validateLabel(label);
        ElementHelper.legalPropertyKeyValueArray(keyValues);

        EdgeBuilderBase eb;
        if (ElementHelper.getIdValue(keyValues).isPresent()) {
            String edgeId = ElementHelper.getIdValue(keyValues).get().toString();
            eb = graph.getBcGraph().prepareEdge(edgeId, id().toString(), inVertex.id().toString(), label, Visibility.EMPTY);
        } else {
            eb = graph.getBcGraph().prepareEdge(id().toString(), inVertex.id().toString(), label, Visibility.EMPTY);
        }

        TinkerBcEdge edge = new TinkerBcEdge(graph, eb.save(authorizations), authorizations);
        ElementHelper.attachProperties(edge, keyValues);
        return edge;
    }

    @Override
    public <V> VertexProperty<V> property(String key, V value) {
        return this.property(VertexProperty.Cardinality.single, key, value);
    }

    @Override
    public <V> VertexProperty<V> property(VertexProperty.Cardinality cardinality, String key, V value, Object... keyValues) {
        ElementHelper.validateProperty(key, value);
        if (ElementHelper.getIdValue(keyValues).isPresent())
            throw Vertex.Exceptions.userSuppliedIdsNotSupported();
        if (cardinality != VertexProperty.Cardinality.single)
            throw VertexProperty.Exceptions.multiPropertiesNotSupported();
        if (keyValues.length > 0)
            throw VertexProperty.Exceptions.metaPropertiesNotSupported();
        try {
            bcVertex.setProperty(key, value, Visibility.EMPTY, authorizations);
            return new TinkerBcVertexProperty<>(this, key, value);
        } catch (final IllegalArgumentException iae) {
            throw Property.Exceptions.dataTypeOfPropertyValueNotSupported(value, iae);
        }
    }

    @Override
    public <V> VertexProperty<V> property(String key) {
        if(bcVertex.getProperty(key) != null) {
            return new TinkerBcVertexProperty<>(this, key, (V) bcVertex.getPropertyValue(key));
        } else {
            return VertexProperty.<V>empty();
        }
    }

    @Override
    public <V> Iterator<VertexProperty<V>> properties(String... propertyKeys) {
        return Stream.of(propertyKeys)
                .map(propKey -> (VertexProperty<V>) new TinkerBcVertexProperty(this, propKey, bcVertex.getPropertyValue(propKey)))
                .iterator();
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction, String... edgeLabels) {
        com.mware.ge.Vertex bcVertex = (com.mware.ge.Vertex ) geElement;
        Iterator<com.mware.ge.Vertex> vertices = null;

        switch (direction) {
            case IN:
                if(edgeLabels.length == 0)
                    vertices = bcVertex.getVertices(com.mware.ge.Direction.IN, authorizations).iterator();
                else
                    vertices = bcVertex.getVertices(com.mware.ge.Direction.IN, edgeLabels, authorizations).iterator();
                break;
            case OUT:
                if(edgeLabels.length == 0)
                    vertices = bcVertex.getVertices(com.mware.ge.Direction.OUT, authorizations).iterator();
                else
                    vertices = bcVertex.getVertices(com.mware.ge.Direction.OUT, edgeLabels, authorizations).iterator();
                break;
            case BOTH:
                if(edgeLabels.length == 0)
                    vertices = IteratorUtils.concat(
                        bcVertex.getVertices(com.mware.ge.Direction.OUT, authorizations).iterator(),
                        bcVertex.getVertices(com.mware.ge.Direction.IN, authorizations).iterator()
                    );
                else
                    vertices = IteratorUtils.concat(
                            bcVertex.getVertices(com.mware.ge.Direction.OUT, edgeLabels, authorizations).iterator(),
                            bcVertex.getVertices(com.mware.ge.Direction.IN, edgeLabels, authorizations).iterator()
                    );
                break;
        }

        return IteratorUtils.map(
                vertices,
                (v) -> new TinkerBcVertex(graph, v, authorizations)
        );
    }

    @Override
    public Iterator<Edge> edges(Direction direction, String... edgeLabels) {
        com.mware.ge.Vertex bcVertex = (com.mware.ge.Vertex ) geElement;
        Iterator<com.mware.ge.Edge> edges = null;

        switch (direction) {
            case IN:
                if(edgeLabels.length == 0)
                    edges = bcVertex.getEdges(com.mware.ge.Direction.IN, authorizations).iterator();
                else
                    edges = bcVertex.getEdges(com.mware.ge.Direction.IN, edgeLabels, authorizations).iterator();
                break;
            case OUT:
                if(edgeLabels.length == 0)
                    edges = bcVertex.getEdges(com.mware.ge.Direction.OUT, authorizations).iterator();
                else
                    edges = bcVertex.getEdges(com.mware.ge.Direction.OUT, edgeLabels, authorizations).iterator();
                break;
            case BOTH:
                if(edgeLabels.length == 0)
                    edges = IteratorUtils.concat(
                            bcVertex.getEdges(com.mware.ge.Direction.OUT, authorizations).iterator(),
                            bcVertex.getEdges(com.mware.ge.Direction.IN, authorizations).iterator()
                    );
                else
                    edges = IteratorUtils.concat(
                            bcVertex.getEdges(com.mware.ge.Direction.OUT, edgeLabels, authorizations).iterator(),
                            bcVertex.getEdges(com.mware.ge.Direction.IN, edgeLabels, authorizations).iterator()
                    );
        }
        return IteratorUtils.map(edges,
                (e) -> new TinkerBcEdge(graph, e, authorizations)
        );
    }

    @Override
    public String label() {
        return BcProperties.CONCEPT_TYPE.getPropertyValue(bcVertex);
    }

    @Override
    public String toString() {
        return StringFactory.vertexString(this);
    }
}
