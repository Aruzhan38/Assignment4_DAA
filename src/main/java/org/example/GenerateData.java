package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.*;

/** Generator for /data datasets in indexed JSON format: {n, edges:[{u,v,w}], ...}. */
public class GenerateData {

    // Сделали поля public, чтобы Jackson их видел без геттеров.
    public static class Edge {
        public int u, v, w;
        public Edge() {}                 // нужен пустой конструктор
        public Edge(int u, int v, int w){ this.u=u; this.v=v; this.w=w; }
    }

    public static class JsonGraph {
        public boolean directed = true;
        public int n;
        public int source = 0;           // можно задать из аргумента при желании
        public String weight_model = "edge";
        public List<Edge> edges = new ArrayList<>();
        public JsonGraph() {}
    }

    public static void main(String[] args) throws Exception {
        // args: <outfile.json> <n> <sparse|mixed|dense> <dag|cycle|multiscc> <seed>
        if (args.length < 5) {
            System.out.println("Usage: GenerateData <outfile.json> <n> <sparse|mixed|dense> <dag|cycle|multiscc> <seed>");
            return;
        }
        String out = args[0];
        int n = Integer.parseInt(args[1]);
        String dens = args[2].toLowerCase(Locale.ROOT);
        String typ  = args[3].toLowerCase(Locale.ROOT);
        long seed = Long.parseLong(args[4]);

        Random rnd = new Random(seed);

        // целевое число рёбер по плотности
        int targetE;
        if ("sparse".equals(dens)) targetE = (int)Math.round(1.5 * n);
        else if ("mixed".equals(dens)) targetE = (int)Math.round(2.2 * n);
        else targetE = (int)Math.round(3.5 * n);

        // случайный порядок для DAG-базы
        int[] order = new int[n];
        Integer[] perm = new Integer[n];
        for (int i=0;i<n;i++) perm[i]=i;
        Collections.shuffle(Arrays.asList(perm), rnd);
        for (int i=0;i<n;i++) order[perm[i]] = i; // order[node] -> позиция в топо-порядке

        boolean[][] has = new boolean[n][n];
        List<Edge> edges = new ArrayList<>();

        // 1) генерируем DAG рёбра (u->v только если order[u] < order[v])
        while (edges.size() < targetE) {
            int u = rnd.nextInt(n), v = rnd.nextInt(n);
            if (u==v) continue;
            if (order[u] < order[v] && !has[u][v]) {
                has[u][v] = true;
                edges.add(new Edge(u, v, 1 + rnd.nextInt(9)));
            }
        }

        // 2) добавляем обратные рёбра для циклов / нескольких SCC
        if ("cycle".equals(typ) || "multiscc".equals(typ)) {
            int extra = Math.max(2, n / 5);
            for (int k=0; k<extra; k++){
                int u = rnd.nextInt(n), v = rnd.nextInt(n);
                if (u==v) continue;
                // обратное ребро: создаёт цикл
                if (order[v] < order[u] && !has[u][v]) {
                    has[u][v] = true;
                    edges.add(new Edge(u, v, 1 + rnd.nextInt(9)));
                }
            }
            if ("multiscc".equals(typ)) {
                // ещё несколько обратных рёбер в разных зонах графа — чтобы было >=2 SCC
                for (int k=0; k<Math.max(2, n/10); k++){
                    int a = rnd.nextInt(Math.max(1, n/2));
                    int b = rnd.nextInt(n - Math.max(1, n/2)) + Math.max(1, n/2);
                    if (a==b) continue;
                    if (order[b] < order[a] && !has[a][b]) {
                        has[a][b] = true;
                        edges.add(new Edge(a, b, 1 + rnd.nextInt(9)));
                    }
                }
            }
        }

        // 3) собираем объект и сохраняем JSON
        JsonGraph jg = new JsonGraph();
        jg.n = n;
        jg.edges = edges;
        // jg.source = 0; // при желании можно прокинуть из аргументов
        ObjectMapper om = new ObjectMapper();
        om.writerWithDefaultPrettyPrinter().writeValue(new File(out), jg);

        System.out.println("Saved: " + out + " | V=" + n + " E=" + edges.size()
                + " | type=" + typ + " | density=" + dens);
    }
}
