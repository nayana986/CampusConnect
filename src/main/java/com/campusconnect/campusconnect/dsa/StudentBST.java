package com.campusconnect.campusconnect.dsa;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class StudentBST {

    class Node {
        String studentId;
        Node left, right;

        Node(String studentId) {
            this.studentId = studentId;
        }
    }

    private Node root;

    public void insert(String studentId) {
        root = insertNode(root, studentId);
    }

    private Node insertNode(Node root, String studentId) {
        if (root == null) {
            return new Node(studentId);
        }

        if (studentId.compareTo(root.studentId) < 0) {
            root.left = insertNode(root.left, studentId);
        } else if (studentId.compareTo(root.studentId) > 0) {
            root.right = insertNode(root.right, studentId);
        }

        return root;
    }

    public boolean search(String studentId) {
        Node current = root;

        while (current != null) {
            if (current.studentId.equals(studentId)) {
                return true;
            }

            if (studentId.compareTo(current.studentId) < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return false;
    }

    // Recursive Inorder Traversal
    public List<String> inOrderTraversal() {
        List<String> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    private void inOrder(Node node, List<String> result) {
        if (node == null) {
            return;
        }

        inOrder(node.left, result);
        result.add(node.studentId);
        inOrder(node.right, result);
    }
}