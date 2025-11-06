package org.example.graph.common;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.*;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;


public class Graphs {

    public static Graph loadJson(File f) throws Exception {
        ObjectMapper om = new ObjectMapper();
        Map<String,Object> root = om.readValue(f, new TypeReference<>(){});
        Graph g = new Graph();

        // Проверяем какой формат
        if (root.containsKey("edges") && root.containsKey("n") && root.get("edges") instanceof List<?>) {
            // --- Новый формат (u, v, w)
            int n = ((Number) root.get("n")).intValue();
            for (int i = 0; i < n; i++) g.addNode(String.valueOf(i));
            @SuppressWarnings("unchecked")
            List<Map<String,Object>> edges = (List<Map<String,Object>>) root.get("edges");
            for (Map<String,Object> e : edges) {
                int u = ((Number)e.get("u")).intValue();
                int v = ((Number)e.get("v")).intValue();
                int w = ((Number)e.getOrDefault("w", 1)).intValue();
                g.addEdge(String.valueOf(u), String.valueOf(v), w);
            }
            return g;
        }

        // --- Старый формат (from, to)
        @SuppressWarnings("unchecked")
        List<String> nodes = (List<String>) root.getOrDefault("nodes", List.of());
        for (String name : nodes) g.addNode(name);
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> edges = (List<Map<String,Object>>) root.getOrDefault("edges", List.of());
        for (Map<String,Object> e : edges){
            String from = e.containsKey("from") ? (String) e.get("from") : String.valueOf(e.get("u"));
            String to   = e.containsKey("to") ? (String) e.get("to") : String.valueOf(e.get("v"));
            int w = ((Number) e.getOrDefault("weight", e.getOrDefault("w", 1))).intValue();
            g.addEdge(from, to, w);
        }
        return g;
    }
}
