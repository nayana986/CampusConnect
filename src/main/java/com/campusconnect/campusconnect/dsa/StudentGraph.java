package com.campusconnect.campusconnect.dsa;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class StudentGraph {

    // Adjacency List
    private final Map<String, List<String>> adjacencyList = new HashMap<>();

    // Add a student to the graph
    public void addStudent(String studentId) {
        adjacencyList.putIfAbsent(studentId, new ArrayList<>());
    }

    // Add friendship between two students
    public void addConnection(String student1, String student2) {

        addStudent(student1);
        addStudent(student2);

        if (!adjacencyList.get(student1).contains(student2)) {
            adjacencyList.get(student1).add(student2);
        }

        if (!adjacencyList.get(student2).contains(student1)) {
            adjacencyList.get(student2).add(student1);
        }
    }

    // Get a student's connections
    public List<String> getConnections(String studentId) {
        return adjacencyList.getOrDefault(studentId, new ArrayList<>());
    }

    // Display the complete network
    public Map<String, List<String>> getNetwork() {
        return adjacencyList;
    }

    // BFS - Find shortest connection between students
    public List<String> findShortestPath(String start, String destination) {

        Queue<List<String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(Arrays.asList(start));
        visited.add(start);

        while (!queue.isEmpty()) {

            List<String> path = queue.poll();

            String current = path.get(path.size() - 1);

            if (current.equals(destination)) {
                return path;
            }

            for (String neighbor : getConnections(current)) {

                if (!visited.contains(neighbor)) {

                    visited.add(neighbor);

                    List<String> newPath = new ArrayList<>(path);
                    newPath.add(neighbor);

                    queue.add(newPath);
                }
            }
        }

        return new ArrayList<>();
    }
}
