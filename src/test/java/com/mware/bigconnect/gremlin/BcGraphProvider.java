package com.mware.bigconnect.gremlin;

import com.mware.bigconnect.gremlin.structure.*;
import com.mware.ge.GraphConfiguration;
import com.mware.ge.id.UUIDIdGenerator;
import com.mware.ge.inmemory.InMemoryGraph;
import com.mware.ge.rocksdb.RocksDBGraph;
import com.mware.ge.search.DefaultSearchIndex;
import com.mware.ge.serializer.kryo.QuickKryoGeSerializer;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.AbstractGraphProvider;
import org.apache.tinkerpop.gremlin.LoadGraphWith;
import org.apache.tinkerpop.gremlin.structure.Graph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class BcGraphProvider extends AbstractGraphProvider {
    private static final Set<Class> IMPLEMENTATIONS = new HashSet<Class>() {{
        add(TinkerBcEdge.class);
        add(TinkerBcElement.class);
        add(TinkerBcGraph.class);
        add(TinkerBcVertex.class);
        add(TinkerBcVertexProperty.class);
        add(TinkerBcGraphVariables.class);
    }};

    protected Graph.Features features = null;

    @Override
    public Map<String, Object> getBaseConfiguration(String graphName, Class<?> test, String testMethodName, LoadGraphWith.GraphData loadGraphWith) {
        final String directory = makeTestDirectory(graphName, test, testMethodName);

        try {
            Files.createDirectories(Paths.get(directory));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new HashMap<String, Object>() {{
            put(Graph.GRAPH, TinkerBcGraph.class.getName());
            put("auths", "a,b");
            put("", InMemoryGraph.class.getName());
//            put("", RocksDBGraph.class.getName());
//            put("rocksdb.data.path", directory + File.separator + "data");
//            put("rocksdb.wal.path", directory + File.separator + "wal");
//            put("streamingPropertyValueDataFolder", directory + File.separator + "spv");
            put(GraphConfiguration.IDGENERATOR_PROP_PREFIX, UUIDIdGenerator.class.getName());
            put(GraphConfiguration.SEARCH_INDEX_PROP_PREFIX, DefaultSearchIndex.class.getName());
            put(GraphConfiguration.SERIALIZER, QuickKryoGeSerializer.class.getName());
        }};
    }

    @Override
    public Graph openTestGraph(final Configuration config) {
        final Graph graph = super.openTestGraph(config);

        // we can just use the initial set of features taken from the first graph generated from the provider because
        // neo4j feature won't ever change. don't think there is any danger of keeping this instance about even if
        // the original graph instance goes out of scope.
        if (null == features) {
            this.features = graph.features();
        }
        return graph;
    }

    @Override
    public Optional<Graph.Features> getStaticFeatures() {
        return Optional.ofNullable(features);
    }

    @Override
    public void clear(Graph graph, Configuration configuration) throws Exception {

    }

    @Override
    public Set<Class> getImplementations() {
        return IMPLEMENTATIONS;
    }
}
