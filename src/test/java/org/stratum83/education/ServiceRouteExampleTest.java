package org.stratum83.education;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ServiceRouteExampleTest {

    @Test
    void bfsMinimizesHopsButNotWeightedCost() {
        ServiceRouteExample example = new ServiceRouteExample();
        ServiceRouteExample.Route bfs = example.fewestHops();
        assertEquals(List.of("API", "DATABASE"), bfs.nodes());
        assertEquals(1, bfs.hops());
        assertEquals(8, bfs.cost());
    }

    @Test
    void dijkstraFindsTheCheaperTwoHopRoute() {
        ServiceRouteExample example = new ServiceRouteExample();
        ServiceRouteExample.Route dijkstra = example.lowestCost();
        assertEquals(List.of("API", "GATEWAY", "DATABASE"), dijkstra.nodes());
        assertEquals(2, dijkstra.hops());
        assertEquals(4, dijkstra.cost());
        assertTrue(dijkstra.cost() < example.fewestHops().cost());
    }

    @Test
    void immutableRouteAndDeterministicResults() {
        ServiceRouteExample example = new ServiceRouteExample();
        assertEquals(example.lowestCost(), example.lowestCost());
        assertThrows(UnsupportedOperationException.class,
                () -> example.lowestCost().nodes().clear());
    }
}
