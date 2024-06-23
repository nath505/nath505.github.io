import { useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import TopBar from './TopBar'

/**
 * Component for updating an existing investment in the user's account.
 */
function UpdateInvestment () {
    // get params from url
    const [searchParams] = useSearchParams();

    const id = searchParams.get("id");
    const mdeposit = searchParams.get("mdeposit");
    const rate = searchParams.get("rate");
    const years = searchParams.get("years");

    const [monthlyDeposit, setMonthlyDeposit] = useState(parseFloat(mdeposit));
    const [interestRate, setInterestRate] = useState(parseFloat(rate));
    const [numOfYears, setNumOfYears] = useState(parseInt(years));
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
        if (monthlyDeposit > 0
            && interestRate > 0
            && numOfYears > 0) {
                return true;
            } else {
                return false;
            }
    }

    /**
     * Submit the form to update investment to the server.
     */
    function submitForm(event) {
        event.preventDefault();

        if (validateForm()) {
            fetch(`http://localhost:5000/api/investments/${id}`, {
                method: 'PUT',
                credentials: 'include',
                headers: {
                'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                monthlyDeposit: parseFloat(monthlyDeposit),
                interestRate: parseFloat(interestRate),
                numOfYears: parseInt(numOfYears)
                }),
            })
            .then((res) => {
                if (res.status === 204) {
                    navigate('/home');
                }
            })
        } else {
            setError("Error: One or more values are missing or incorrect");
            setShowError(true);
        }
    }

    /**
     * Update values to appear in the form's fields.
     */
    function updateValues(event) {
        let value = event.target.value;
        let name = event.target.name;

        if (name === "monthlyDeposit") {
            setMonthlyDeposit(value);
        } else if (name === "interestRate") {
            setInterestRate(value);
        } else if (name === "numOfYears") {
            setNumOfYears(value);
        }
    }
    
    return (
        <div className='UpdateMenu'>
            <TopBar />
            <div className="UpdateInvestment">
                <ErrorMessage />
                <form onSubmit={submitForm}>
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
                    <button type="submit">Update</button>
                    <button type="button" onClick={() => navigate('/home')}>Cancel</button>
                </form>
            </div>
        </div>
    )
}

export default UpdateInvestment
