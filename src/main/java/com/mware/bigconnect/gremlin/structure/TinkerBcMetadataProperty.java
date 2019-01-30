package com.mware.bigconnect.gremlin.structure;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Property;

import java.util.NoSuchElementException;

public class TinkerBcMetadataProperty<V> implements Property<V> {
    private TinkerBcVertexProperty property;
    private String key;
    private V value;

    public TinkerBcMetadataProperty(TinkerBcVertexProperty property, String key, V value) {
        this.property = property;
        this.key = key;
        this.value = value;
    }

    @Override
    public String key() {
        return this.key();
    }

    @Override
    public V value() throws NoSuchElementException {
        return value;
    }

    @Override
    public boolean isPresent() {
        return null != value;
    }

    @Override
    public Element element() {
        return property;
    }

    @Override
    public void remove() {
        com.mware.ge.Property vertexProperty = property.vertex.geElement.getProperty(property.key);
        vertexProperty.getMetadata().remove(key);
    }
}
