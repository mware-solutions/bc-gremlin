package com.mware.ge.gremlin.structure;

import com.mware.ge.Visibility;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class TinkerBcVertexProperty<V> implements VertexProperty<V> {
    protected final TinkerBcVertex vertex;
    protected final String key;
    protected final V value;

    public TinkerBcVertexProperty(final TinkerBcVertex vertex, final String key, final V value) {
        this.vertex = vertex;
        this.key = key;
        this.value = value;
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public V value() throws NoSuchElementException {
        return this.value;
    }

    @Override
    public boolean isPresent() {
        return null != this.value;
    }

    @Override
    public Vertex element() {
        return vertex;
    }

    @Override
    public void remove() {
        vertex.geElement.deleteProperties(key, vertex.authorizations);
    }

    @Override
    public Object id() {
        return (long) (this.key.hashCode() + this.value.hashCode() + this.vertex.id().hashCode());
    }

    @Override
    public <V> Property<V> property(String key, V value) {
        com.mware.ge.Property vertexProperty = vertex.geElement.getProperty(this.key);
        vertexProperty.getMetadata().add(key, value, Visibility.EMPTY);
        return new TinkerBcMetadataProperty<>(this, key, value);
    }

    @Override
    public <V> Property<V> property(String key) {
        com.mware.ge.Property vertexProperty = vertex.geElement.getProperty(this.key);
        return new TinkerBcMetadataProperty<V>(this, key, (V) vertexProperty.getMetadata().getValue(key));
    }

    @Override
    public <U> Iterator<Property<U>> properties(String... propertyKeys) {
        com.mware.ge.Property vertexProperty = vertex.geElement.getProperty(this.key);
        return Stream.of(propertyKeys)
                .map(propKey -> (Property<U>) new TinkerBcMetadataProperty<U>(this, propKey, (U) vertexProperty.getMetadata().getValue(propKey)))
                .iterator();
    }
}
