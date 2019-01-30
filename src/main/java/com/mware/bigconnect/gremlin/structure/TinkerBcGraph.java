package com.mware.bigconnect.gremlin.structure;

import com.mware.bigconnect.gremlin.process.traversal.strategy.optimization.BcGraphStepStrategy;
import com.mware.core.model.properties.BcProperties;
import com.mware.ge.Authorizations;
import com.mware.ge.GraphFactory;
import com.mware.ge.VertexBuilder;
import com.mware.ge.Visibility;
import com.mware.ge.query.Query;
import com.mware.ge.query.TextPredicate;
import com.mware.ge.util.StreamUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.process.traversal.Compare;
import org.apache.tinkerpop.gremlin.process.traversal.Contains;
import org.apache.tinkerpop.gremlin.process.traversal.Text;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalStrategies;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.HasContainer;
import org.apache.tinkerpop.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Graph.OptIn(Graph.OptIn.SUITE_STRUCTURE_STANDARD)
@Graph.OptIn(Graph.OptIn.SUITE_STRUCTURE_INTEGRATE)
@Graph.OptIn(Graph.OptIn.SUITE_PROCESS_STANDARD)
@Graph.OptIn("com.mware.ge.gremlin.BcTestSuite")
public class TinkerBcGraph implements Graph {
    private final com.mware.ge.Graph bcGraph;
    private final Authorizations authorizations;
    protected Features features = new BcGraphFeatures();
    private final Configuration configuration;

    static {
        TraversalStrategies.GlobalCache.registerStrategies(
                TinkerBcGraph.class,
                TraversalStrategies.GlobalCache.getStrategies(Graph.class).clone()
                        .addStrategies(BcGraphStepStrategy.instance())
        );
    }

    public TinkerBcGraph(com.mware.ge.Graph bcGraph, Authorizations authorizations, Configuration configuration) {
        this.bcGraph = bcGraph;
        this.authorizations = authorizations;
        this.configuration = configuration;
    }

    public static TinkerBcGraph open(final Configuration configuration) {
        if (null == configuration) throw Graph.Exceptions.argumentCanNotBeNull("configuration");
        Map<String, String> bcConfig = new HashMap<>();
        StreamUtils.stream(configuration.getKeys())
                .filter(k -> configuration.getProperty(k) instanceof String)
                .forEach(k -> { bcConfig.put(k, configuration.getString(k)); });
        com.mware.ge.Graph bcGraph = new GraphFactory().createGraph(bcConfig);
        String strAuths = configuration.getString("auths", "Administrator");
        Authorizations auths = new Authorizations(strAuths.split(","));
        return new TinkerBcGraph(bcGraph, auths, configuration);
    }

    @Override
    public Vertex addVertex(Object... keyValues) {
        ElementHelper.legalPropertyKeyValueArray(keyValues);
        VertexBuilder vb;
        if (ElementHelper.getIdValue(keyValues).isPresent()) {
            String vertexId = ElementHelper.getIdValue(keyValues).get().toString();
            vb = getBcGraph().prepareVertex(vertexId, Visibility.EMPTY);
        } else {
            vb = getBcGraph().prepareVertex(Visibility.EMPTY);
        }

        Optional<String> label = ElementHelper.getLabelValue(keyValues);
        if(label.isPresent())
            BcProperties.CONCEPT_TYPE.setProperty(vb, label.get(), Visibility.EMPTY);

        TinkerBcVertex vertex = new TinkerBcVertex(this, vb.save(authorizations), authorizations);
        ElementHelper.attachProperties(vertex, keyValues);
        return vertex;
    }

    @Override
    public <C extends GraphComputer> C compute(Class<C> graphComputerClass) throws IllegalArgumentException {
        throw Graph.Exceptions.graphComputerNotSupported();
    }

    @Override
    public GraphComputer compute() {
        throw Graph.Exceptions.graphComputerNotSupported();
    }

    @Override
    public Iterator<Vertex> vertices(Object... vertexIds) {
        if (0 == vertexIds.length) {
            return StreamUtils.stream(getBcGraph().getVertices(authorizations))
                    .map(v -> (Vertex) new TinkerBcVertex(this, v, authorizations))
                    .iterator();
        } else {
            Set<String> strIds = Stream.of(vertexIds)
                    .map(id -> {
                        if (id instanceof Number)
                            return id.toString();
                        else if (id instanceof String)
                            return (String) id;
                        else if (id instanceof Vertex) {
                            return ((Vertex)id).id().toString();
                        } else
                            throw new IllegalArgumentException("Unknown vertex id type: " + id);
                    })
                    .collect(Collectors.toSet());

            return StreamUtils.stream(getBcGraph().getVertices(strIds, authorizations))
                    .map(v -> (Vertex) new TinkerBcVertex(this, v, authorizations))
                    .iterator();
        }
    }

    @Override
    public Iterator<Edge> edges(Object... edgeIds) {
        if (0 == edgeIds.length) {
            return StreamUtils.stream(getBcGraph().getEdges(authorizations))
                    .map(e -> (Edge) new TinkerBcEdge(this, e, authorizations))
                    .iterator();
        } else {
            Set<String> strIds = Stream.of(edgeIds)
                    .map(id -> {
                        if (id instanceof Number)
                            return id.toString();
                        else if (id instanceof String)
                            return (String) id;
                        else if (id instanceof Edge) {
                            return ((Edge)id).id().toString();
                        } else
                            throw new IllegalArgumentException("Unknown edge id type: " + id);
                    })
                    .collect(Collectors.toSet());

            return StreamUtils.stream(getBcGraph().getEdges(strIds, authorizations))
                    .map(e -> (Edge) new TinkerBcEdge(this, e, authorizations))
                    .iterator();
        }
    }

    @Override
    public Transaction tx() {
        throw Graph.Exceptions.transactionsNotSupported();
    }

    @Override
    public void close() throws Exception {
        getBcGraph().shutdown();
    }

    @Override
    public Variables variables() {
        return new TinkerBcGraphVariables(bcGraph);
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

    @Override
    public Vertex addVertex(String label) {
        throw Graph.Exceptions.transactionsNotSupported();
    }

    @Override
    public Features features() {
        return features;
    }

    public com.mware.ge.Graph getBcGraph() {
        return bcGraph;
    }

    public Iterator<Vertex> lookupVertices(List<HasContainer> hasContainers, Object... ids) {
        // ids are present, filter on them first
        if (ids.length > 0) {
            return IteratorUtils.filter(
                    vertices(ids),
                    (vertex) -> HasContainer.testAll(vertex, hasContainers)
            );
        }

        Query query = bcGraph.query(authorizations);

        // apply filters
        for(HasContainer container : hasContainers) {
            Object rval = container.getPredicate().getValue();
            String propName = container.getKey();

            if (container.getKey().equals(T.label.getAccessor())) {
                propName = BcProperties.CONCEPT_TYPE.getPropertyName();
            }

            if(container.getBiPredicate() == Compare.eq) {
                query = query.has(propName, com.mware.ge.query.Compare.EQUAL, rval);
            } else if (container.getBiPredicate() == Compare.neq) {
                query = query.has(propName, com.mware.ge.query.Compare.NOT_EQUAL, rval);
            } else if (container.getBiPredicate() == Compare.gt) {
                query = query.has(propName, com.mware.ge.query.Compare.GREATER_THAN, rval);
            } else if (container.getBiPredicate() == Compare.gte) {
                query = query.has(propName, com.mware.ge.query.Compare.GREATER_THAN_EQUAL, rval);
            } else if (container.getBiPredicate() == Compare.lt) {
                query = query.has(propName, com.mware.ge.query.Compare.LESS_THAN, rval);
            } else if (container.getBiPredicate() == Compare.lte) {
                query = query.has(propName, com.mware.ge.query.Compare.LESS_THAN_EQUAL, rval);
            } else if (container.getBiPredicate() == Contains.within) {
                query = query.has(propName, com.mware.ge.query.Contains.IN, rval);
            } else if (container.getBiPredicate() == Contains.without) {
                query = query.has(propName, com.mware.ge.query.Contains.NOT_IN, rval);
            } else if (container.getBiPredicate() == Text.containing) {
                query = query.has(propName, TextPredicate.CONTAINS, rval);
            } else if (container.getBiPredicate() == Text.notContaining) {
                query = query.has(propName, TextPredicate.DOES_NOT_CONTAIN, rval);
            }
        }

        return StreamUtils.stream(query.vertices())
                .map(v -> (Vertex) new TinkerBcVertex(this, v, authorizations))
                .iterator();
    }

    public class BcGraphFeatures implements Features {
        protected GraphFeatures graphFeatures = new BcGraphGraphFeatures();
        protected VertexFeatures vertexFeatures = new BcVertexFeatures();
        protected EdgeFeatures edgeFeatures = new BcEdgeFeatures();

        @Override
        public GraphFeatures graph() {
            return graphFeatures;
        }

        @Override
        public VertexFeatures vertex() {
            return vertexFeatures;
        }

        @Override
        public EdgeFeatures edge() {
            return edgeFeatures;
        }

        @Override
        public String toString() {
            return StringFactory.featureString(this);
        }

        public class BcGraphGraphFeatures implements GraphFeatures {
            private VariableFeatures variableFeatures = new TinkerBcGraphVariables.BcVariableFeatures();

            BcGraphGraphFeatures() {
            }

            @Override
            public boolean supportsConcurrentAccess() {
                // true for hadoop, false for rocksdb
                return false;
            }

            @Override
            public boolean supportsComputer() {
                return false;
            }

            @Override
            public VariableFeatures variables() {
                return variableFeatures;
            }

            @Override
            public boolean supportsThreadedTransactions() {
                return false;
            }

            @Override
            public boolean supportsTransactions() {
                return false;
            }
        }

        public class BcElementFeatures implements ElementFeatures {
            BcElementFeatures() {
            }

            @Override
            public boolean supportsNumericIds() {
                return false;
            }

            @Override
            public boolean supportsCustomIds() {
                return false;
            }

            @Override
            public boolean supportsUuidIds() {
                return false;
            }
        }

        public class BcVertexFeatures extends BcElementFeatures implements VertexFeatures {
            private final VertexPropertyFeatures vertexPropertyFeatures = new BcVertexPropertyFeatures();

            protected BcVertexFeatures() {
            }

            @Override
            public VertexPropertyFeatures properties() {
                return vertexPropertyFeatures;
            }

            @Override
            public boolean supportsMultiProperties() {
                return false;
            }

            @Override
            public boolean supportsMetaProperties() {
                return false;
            }

            @Override
            public boolean supportsRemoveVertices() {
                return false;
            }

            @Override
            public VertexProperty.Cardinality getCardinality(String key) {
                return VertexProperty.Cardinality.single;
            }
        }

        public class BcEdgeFeatures extends BcElementFeatures implements EdgeFeatures {
            private final EdgePropertyFeatures edgePropertyFeatures = new BcEdgePropertyFeatures();

            public BcEdgeFeatures() {
            }

            @Override
            public EdgePropertyFeatures properties() {
                return edgePropertyFeatures;
            }
        }

        public class BcVertexPropertyFeatures implements VertexPropertyFeatures {
            BcVertexPropertyFeatures() {
            }

            @Override
            public boolean supportsMapValues() {
                return false;
            }

            @Override
            public boolean supportsMixedListValues() {
                return false;
            }

            @Override
            public boolean supportsUserSuppliedIds() {
                return false;
            }

            @Override
            public boolean supportsCustomIds() {
                return false;
            }

            @Override
            public boolean supportsAnyIds() {
                return false;
            }

            @Override
            public boolean supportsSerializableValues() {
                return false;
            }
        }

        public class BcEdgePropertyFeatures implements EdgePropertyFeatures {
            BcEdgePropertyFeatures() {
            }

            @Override
            public boolean supportsMapValues() {
                return false;
            }

            @Override
            public boolean supportsMixedListValues() {
                return false;
            }

            @Override
            public boolean supportsUniformListValues() {
                return false;
            }
        }
    }
}
