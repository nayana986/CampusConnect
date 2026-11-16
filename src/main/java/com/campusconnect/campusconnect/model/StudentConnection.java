package com.campusconnect.campusconnect.model;

import jakarta.persistence.*;

@Entity
@Table(name = "student_connections", uniqueConstraints = @UniqueConstraint(columnNames = { "student_one_id",
        "student_two_id" }))
public class StudentConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_one_id", nullable = false)
    private String studentOneId;

    @Column(name = "student_two_id", nullable = false)
    private String studentTwoId;

    public StudentConnection() {
    }

    public StudentConnection(String studentOneId, String studentTwoId) {
        this.studentOneId = studentOneId;
        this.studentTwoId = studentTwoId;
    }

    public Long getId() {
        return id;
    }

    public String getStudentOneId() {
        return studentOneId;
    }

    public String getStudentTwoId() {
        return studentTwoId;
    }
}