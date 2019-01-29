package com.mware.ge.gremlin.structure;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;

import java.util.NoSuchElementException;

public class TinkerBcEdgeProperty<V> implements Property<V> {
    protected final Element element;
    protected final String key;
    protected V value;

    public TinkerBcEdgeProperty(final Element element, final String key, final V value) {
        this.element = element;
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
    public Element element() {
        return this.element;
    }

    @Override
    public void remove() {
        if(element instanceof TinkerBcEdge) {
            TinkerBcEdge edge = (TinkerBcEdge) element;
            edge.bcEdge.deleteProperties(key, edge.authorizations);
        }
    }

    @Override
    public int hashCode() {
        return ElementHelper.hashCode(this);
    }

    @Override
    public String toString() {
        return StringFactory.propertyString(this);
    }
}
