package main;

/**
* Node class that defines and creates each node in the BST
*/
public class Node {
    Course course;
    Node left;
    Node right;

    public Node() {
        left = null;
        right = null;
    }

    public Node(Course course) {
        this.course = course;
    }
}
