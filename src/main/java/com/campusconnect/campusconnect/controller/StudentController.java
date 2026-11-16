package com.campusconnect.campusconnect.controller;

import com.campusconnect.campusconnect.model.Student;
import com.campusconnect.campusconnect.service.StudentService;
import com.campusconnect.campusconnect.dsa.StudentGraph;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PutMapping("/{studentId}")
    public Student updateStudent(
            @PathVariable String studentId,
            @RequestBody Student student) {

        return studentService.updateStudent(studentId, student);
    }

    // Register student
    @PostMapping
    public Student registerStudent(@RequestBody Student student) {
        return studentService.registerStudent(student);
    }

    // Get all students
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    // Search student
    @GetMapping("/{studentId}")
    public Student getStudentById(@PathVariable String studentId) {
        return studentService.getStudentById(studentId);
    }

    // Delete student
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
        return "Student deleted successfully!";
    }

    // Add connection
    @PostMapping("/{student1}/connections/{student2}")
    public String addConnection(
            @PathVariable String student1,
            @PathVariable String student2) {

        studentService.addConnection(student1, student2);
        return "Connection added successfully!";
    }

    // Remove connection
    @DeleteMapping("/{student1}/connections/{student2}")
    public String removeConnection(
            @PathVariable String student1,
            @PathVariable String student2) {

        studentService.removeConnection(student1, student2);
        return "Connection removed successfully!";
    }

    // Get connections
    @GetMapping("/{studentId}/connections")
    public List<String> getConnections(@PathVariable String studentId) {
        return studentService.getConnections(studentId);
    }

    // Complete campus network
    @GetMapping("/network")
    public Map<String, List<String>> getNetwork() {
        return studentService.getNetwork();
    }

    // BFS shortest path
    @GetMapping("/shortest-path")
    public List<String> shortestPath(
            @RequestParam String from,
            @RequestParam String to) {

        Map<String, List<String>> network = studentService.getNetwork();

        StudentGraph graph = new StudentGraph();

        for (Map.Entry<String, List<String>> entry : network.entrySet()) {
            for (String connectedStudent : entry.getValue()) {
                graph.addConnection(entry.getKey(), connectedStudent);
            }
        }

        return graph.findShortestPath(from, to);
    }

    // BST search
    @GetMapping("/bst/{studentId}")
    public String searchStudentBST(@PathVariable String studentId) {

        boolean found = studentService.searchStudentBST(studentId);

        return found
                ? "Student found in Binary Search Tree!"
                : "Student not found in Binary Search Tree!";
    }

    @GetMapping("/hash/{studentId}")
    public Student searchStudentHashTable(@PathVariable String studentId) {

        Student student = studentService.searchStudentHashTable(studentId);

        if (student == null) {
            throw new RuntimeException("Student not found in Hash Table!");
        }

        return student;
    }

    // BST traversal
    @GetMapping("/bst/traversal/all")
    public List<String> getBSTTraversal() {
        return studentService.getBSTTraversal();
    }

    // GPA Leaderboard
    @GetMapping("/leaderboard")
    public List<Student> getGpaLeaderboard() {
        return studentService.getGpaLeaderboard();
    }
}