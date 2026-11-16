package com.campusconnect.campusconnect.service;

import com.campusconnect.campusconnect.model.Student;
import com.campusconnect.campusconnect.model.StudentConnection;
import com.campusconnect.campusconnect.repository.StudentRepository;
import com.campusconnect.campusconnect.repository.StudentConnectionRepository;
import com.campusconnect.campusconnect.dsa.StudentBST;
import com.campusconnect.campusconnect.dsa.StudentSorting;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentConnectionRepository connectionRepository;
    private final StudentBST studentBST;
    private final StudentSorting studentSorting;

    private final Map<String, Student> studentHashTable = new HashMap<>();

    public StudentService(
            StudentRepository studentRepository,
            StudentConnectionRepository connectionRepository,
            StudentBST studentBST,
            StudentSorting studentSorting) {

        this.studentRepository = studentRepository;
        this.connectionRepository = connectionRepository;
        this.studentBST = studentBST;
        this.studentSorting = studentSorting;

        List<Student> students = studentRepository.findAll();

        for (Student student : students) {
            studentHashTable.put(student.getStudentId(), student);
            studentBST.insert(student.getStudentId());
        }
    }

    // Register Student
    public Student registerStudent(Student student) {

        Student savedStudent = studentRepository.save(student);

        studentHashTable.put(savedStudent.getStudentId(), savedStudent);
        studentBST.insert(savedStudent.getStudentId());

        return savedStudent;
    }

    // Get All Students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Get Student By ID
    public Student getStudentById(String studentId) {

        Student student = studentRepository.findByStudentId(studentId);

        if (student == null) {
            throw new RuntimeException("Student not found");
        }

        return student;
    }

    // Search using BST
    public boolean searchStudentBST(String studentId) {
        return studentBST.search(studentId);
    }

    // BST Inorder Traversal
    public List<String> getBSTTraversal() {
        return studentBST.inOrderTraversal();
    }

    // GPA Leaderboard
    public List<Student> getGpaLeaderboard() {
        return studentSorting.mergeSort(getAllStudents());
    }

    // Add Connection
    public void addConnection(String studentOne, String studentTwo) {

        if (studentOne.equals(studentTwo)) {
            throw new RuntimeException(
                    "A student cannot connect to themselves.");
        }

        if (!studentHashTable.containsKey(studentOne)
                || !studentHashTable.containsKey(studentTwo)) {
            throw new RuntimeException("Student not found.");
        }

        String first = studentOne.compareTo(studentTwo) < 0
                ? studentOne
                : studentTwo;

        String second = studentOne.compareTo(studentTwo) < 0
                ? studentTwo
                : studentOne;

        boolean exists = connectionRepository.findAll()
                .stream()
                .anyMatch(c -> c.getStudentOneId().equals(first)
                        && c.getStudentTwoId().equals(second));

        if (!exists) {
            connectionRepository.save(
                    new StudentConnection(first, second));
        }
    }

    // Remove Connection
    @Transactional
    public void removeConnection(String studentOne, String studentTwo) {

        if (studentOne.equals(studentTwo)) {
            throw new RuntimeException(
                    "A student cannot disconnect from themselves.");
        }

        String first = studentOne.compareTo(studentTwo) < 0
                ? studentOne
                : studentTwo;

        String second = studentOne.compareTo(studentTwo) < 0
                ? studentTwo
                : studentOne;

        connectionRepository.deleteByStudentOneIdAndStudentTwoId(
                first, second);
    }

    // Get Connections
    public List<String> getConnections(String studentId) {

        List<String> connections = new ArrayList<>();

        for (StudentConnection connection : connectionRepository.findAll()) {

            if (connection.getStudentOneId().equals(studentId)) {
                connections.add(connection.getStudentTwoId());
            }

            if (connection.getStudentTwoId().equals(studentId)) {
                connections.add(connection.getStudentOneId());
            }
        }

        return connections;
    }

    // Get Complete Network
    public Map<String, List<String>> getNetwork() {

        Map<String, List<String>> network = new HashMap<>();

        for (Student student : getAllStudents()) {
            network.put(student.getStudentId(), new ArrayList<>());
        }

        for (StudentConnection connection : connectionRepository.findAll()) {

            String first = connection.getStudentOneId();
            String second = connection.getStudentTwoId();

            network.computeIfAbsent(first, k -> new ArrayList<>()).add(second);
            network.computeIfAbsent(second, k -> new ArrayList<>()).add(first);
        }

        return network;
    }

    // Delete Student
    @Transactional
    public void deleteStudent(String studentId) {

        Student student = getStudentById(studentId);

        for (StudentConnection connection : connectionRepository.findAll()) {

            if (connection.getStudentOneId().equals(studentId)
                    || connection.getStudentTwoId().equals(studentId)) {

                connectionRepository.delete(connection);
            }
        }

        studentRepository.delete(student);
        studentHashTable.remove(studentId);
    }

    public Student updateStudent(String studentId, Student updatedStudent) {

        Student existingStudent = studentRepository.findByStudentId(studentId);

        if (existingStudent == null) {
            throw new RuntimeException("Student not found");
        }

        existingStudent.setName(updatedStudent.getName());
        existingStudent.setEmail(updatedStudent.getEmail());
        existingStudent.setDepartment(updatedStudent.getDepartment());
        existingStudent.setYear(updatedStudent.getYear());
        existingStudent.setGpa(updatedStudent.getGpa());

        Student savedStudent = studentRepository.save(existingStudent);

        studentHashTable.put(studentId, savedStudent);

        return savedStudent;
    }

    // Search Student using Hash Table
    public Student searchStudentHashTable(String studentId) {
        return studentHashTable.get(studentId);
    }
}