package com.campusconnect.campusconnect.repository;

import com.campusconnect.campusconnect.model.StudentConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentConnectionRepository
        extends JpaRepository<StudentConnection, Long> {

    List<StudentConnection> findByStudentOneId(String studentOneId);

    List<StudentConnection> findByStudentTwoId(String studentTwoId);

    void deleteByStudentOneIdAndStudentTwoId(
            String studentOneId, String studentTwoId);
}