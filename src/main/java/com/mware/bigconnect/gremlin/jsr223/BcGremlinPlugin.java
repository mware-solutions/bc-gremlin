package com.mware.bigconnect.gremlin.jsr223;

import com.mware.bigconnect.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.jsr223.AbstractGremlinPlugin;
import org.apache.tinkerpop.gremlin.jsr223.DefaultImportCustomizer;
import org.apache.tinkerpop.gremlin.jsr223.ImportCustomizer;

public class BcGremlinPlugin extends AbstractGremlinPlugin {
    private static final ImportCustomizer imports = DefaultImportCustomizer.build()
            .addClassImports(TinkerBcEdge.class,
                    TinkerBcElement.class,
                    TinkerBcGraph.class,
                    TinkerBcVertex.class,
                    TinkerBcVertexProperty.class,
                    TinkerBcEdgeProperty.class,
                    TinkerBcMetadataProperty.class,
                    TinkerBcGraphVariables.class
            ).create();

    private static final BcGremlinPlugin instance = new BcGremlinPlugin();

    public BcGremlinPlugin() {
        super("bigconnect", imports);
    }

    public static BcGremlinPlugin instance() {
        return instance;
    }
}
