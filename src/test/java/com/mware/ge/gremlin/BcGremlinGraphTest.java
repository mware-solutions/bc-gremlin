package com.mware.ge.gremlin;

import com.mware.ge.gremlin.structure.TinkerBcGraph;
import org.apache.tinkerpop.gremlin.GraphProviderClass;
import org.apache.tinkerpop.gremlin.process.ProcessComputerSuite;
import org.apache.tinkerpop.gremlin.process.ProcessStandardSuite;
import org.apache.tinkerpop.gremlin.structure.StructureStandardSuite;
import org.junit.runner.RunWith;

@RunWith(ProcessStandardSuite.class)
@GraphProviderClass(provider = BcGraphProvider.class, graph = TinkerBcGraph.class)
public class BcGremlinGraphTest {
}
