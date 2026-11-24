package org.stratum83.education;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Tiny fictional service-routing problem with nonnegative edge costs.
 * BFS minimizes hop count; Dijkstra minimizes total cost.
 * Values are invented for education, not real latency measurements.
 */
public class ServiceRouteExample {

    private static final String START = "API";
    private static final String GOAL = "DATABASE";

    private final Map<String, List<Edge>> graph = Map.of(
            "API", List.of(new Edge("DATABASE", 8), new Edge("GATEWAY", 2)),
            "GATEWAY", List.of(new Edge("DATABASE", 2)),
            "DATABASE", List.of());

    public record Route(List<String> nodes, int cost) {
        public Route {
            nodes = List.copyOf(nodes);
        }

        public int hops() {
            return nodes.size() - 1;
        }
    }

    private record Edge(String destination, int cost) {
    }

    private record Entry(String node, int cost) {
    }

    /** Fewest edges, ignoring edge costs when choosing the route. */
    public Route fewestHops() {
        ArrayDeque<String> queue = new ArrayDeque<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> seen = new HashSet<>();
        queue.add(START);
        seen.add(START);

        while (!queue.isEmpty()) {
            String current = queue.remove();
            if (current.equals(GOAL)) {
                return build(previous);
            }
            for (Edge next : graph.getOrDefault(current, List.of())) {
                if (seen.add(next.destination())) {
                    previous.put(next.destination(), current);
                    queue.add(next.destination());
                }
            }
        }
        throw new IllegalStateException("No service route exists");
    }

    /** Least total nonnegative edge cost, using Dijkstra's algorithm. */
    public Route lowestCost() {
        PriorityQueue<Entry> frontier = new PriorityQueue<>(
                Comparator.comparingInt(Entry::cost).thenComparing(Entry::node));
        Map<String, Integer> costs = new HashMap<>();
        Map<String, String> previous = new HashMap<>();

        frontier.add(new Entry(START, 0));
        costs.put(START, 0);
        while (!frontier.isEmpty()) {
            Entry current = frontier.poll();
            if (current.cost() != costs.getOrDefault(current.node(), Integer.MAX_VALUE)) {
                continue;
            }
            if (current.node().equals(GOAL)) {
                return build(previous);
            }
            for (Edge next : graph.getOrDefault(current.node(), List.of())) {
                int candidate = current.cost() + next.cost();
                if (candidate < costs.getOrDefault(next.destination(), Integer.MAX_VALUE)) {
                    costs.put(next.destination(), candidate);
                    previous.put(next.destination(), current.node());
                    frontier.add(new Entry(next.destination(), candidate));
                }
            }
        }
        throw new IllegalStateException("No service route exists");
    }

    private Route build(Map<String, String> previous) {
        List<String> route = new ArrayList<>();
        String current = GOAL;
        while (current != null) {
            route.add(current);
            current = previous.get(current);
        }
        Collections.reverse(route);

        int total = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            String from = route.get(i);
            String to = route.get(i + 1);
            int cost = graph.get(from).stream()
                    .filter(edge -> edge.destination().equals(to))
                    .findFirst().orElseThrow().cost();
            total += cost;
        }
        return new Route(route, total);
    }
}
