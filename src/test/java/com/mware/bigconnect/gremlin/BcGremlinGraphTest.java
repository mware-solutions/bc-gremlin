package com.mware.bigconnect.gremlin;

import com.mware.bigconnect.gremlin.structure.TinkerBcGraph;
import org.apache.tinkerpop.gremlin.GraphProviderClass;
import org.apache.tinkerpop.gremlin.process.ProcessStandardSuite;
import org.junit.runner.RunWith;

@RunWith(ProcessStandardSuite.class)
@GraphProviderClass(provider = BcGraphProvider.class, graph = TinkerBcGraph.class)
public class BcGremlinGraphTest {
}
