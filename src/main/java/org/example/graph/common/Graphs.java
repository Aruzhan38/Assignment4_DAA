package org.example.graph.common;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.*;
import java.util.*;


public class Graphs {
    public static Graph loadJson(File f) throws IOException {
        ObjectMapper om = new ObjectMapper();
        Map<String,Object> root = om.readValue(f, new TypeReference<>(){});
        Graph g = new Graph();

        @SuppressWarnings("unchecked") List<String> nodes = (List<String>) root.getOrDefault("nodes", List.of());
        for (String name : nodes) g.addNode(name);
        @SuppressWarnings("unchecked") List<Map<String,Object>> edges = (List<Map<String,Object>>) root.getOrDefault("edges", List.of());
        for (Map<String,Object> e : edges){
            String from = (String) e.get("from");
            String to = (String) e.get("to");
            int w = ((Number) e.getOrDefault("weight", 1)).intValue();
            g.addEdge(from,to,w);
        }
        return g;
    }
}