package main;

import java.io.File;
import java.util.Scanner;
import java.util.Vector;

/**
* Course class which contains methods and variables
* pertaining to each course
*/
public class Course {
    private String courseName;
    private String courseNumber;
    private Vector<String> prereqs = new Vector<String>();

    public Course() {
        courseName = "";
        courseNumber = "";
    }

    // Get the course name
    public String getCourseName() {
        return this.courseName;
    }

    // Set the course name
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    // Get the course number of ID
    public String getCourseNumber() {
        return this.courseNumber;
    }

    // Set the course number or ID
    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }
    
    // Get the size of the prereqs vector
    public int getSize() {
        return this.prereqs.size();
    }

    // Get the prereq located at the given position
    public String getPrereq(int pos) {
        return this.prereqs.get(pos);
    }

    // Get the course IDs from the file
    public Vector<String> getCourseIDs(File readFile) {
        Vector<String> courseIDs = new Vector<String>();
        Scanner lineInput = null;
        String courseID;

        try {
            lineInput = new Scanner(readFile);
        } catch (Exception e) {
            System.out.printf("%nError occurred: " + e.getMessage() + "%n%n");
        }

        while (lineInput.hasNext()) {
            lineInput.useDelimiter(",");
            courseID = lineInput.next();
            lineInput.nextLine();

            courseIDs.add(courseID);
        }

        lineInput.close();

        return courseIDs;
    }
    
    // Add a prereq to the prereqs vector
    public int addPrereq(Vector<String> courseIDs, String prereq) {
        // Check to see if prereq exists as course
        for (int i = 0; i < courseIDs.size(); ++i) {
            if (courseIDs.get(i).compareTo(prereq) == 0) {
                // Prereq found
                this.prereqs.add(prereq);
    
                return 0;
            }
            // Prereq list is empty
            else if (prereq == "") {
                return 0;
            }
        }
        // Prereq not found
        return -1;
    }
}
