package com.mware.bigconnect.gremlin;

import org.apache.tinkerpop.gremlin.AbstractGremlinSuite;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalEngine;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

public class BcTestSuite extends AbstractGremlinSuite {
   public BcTestSuite(final Class<?> klass, final RunnerBuilder builder) throws InitializationError {
       super(klass, builder,
               new Class<?>[]{
                       BcStructureCheck.class,
               }, new Class<?>[]{
                       BcStructureCheck.class,
               },
               false,
               TraversalEngine.Type.STANDARD);
   }
}
