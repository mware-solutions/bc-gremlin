package com.mware.bigconnect.gremlin.structure;

import com.mware.ge.util.StreamUtils;
import org.apache.tinkerpop.gremlin.structure.Graph;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TinkerBcGraphVariables implements Graph.Variables  {
    private com.mware.ge.Graph bcGraph;

    public TinkerBcGraphVariables(com.mware.ge.Graph bcGraph) {
        this.bcGraph = bcGraph;
    }

    @Override
    public Set<String> keys() {
        return StreamUtils.stream(bcGraph.getMetadata())
                .map(m -> m.getKey())
                .collect(Collectors.toSet());
    }

    @Override
    public <R> Optional<R> get(String key) {
        return Optional.of((R) bcGraph.getMetadata(key));
    }

    @Override
    public void set(String key, Object value) {
        bcGraph.setMetadata(key, value);
    }

    @Override
    public void remove(String key) {
        bcGraph.removeMetadata(key);
    }

    public static class BcVariableFeatures implements Graph.Features.VariableFeatures {
        @Override
        public boolean supportsBooleanValues() {
            return true;
        }

        @Override
        public boolean supportsDoubleValues() {
            return true;
        }

        @Override
        public boolean supportsFloatValues() {
            return true;
        }

        @Override
        public boolean supportsIntegerValues() {
            return true;
        }

        @Override
        public boolean supportsLongValues() {
            return true;
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
        public boolean supportsByteValues() {
            return false;
        }

        @Override
        public boolean supportsBooleanArrayValues() {
            return true;
        }

        @Override
        public boolean supportsByteArrayValues() {
            return false;
        }

        @Override
        public boolean supportsDoubleArrayValues() {
            return true;
        }

        @Override
        public boolean supportsFloatArrayValues() {
            return true;
        }

        @Override
        public boolean supportsIntegerArrayValues() {
            return true;
        }

        @Override
        public boolean supportsLongArrayValues() {
            return true;
        }

        @Override
        public boolean supportsStringArrayValues() {
            return true;
        }

        @Override
        public boolean supportsSerializableValues() {
            return false;
        }

        @Override
        public boolean supportsStringValues() {
            return true;
        }

        @Override
        public boolean supportsUniformListValues() {
            return false;
        }
    }
}
