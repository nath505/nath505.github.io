package main;

import java.io.File;
import java.util.Scanner;
import java.util.Vector;

/**
* Binary search tree class and functions
*/
public class BinarySearchTree {
    private Node root;
    private Vector<Course> courseList = new Vector<Course>();

    // Implentation of the Singleton pattern to ensure that
    // only one instance is created at a time
    private static BinarySearchTree instance = null;

    // Private constructor method
    private BinarySearchTree() {
        this.root = null;
    }
    
    // Getter method for retrieving the current instance of the class,
    // or creating a new one if it doesn't exist
    public static BinarySearchTree getInstance() {
        if (instance == null) {
            instance = new BinarySearchTree();
        }

        return instance;
    }

    public void insertNode(Course course) {
        if (this.root == null) {
            this.root = new Node(course);
        }
        else {
            this.addNode(root, course);
        }
    }
    
    public void inOrder(Node node) {
        if (this.root == null) {
            System.out.println('\t' + "No courses found");
        }
        else if (node != null) {
            this.inOrder(node.left);
    
            // Output course information
            System.out.println('\t' + node.course.getCourseNumber() + ": " + node.course.getCourseName());
    
            this.inOrder(node.right);
        }
    }

    public void addNode(Node node, Course course) {
        // Add a new course to the BST
        // Sort each one by its number (or ID) by ascending order
        if (node.course.getCourseNumber().compareTo(course.getCourseNumber()) > 0) {
            if (node.left == null) {
                node.left = new Node(course);
            }
            else {
                addNode(node.left, course);
            }
        }
        else {
            if (node.right == null) {
                node.right = new Node(course);
            }
            else {
                addNode(node.right, course);
            }
        }
    }

    public void addCourse(Course course) {
        this.courseList.add(course);
    }

    public Vector<Course> getCourseList() {
        return this.courseList;
    }

    public void printCourseList() {
        // Call the inOrder method
        this.inOrder(root);

        System.out.println();
    }

    public void printCourse(String courseID) {
        Node currNode = this.root;
    
        // Transform courseID to uppercase
        courseID = courseID.toUpperCase();
        
        while (currNode != null) {
            if (currNode.course.getCourseNumber().compareTo(courseID) == 0) {
                // Course found
                // Output course information
                System.out.printf("%n" + currNode.course.getCourseNumber() + ": " + currNode.course.getCourseName() + "%n");
                // Output prerequisites of the course, if any
                for (int i = 0; i < currNode.course.getSize(); ++i) {
                    System.out.printf('\t' + "Prereq #" + (i + 1) + ": " + currNode.course.getPrereq(i) + "%n");
                }
    
                if (currNode.course.getSize() == 0) {
                    System.out.printf('\t' + "No prerequisites for this course%n");
                }

                System.out.println();

                return;
            }
            // Keep searching the BST for the course
            if (currNode.course.getCourseNumber().compareTo(courseID) > 0) {
                currNode = currNode.left;
            }
            else {
                currNode = currNode.right;
            }
        }
    
        // If course not found
        System.out.printf("%nError: Course not found%n%n");
    }

    public void loadCourses(String fileName) {
        File readFile = null;
        Scanner lineInput = null;

        // Open file to load courses
        readFile = new File(fileName);

        if (!readFile.exists()) {
            System.out.printf("%nError occurred: File not found%n%n");

            return;
        }

        // Declare vector for storing course IDs
        // Will be used for checking prereqs
        Vector<String>courseIDs;

        // Get course IDs from file
        Course obj = new Course();

        try {
            lineInput = new Scanner(readFile);
        } catch (Exception e) {
            System.out.printf("%nError occurred: " + e.getMessage() + "%n%n");

            if (lineInput == null) {
                return;
            }
        }

        courseIDs = obj.getCourseIDs(readFile);

        // Loop through the file contents
        while (lineInput.hasNext()) {
            // Create a new Course object
            Course course = new Course();

            // Add a new course
            // Declare variables to hold course data
            String courseLine;
            String courseID;
            String courseName = "";
            String prereq = "";
    
            // Get the course ID and name from line
            lineInput.useDelimiter(",");

            courseID = lineInput.next();
            lineInput.reset();
            courseLine = lineInput.nextLine();
            
            // Set the course ID
            course.setCourseNumber(courseID);
    
            // Get the course name and any prerequisites
            char currChar = courseLine.charAt(0);
            int i = 0;

            while (i < courseLine.length() - 1) {
                currChar = courseLine.charAt(++i);

                // Advance to prereqs section
                if (currChar == ',') {
                    // Get prereqs and store them
                    while (i < courseLine.length() - 1) {
                        currChar = courseLine.charAt(++i);

                        if (currChar == ',') {
                            if (course.addPrereq(courseIDs, prereq) == -1) {
                                System.out.printf("%nWarning: one or more prereqs are not courses in file%n");
    
                                break;
                            }

                            // Clear the prereq for next one
                            prereq = "";
                        }
                        else {
                            prereq += currChar;
                        }
                    }
                }
                else {
                    courseName += currChar;
                }
            }

            // Check for and set the course name
            if (courseName == "") {
                System.out.printf("%nWarning: course name is missing in one or more entries%n");
            }
            else {
                course.setCourseName(courseName);
            }

            // Insert the course into the courseList vector
            this.courseList.add(course);

            // Insert the course into the BST
            this.insertNode(course);
        }

        // Close the file
        lineInput.close();
        
        // Output number of courses loaded into program
        System.out.printf("%nCourses loaded: " + this.courseList.size() + "%n%n");
    }
}
