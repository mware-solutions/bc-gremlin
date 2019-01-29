package com.mware.ge.gremlin;

import com.mware.ge.Authorizations;
import com.mware.ge.Graph;
import com.mware.ge.Visibility;
import com.mware.ge.gremlin.structure.TinkerBcGraph;
import org.apache.tinkerpop.gremlin.AbstractGremlinTest;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Before;
import org.junit.Test;

public class BcStructureCheck extends AbstractGremlinTest {
    public static final String VISIBILITY_A_STRING = "a";
    public static final String VISIBILITY_B_STRING = "b";
    public static final Visibility VISIBILITY_A = new Visibility(VISIBILITY_A_STRING);
    public static final Visibility VISIBILITY_A_AND_B = new Visibility("a&b");
    public static final Visibility VISIBILITY_B = new Visibility(VISIBILITY_B_STRING);
    public static final Authorizations AUTHORIZATIONS_A = new Authorizations("a");
    public static final Authorizations AUTHORIZATIONS_B = new Authorizations("b");
    public static final Authorizations AUTHORIZATIONS_ALL = new Authorizations("a", "b");
    public static final Authorizations AUTHORIZATIONS_EMPTY = new Authorizations();
    public static final Authorizations AUTHORIZATIONS_BAD = new Authorizations("bad");

    public BcStructureCheck() {
    }

    @Before
    public void before() {
        bcGraph().createAuthorizations("a", "b");
    }

    @Test
    public void test1() {
        Vertex v1 = graph.addVertex();
        Vertex v2 = graph.addVertex();
        Vertex v3 = graph.addVertex();
        v1.addEdge("e1", v2);
        v2.addEdge("e2", v3);
        v3.addEdge("e3", v1);

        // this is for testing only
    }

    public Graph bcGraph() {
        return ((TinkerBcGraph) this.graph).getBcGraph();

    }
}
