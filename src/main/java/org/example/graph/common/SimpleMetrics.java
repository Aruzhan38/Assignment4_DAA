package org.example.graph.common;
import java.util.*;


public class SimpleMetrics implements Metrics {
    private final Map<String, Long> counters = new HashMap<>();
    private final Map<String, Long> t0 = new HashMap<>();
    private final Map<String, Long> dt = new HashMap<>();

    @Override public void inc(String key, long by){
        counters.merge(key, by, Long::sum);
    }
    @Override public long get(String key){
        return counters.getOrDefault(key,0L);
    }
    @Override public void timeStart(String key){
        t0.put(key, System.nanoTime());
    }
    @Override public void timeEnd(String key){
        dt.put(key, System.nanoTime() - t0.getOrDefault(key,0L));
    }
    @Override public long nanos(String key){
        return dt.getOrDefault(key,0L);
    }

    public String pretty(){
        StringBuilder sb = new StringBuilder();
        sb.append("\nMETRICS \n");
        counters.forEach((k,v)-> sb.append(k).append(": ").append(v).append('\n'));
        dt.forEach((k,v)-> sb.append(k).append(" time: ").append(v/1e6).append(" ms\n"));
        return sb.toString();
    }
}
