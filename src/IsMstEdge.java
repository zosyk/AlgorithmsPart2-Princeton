import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.In;


/**
 * Is an edge in a MST.
 * Given an edge-weighted graph G and an edge e,
 * design a linear-time algorithm to determine
 * whether e appears in some MST of G.
 *
 */
public class IsMstEdge {

    private boolean isMstEdge;
    private boolean[] marked;   // marked[v] = has vertex v been marked?


    public IsMstEdge(Edge edge, EdgeWeightedGraph graph) {
        marked = new boolean[graph.V()];

        int v = edge.either();

        dfs(graph, v, edge.weight());

        isMstEdge = !marked[v] || !marked[edge.other(v)];

    }

    public boolean isMstEdge() {
        return isMstEdge;
    }


    // depth-first search for an EdgeWeightedGraph
    private void dfs(EdgeWeightedGraph G, int v, double weight) {
        marked[v] = true;
        for (Edge e : G.adj(v)) {
            int w = e.other(v);
            if (!marked[w] && e.weight() < weight) {
                dfs(G, w, weight);
            }
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        EdgeWeightedGraph G = new EdgeWeightedGraph(in);

        IsMstEdge isMstEdge = new IsMstEdge(new Edge(0,2,0.26), G);

        System.out.println(isMstEdge.isMstEdge());
    }
}
