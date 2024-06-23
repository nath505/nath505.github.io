/*
* CS 300 Project Two
* 
* This program takes a list of courses and allows the user
* to perform actions on them, such as printing a course schedule
* 
* Author: Nathaniel Rodriguez
* Date: April 18, 2023
*/

#include <string>
#include <iostream>
#include <fstream>
#include <vector>

using namespace std;

/**
 * Course class definition
 */
class Course
{
public:

    Course();
    string getCourseName();
    string getCourseNumber();
    string getPrereq(int pos);
    size_t getSize();
    vector<string> getCourseIDs(fstream& fileName);
    void setCourseName(string courseName);
    void setCourseNumber(string courseNumber);
    int addPrereq(vector<string> courseIDs, string prereq);

private:

    string courseName;
    string courseNumber;

    vector<string> prereqs;

};

/**
 * Course class which contains methods and variables
 * pertaining to each course
 */
Course::Course() {
    courseName = "";
    courseNumber = "";
}

// Get the course name
string Course::getCourseName() {
    return this->courseName;
}
// Get the course number of ID
string Course::getCourseNumber() {
    return this->courseNumber;
}
// Get the size of the prereqs vector
size_t Course::getSize() {
    return this->prereqs.size();
}
// Get the prereq located at the given position
string Course::getPrereq(int pos) {
    return this->prereqs.at(pos);
}
// Get the course IDs from the file
vector<string> Course::getCourseIDs(fstream& readFile) {
    vector<string> courseIDs;
    string courseID;
    string restOfLine;

    while (!readFile.eof()) {
        getline(readFile, courseID, ',');
        getline(readFile, restOfLine);

        courseIDs.push_back(courseID);
    }

    return courseIDs;
}
// Set the course name
void Course::setCourseName(string courseName) {
    this->courseName = courseName;
}
// Set the course number or ID
void Course::setCourseNumber(string courseNumber) {
    this->courseNumber = courseNumber;
}
// Add a prereq to the prereqs vector
int Course::addPrereq(vector<string> courseIDs, string prereq) {
    // Check to see if prereq exists as course
    for (int i = 0; i < courseIDs.size(); ++i) {
        if (courseIDs.at(i) == prereq) {
            // Prereq found
            this->prereqs.push_back(prereq);

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

/**
 * Node struct that defines and creates each node in the BST
 */
struct Node {
    Course course;
    Node* left;
    Node* right;

    Node() {
        left = nullptr;
        right = nullptr;
    }

    Node(Course course) : Node() {
        this->course = course;
    }
};

/**
 * Binary search tree class and functions
 */
class BinarySearchTree {
public:
    BinarySearchTree();
    void insertNode(Course course);
    void inOrder(Node* node);
    void loadCourses(string fileName);
    void printCourse(string courseID);
    void printCourseList();

private:
    Node* root;

    void addNode(Node* node, Course course);
    vector<Course> courseList;
};

BinarySearchTree::BinarySearchTree() {
    this->root = nullptr;
}

void BinarySearchTree::insertNode(Course course) {
    if (this->root == nullptr) {
        this->root = new Node(course);
    }
    else {
        this->addNode(root, course);
    }
}

void BinarySearchTree::inOrder(Node* node) {
    if (node != nullptr) {
        this->inOrder(node->left);

        // Output course information
        cout << node->course.getCourseNumber() << ": " << node->course.getCourseName() << endl;

        this->inOrder(node->right);
    }
}

void BinarySearchTree::loadCourses(string fileName) {
    // Declare variable for file
    fstream readFile;
    // Declare vector for storing course IDs
    // Will be used for checking prereqs
    vector<string>courseIDs;

    // Open file to load courses
    readFile.open(fileName, ios::in);

    if (!readFile) {
        cout << "File not found" << endl;

        return;
    }

    // Get course IDs from file
    Course obj;

    courseIDs = obj.getCourseIDs(readFile);

    // Reset the file position
    readFile.clear();
    readFile.seekg(0, ios::beg);

    // Loop through the file contents
    while (!readFile.eof()) {
        // Create a new Course object
        Course course;

        // Add a new course
        // Declare variables to hold course data
        string courseLine;
        string courseID;
        string courseName = "";
        string prereq;

        // Get the course ID and name from line
        getline(readFile, courseID, ',');
        getline(readFile, courseLine);
        // Set the course ID
        course.setCourseNumber(courseID);

        // Get the course name and any prerequisites
        char currChar = courseLine[0];
        int i = 0;

        while (i < courseLine.length()) {
            if (currChar == ',') {
                // Advance to prereqs section
                currChar = courseLine[++i];

                // Get prereqs and store them
                while (i < courseLine.length()) {
                    if (currChar == ',') {
                        if (course.addPrereq(courseIDs, prereq) == -1) {
                            cout << endl << "Error: one or more prereqs are not courses in file" << endl;

                            break;
                        }

                        // Clear the prereq for next one
                        prereq = "";
                    }
                    else {
                        prereq += currChar;
                    }

                    currChar = courseLine[++i];
                }

                if (course.addPrereq(courseIDs, prereq) == -1) {
                    cout << endl << "Error: one or more prereqs are not courses in file" << endl;

                    break;
                }

                // Check for and set the course name
                if (courseName == "") {
                    cout << endl << "Error: course name is missing in one or more entries" << endl;

                    break;
                }
                else {
                    course.setCourseName(courseName);
                }

                // Insert the course into the courseList vector
                this->courseList.push_back(course);

                // Insert the course into the BST
                this->insertNode(course);
            }
            else {
                courseName += currChar;

                currChar = courseLine[++i];
            }
        }
    }
    // Output number of courses loaded into program
    cout << endl << "Courses loaded: " << courseList.size() << endl;

    // Close the file
    readFile.close();
}

void BinarySearchTree::addNode(Node* node, Course course) {
    // Add a new course to the BST
    // Sort each one by its number (or ID)
    if (node->course.getCourseNumber().compare(course.getCourseNumber()) > 0) {
        if (node->left == nullptr) {
            node->left = new Node(course);
        }
        else {
            addNode(node->left, course);
        }
    }
    else {
        if (node->right == nullptr) {
            node->right = new Node(course);
        }
        else {
            addNode(node->right, course);
        }
    }
}

void BinarySearchTree::printCourse(string courseID) {
    Node* currNode = this->root;

    // Transform courseID to uppercase
    string temp;

    for (int i = 0; i < courseID.length(); ++i) {
        temp += toupper(courseID.at(i));
    }

    courseID = temp;

    while (currNode != nullptr) {
        if (currNode->course.getCourseNumber().compare(courseID) == 0) {
            // Course found
            // Output course information
            cout << endl << currNode->course.getCourseNumber() << ": " << currNode->course.getCourseName() << endl;
            // Output prerequisites of the course, if any
            for (int i = 0; i < currNode->course.getSize(); ++i) {
                cout << '\t' << "Prereq #" << i + 1 << ": " << currNode->course.getPrereq(i) << endl;
            }

            if (currNode->course.getSize() == 0) {
                cout << '\t' << "No prerequisites for this course" << endl;
            }

            return;
        }
        // Keep searching the BST for the course
        if (currNode->course.getCourseNumber().compare(courseID) > 0) {
            currNode = currNode->left;
        }
        else {
            currNode = currNode->right;
        }
    }

    // If course not found
    cout << endl << "Error: Course not found" << endl;
}

void BinarySearchTree::printCourseList() {
    // Call the inOrder method
    this->inOrder(root);
}

int main()
{
    // Declare variable to hold choice
    int userChoice = 0;
    // Declare variable to store file name
    string fileName;
    // Declare variable to store course ID
    string courseID;

    // Create new instance of BinarySearchTree class
    BinarySearchTree BST;

    // Loop until user exits
    while (userChoice != 9) {
        cout << endl;

        // Main menu
        cout << "====== MAIN MENU ======" << endl;
        cout << "1. Load Course Data" << endl;
        cout << "2. Print Course List" << endl;
        cout << "3. Print Course" << endl;
        cout << "9. Exit Program" << endl;

        // Prompt user for menu option
        cout << endl << "What would you like to do?" << endl;

        cin >> userChoice;

        switch (userChoice) {
        case 1:
            cout << endl << "Please enter the full path of the file:" << endl;

            cin >> fileName;

            BST.loadCourses(fileName);

            break;
        case 2:
            // Print the entire course list
            cout << endl << "Here is a sample schedule:" << endl;

            BST.printCourseList();

            break;
        case 3:
            // Print a specific course
            cout << endl << "What course do you want to know about?" << endl;

            cin >> courseID;

            BST.printCourse(courseID);

            break;
        case 9:
            // Exit program
            cout << endl << "Thank you and goodbye!" << endl;

            break;
        default:
            // Option does not exist
            cout << endl << userChoice << " is not a valid option." << endl;

            break;
        }
    }

    return 0;
}
