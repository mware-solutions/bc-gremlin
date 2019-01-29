package com.mware.ge.gremlin.structure;

import com.mware.ge.Authorizations;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;

public abstract class TinkerBcElement implements Element {
    protected final Authorizations authorizations;
    protected TinkerBcGraph graph;
    protected final com.mware.ge.Element geElement;

    public TinkerBcElement(TinkerBcGraph graph, com.mware.ge.Element geElement, Authorizations authorizations) {
        this.graph = graph;
        this.geElement = geElement;
        this.authorizations = authorizations;
    }

    @Override
    public Object id() {
        return this.geElement.getId();
    }

    @Override
    public Graph graph() {
        return graph;
    }

    @Override
    public void remove() {
        throw Graph.Exceptions.transactionsNotSupported();
    }
}
