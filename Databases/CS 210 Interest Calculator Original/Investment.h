#ifndef INVESTMENT_H
#define INVESTMENT_H

class Investment {
public:
	Investment(); // Constructor for new object

	// Display the main menu
	void mainMenu();

	double calculateBalanceWithoutMonthlyDeposit(double t_initialInvestment, double t_interestRate, int t_numberOfYears);
	double balanceWithMonthlyDeposit(double t_initialInvestment, double t_monthlyDeposit, double t_interestRate, int t_numberOfYears);

	// Getters and setters
	double getInitialBalance() const;
	double getMonthlyDeposit() const;
	double getYearlyInterestRate() const;
	int getNumberOfYears() const;

	void setInitialBalance(double t_balance);
	void setMonthlyDeposit(double t_deposit);
	void setYearlyInterestRate(double t_rate);
	void setNumberOfYears(int t_years);

	// Display table and calculate balances
	void calcMonthlyWithoutDeposit();
	void calcMonthlyWithDeposit();

	// Output details for each year
	void printDetails(int t_year, double t_yearEndBalance, double t_interestEarned);

private:
	double m_initialBalance;
	double m_monthlyDeposit;
	double m_yearlyInterestRate;
	int m_numberOfYears;
};

#endif