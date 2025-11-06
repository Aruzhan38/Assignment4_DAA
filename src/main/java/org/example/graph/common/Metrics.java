package org.example.graph.common;

public interface Metrics {
    void inc(String key, long by);
    long get(String key);
    void timeStart(String key);
    void timeEnd(String key);
    long nanos(String key);
}
