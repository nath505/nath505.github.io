/*
 * Name: Nathaniel Rodriguez
 * 
 * Date: June 1, 2024
 * 
 * Description: This is the Java version of ProjectTwo.cpp from CS-300,
 * and every function within that file has been rewritten manually to work
 * with the differences in the new language
 */

package main;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ProjectTwo {
    public static void main(String[] args)
    {
        // Declare variable to hold choice
        int userChoice = 0;
        // Declare variable to store file name
        String fileName;
        // Declare variable to store course ID
        String courseID;

        // Get the instance of the BinarySearchTree class
        BinarySearchTree BST = BinarySearchTree.getInstance();
        
        Scanner userInput = new Scanner(System.in);

        // Loop until user exits
        while (userChoice != 9) {
            // Main menu
            System.out.println("====== MAIN MENU ======");
            System.out.println("1. Load Course Data");
            System.out.println("2. Print Course List");
            System.out.println("3. Print Course");
            System.out.println("9. Exit Program");
    
            // Prompt user for menu option
            System.out.printf("%nWhat would you like to do?%n");

            try {
                userChoice = userInput.nextInt();
                userInput.nextLine();
            } catch (InputMismatchException e) {
                System.out.printf("%nError: Please enter an integer%n%n");
                userInput.nextLine();

                continue;
            }

            switch (userChoice) {
            case 1:
                System.out.printf("Please enter the full path of the file:%n");
    
                fileName = userInput.nextLine();
    
                BST.loadCourses(fileName);
    
                break;
            case 2:
                // Print the entire course list
                System.out.printf("%nHere is a sample schedule:%n%n");
    
                BST.printCourseList();
                
                break;
            case 3:
                // Print a specific course
                System.out.printf("What course do you want to know about?%n");
    
                courseID = userInput.nextLine();
    
                BST.printCourse(courseID);
    
                break;
            case 9:
                // Exit program
                System.out.printf("%nThank you and goodbye!");
    
                break;
            default:
                // Option does not exist
                System.out.printf("%n" + userChoice + " is not a valid option.%n%n");
    
                break;
            }
        }

        userInput.close();
    }
}
