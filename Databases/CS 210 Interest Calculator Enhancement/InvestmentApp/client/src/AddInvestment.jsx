import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import TopBar from './TopBar'

/**
 * Component for adding a new investment to the user's account.
 */
function AddInvestment () {
    const [initialAmount, setInitialAmount] = useState("");
    const [monthlyDeposit, setMonthlyDeposit] = useState("");
    const [interestRate, setInterestRate] = useState("");
    const [numOfYears, setNumOfYears] = useState("");
    const [error, setError] = useState("");
    const [showError, setShowError] = useState(false);
    
    // enable navigation to other pages
    const navigate = useNavigate();
    
    /**
     * Display an error message to the user.
     */
    function ErrorMessage() {
        return (
            <div>
                { showError && <p style={{
                textAlign: 'center',
                fontWeight: 'bold',
                backgroundColor: 'darkslateblue',
                padding: "1em"
                }}>{error}</p> }
            </div>
        )
    }

    /**
     * Validate the form's fields before being sent to server.
     */
    function validateForm() {
        if (parseFloat(initialAmount) > 0
            && parseFloat(monthlyDeposit) > 0
            && parseFloat(interestRate) > 0
            && parseInt(numOfYears) > 0) {
                return true;
            } else {
                return false;
            }
    }

    /**
     * Submit the form to add new investment to the server.
     */
    function submitForm(event) {
        event.preventDefault();

        if (validateForm()) {
            fetch('http://localhost:5000/api/investments', {
                method: 'POST',
                credentials: 'include',
                headers: {
                'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                initialAmount: parseFloat(initialAmount),
                monthlyDeposit: parseFloat(monthlyDeposit),
                interestRate: parseFloat(interestRate),
                numOfYears: parseInt(numOfYears)
                }),
            })
            .then((res) => {
                if (res.status === 204) {
                    navigate('/home');
            }})

        } else {
            setError("Error: One or more values are missing or incorrect");
            setShowError(true);
        }
    }

    /**
     * Update values to appear in the form's fields.
     */
    function updateValues(event) {
        let value = event.target.value.toString();
        let name = event.target.name;

        if (name === "initialAmount") {
            setInitialAmount(value);
        } else if (name === "monthlyDeposit") {
            setMonthlyDeposit(value);
        } else if (name === "interestRate") {
            setInterestRate(value);
        } else if (name === "numOfYears") {
            value = parseInt(value) || "";
            setNumOfYears(value.toString());
        }
    }
    
    return (
        <div className='AddMenu'>
            <TopBar />
            <div className="AddInvestment">
                <ErrorMessage />
                <form onSubmit={submitForm}>
                    <label>Initial amount $ <input type="number"
                        name="initialAmount"
                        value={initialAmount}
                        step="0.01"
                        min="0.01"
                        onChange={updateValues} />
                    </label> <br /><br />
                    <label>Monthly deposit $ <input type="number"
                        name="monthlyDeposit"
                        value={monthlyDeposit}
                        step="0.01"
                        min="0.01"
                        onChange={updateValues} />
                    </label> <br /><br />
                    <label>Interest rate % <input type="number"
                        name="interestRate"
                        value={interestRate}
                        step="0.01"
                        min="0.01"
                        onChange={updateValues} />
                    </label> <br /><br />
                    <label>Number of years <input type="number"
                        name="numOfYears"
                        value={numOfYears}
                        step="1"
                        min="1"
                        onChange={updateValues} />
                    </label> <br /><br />
                    <button type="submit">Add</button>
                    <button type="button" onClick={() => navigate('/home')}>Cancel</button>
                </form>
            </div>
        </div>
    )
}

export default AddInvestment
