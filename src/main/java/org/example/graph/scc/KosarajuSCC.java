package org.example.graph.scc;

import org.example.graph.common.Graph;
import org.example.graph.common.Metrics;
import java.util.*;

public class KosarajuSCC {
    /** Result holds the component id per node and the list of components. */
    public static class Result {
        /** component id for each node (0..components.size()-1) */
        public final int[] compId;
        /** list of components, each component is a list of node ids */
        public final List<List<Integer>> components;
        public Result(int[] compId, List<List<Integer>> components){
            this.compId = compId;
            this.components = components;
        }
    }
