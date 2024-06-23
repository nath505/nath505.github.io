package test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.Vector;
import main.Course;
import main.BinarySearchTree;

public class ProjectTwo_test {
    @Test
    public void testAddCourses() {
        BinarySearchTree BST = BinarySearchTree.getInstance();
        Course course1 = new Course();
        Course course2 = new Course();
        Course course3 = new Course();

        BST.addCourse(course1);
        BST.addCourse(course2);
        BST.addCourse(course3);

        // Course list should be of size 3
        assertEquals(BST.getCourseList().size(), 3);
    }
    
    @Test
    public void testAddPrereq() {
        Vector<String> testVector = new Vector<String>();
        Course course = new Course();
        int result;
        
        result = course.addPrereq(testVector, "CS100");

        // Prereq doesn't exist, should return -1
        assertEquals(result, -1);

        testVector.add("CS100");

        result = course.addPrereq(testVector, "CS100");

        // Prereq exists in vector, should return 0
        assertEquals(result, 0);
    }
}