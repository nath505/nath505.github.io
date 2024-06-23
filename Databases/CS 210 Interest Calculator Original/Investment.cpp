#include <iostream>
#include <iomanip>
#include <string>

using namespace std;

#include "Investment.h"

Investment::Investment() {
    // Initialize values for new object
    m_initialBalance = 0.0;
    m_monthlyDeposit = 0.0;
    m_yearlyInterestRate = 0.0;
    m_numberOfYears = 0;
}

/**
 * Show the main menu to user, prompt for and validate input,
 * and call other functions as needed during program execution
 */
void Investment::mainMenu() {
	double balance; // Initial balance
	double depositAmt; // Monthly deposit amount
	double interestRate; // Yearly interest rate

	// Keeps track of user input and new values
	bool input = false;
	bool newValues = false;

	int years; // Number of years to invest

	cin.exceptions(std::ios::failbit); // Enable exception handling for input mismatch

	do {
		cout << "*******************************************" << endl;
		cout << "**************** DATA INPUT ***************" << endl;

		input = true;

		// If the user has chosen to enter new values, execute this if statement
		if (newValues) {
			cout << "Initial Investment Amount: $" << getInitialBalance() << endl;
		}

		// Skip the while loop if this is not the first run
		while (input && !newValues) {
			try {
				cout << "Initial Investment Amount: $";

				cin >> balance; // Get value for initial balance

				// Keep looping until the user enters a valid number
				if (balance < 0) {
					cout << "Please enter a positive value" << endl;
				}
				else {
					input = false;
				}
			}
			catch (...) {
				// User entered an invalid character or string
				cout << endl << "Please enter a valid choice" << endl << endl;

				cin.clear();

				string invalidInput; // String to get and ignore invalid input

				getline(cin, invalidInput);
			}
		}

		setInitialBalance(balance);

		input = true;

		while (input) {
			try {
				cout << "Monthly Deposit: $";

				cin >> depositAmt; // Get value for monthly deposit

				// Keep looping until the user enters a valid number
				if (depositAmt < 0) {
					cout << "Please enter a positive value" << endl;
				}
				else {
					input = false;
				}
			}
			catch (...) {
				// User entered an invalid character or string
				cout << endl << "Please enter a valid choice" << endl << endl;

				cin.clear();

				string invalidInput; // String to get and ignore invalid input

				getline(cin, invalidInput);
			}
		}

		setMonthlyDeposit(depositAmt);

		input = true;

		while (input) {
			try {
				cout << "Annual Interest: %";

				cin >> interestRate; // Get value for yearly interest

				// Keep looping until the user enters a valid number
				if (interestRate < 0) {
					cout << "Please enter a positive value" << endl;
				}
				else {
					input = false;
				}
			}
			catch (...) {
				// User entered an invalid character or string
				cout << endl << "Please enter a valid choice" << endl << endl;

				cin.clear();

				string invalidInput; // String to get and ignore invalid input

				getline(cin, invalidInput);
			}
		}

		setYearlyInterestRate(interestRate);

		input = true;

		while (input) {
			try {
				cout << "Number of years: ";

				cin >> years; // Get the number of years to invest

				// Keep looping until the user enters a valid number
				if (years < 0) {
					cout << "Please enter a positive value" << endl;
				}
				else {
					input = false;
				}
			}
			catch (...) {
				// User entered an invalid character or string
				cout << endl << "Please enter a valid choice" << endl << endl;

				cin.clear();

				string invalidInput; // String to get and ignore invalid input

				getline(cin, invalidInput);
			}
		}

		setNumberOfYears(years);

		system("PAUSE"); // Wait for user to press a key

		cout << endl; // New line

		// Call function for interest without deposit
		calcMonthlyWithoutDeposit();

		cout << endl; // New line

		// Call function for interest with deposit
		calcMonthlyWithDeposit();

		cout << endl; // New line

		char choice = 0;

		while (choice != 'Y' && choice != 'y') {
			// Ask user to input new values or exit
			cout << "Input new values? Y/N" << endl;

			cin >> choice;

			if (choice == 'N' || choice == 'n') {
				return; // Exit program if user enters 'N'
			}
		}

		newValues = true; // User will enter new values for investment

		system("CLS"); // Clears the console for next run

	} while (true); // Continue to loop until user exits
}

double Investment::getInitialBalance() const {
    return m_initialBalance; // get initial balance
}

double Investment::getMonthlyDeposit() const {
    return m_monthlyDeposit; // get monthly deposit
}

double Investment::getYearlyInterestRate() const {
    return m_yearlyInterestRate; // get yearly interest rate
}

int Investment::getNumberOfYears() const {
    return m_numberOfYears; // get number of years
}

void Investment::setInitialBalance(double t_balance) {
    m_initialBalance = t_balance; // set initial balance
}

void Investment::setMonthlyDeposit(double t_deposit) {
    m_monthlyDeposit = t_deposit; // set monthly deposit
}

void Investment::setYearlyInterestRate(double t_rate) {
    m_yearlyInterestRate = t_rate; // set yearly interest rate
}

void Investment::setNumberOfYears(int t_years) {
    m_numberOfYears = t_years; // set number of years
}

/**
 * Calculates and returns an end of year balance for given number of years
 *
 * @param initialInvestment dollar amount of initial investment
 * @param interestRate percentage of investment earned each year (annually), so a passed value of 3.5 is a rate of .035
 * @param numberOfYears number of years to calculate balance for
 *
 * @return the final calculated end of year balance
 */
double Investment::calculateBalanceWithoutMonthlyDeposit(double t_initialInvestment, double t_interestRate, int t_numberOfYears) {
    double balance = t_initialInvestment;
    
    // User interest rate converted into monthly
    double monthlyInterestRate = (t_interestRate / 100) / 12;

    // Iterate through each year, 1 to user-specified value
    for (int year = 1; year <= t_numberOfYears; ++year) {
        double interestEarnedThisYear = 0.0;

        // Iterate through each month, 1 to 12
        for (int month = 1; month <= 12; ++month) {
            double monthlyInterestEarned = 0.0;
            // Calculate interest for current month
            monthlyInterestEarned += balance * monthlyInterestRate;
            // Add monthly interest to interest earned in current year
            interestEarnedThisYear += monthlyInterestEarned;
            // Add monthly interest to current balance
            balance += monthlyInterestEarned;
        }
        // Call function to print row for current year
        printDetails(year, balance, interestEarnedThisYear);
    }

    return balance; // total balance
}

/**
 * Calculates and returns an end of year balance for a given number of years
 *
 * @param initialInvestment dollar amount of initial investment
 * @param monthlyDeposit dollar amount added into the investment each month
 * @param interestRate percentage of investment earned each year (annually), so a passed value of 3.5 is a rate of .035
 * @param numberOfYears number of years to calculate balance for
 *
 * @return the final calculated end of year balance
 */
double Investment::balanceWithMonthlyDeposit(double t_initialInvestment, double t_monthlyDeposit, double t_interestRate, int t_numberOfYears) {
    double balance = t_initialInvestment;

    // User interest rate converted into monthly
    double monthlyInterestRate = (t_interestRate / 100) / 12;

    // Iterate through each year, 1 to user-specified value
    for (int year = 1; year <= t_numberOfYears; ++year) {
        double interestEarnedThisYear = 0.0;

        // Iterate through each month, 1 to 12
        for (int month = 1; month <= 12; ++month) {
            double monthlyInterestEarned = 0.0;
			// Add monthly deposit
			balance += t_monthlyDeposit;
			// Calculate interest for current month
            monthlyInterestEarned += balance * monthlyInterestRate;
            // Add monthly interest to interest earned in current year
            interestEarnedThisYear += monthlyInterestEarned;
			// Add monthly interest to current balance
			balance += monthlyInterestEarned;
        }
        // Call function to print row for current year
        printDetails(year, balance, interestEarnedThisYear);
    }

    return balance; // total balance
}

/**
 * Displays the table and then calls a function to calculate interest on balance,
 * without monthly deposits
 */
void Investment::calcMonthlyWithoutDeposit() {
    cout << "     Balance and interest without additional monthly deposits" << endl;
    cout << "==================================================================" << endl;
    cout << "Year           Year End Balance           Year End Earned Interest" << endl;
    cout << "------------------------------------------------------------------" << endl;

    // Call the function that calculates balance without deposits
    calculateBalanceWithoutMonthlyDeposit(getInitialBalance(), getYearlyInterestRate(), getNumberOfYears());
}

/**
 * Displays the table and then calls a function to calculate interest on balance,
 * with monthly deposits
 */
void Investment::calcMonthlyWithDeposit() {
    cout << "        Balance and interest with additional monthly deposits" << endl;
    cout << "==================================================================" << endl;
    cout << "Year           Year End Balance           Year End Earned Interest" << endl;
    cout << "------------------------------------------------------------------" << endl;

    // Call the function that calculates balance with deposits
    balanceWithMonthlyDeposit(getInitialBalance(), getMonthlyDeposit(), getYearlyInterestRate(), getNumberOfYears());
}

/**
* Prints the calculated results of each years investment details in 3 separate fields
* @param year year number
* @param yearEndBalance the current dollar value of the investment
* @param interestEarned dollar amount of how much earned in that year
*/
void Investment::printDetails(int t_year, double t_yearEndBalance, double t_interestEarned) {
    cout << t_year << "              ";
    cout << fixed << setprecision(2);
    cout << '$' << t_yearEndBalance << "                      ";
    cout << '$' << t_interestEarned << endl;
}