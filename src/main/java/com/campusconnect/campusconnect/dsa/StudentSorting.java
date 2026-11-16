package com.campusconnect.campusconnect.dsa;

import com.campusconnect.campusconnect.model.Student;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StudentSorting {

    // Merge Sort for GPA leaderboard
    public List<Student> mergeSort(List<Student> students) {

        if (students.size() <= 1) {
            return students;
        }

        int middle = students.size() / 2;

        List<Student> left = new ArrayList<>(
                students.subList(0, middle));

        List<Student> right = new ArrayList<>(
                students.subList(middle, students.size()));

        left = mergeSort(left);
        right = mergeSort(right);

        return merge(left, right);
    }

    private List<Student> merge(
            List<Student> left,
            List<Student> right) {

        List<Student> result = new ArrayList<>();

        int i = 0;
        int j = 0;

        while (i < left.size() && j < right.size()) {

            if (left.get(i).getGpa() >= right.get(j).getGpa()) {
                result.add(left.get(i));
                i++;
            } else {
                result.add(right.get(j));
                j++;
            }
        }

        while (i < left.size()) {
            result.add(left.get(i++));
        }

        while (j < right.size()) {
            result.add(right.get(j++));
        }

        return result;
    }
}