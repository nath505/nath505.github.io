import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import TopBar from './TopBar'

/**
 * Component for displaying the home page.
 */
function Home() {
    return (
        <div className='MainMenu'>
            <TopBar />
            <Investments />
        </div>
    )
}

/**
 * Component for managing the user's investments.
 */
function Investments() {
    const [listItems, setListItems] = useState([]);
    const [showEmptyMessage, setShowEmptyMessage] = useState(true);

    // enable navigation to other pages
    const navigate = useNavigate();

    /**
     * Display a message to the user if list is empty.
     */
    function EmptyMessage() {
        return (
            <div>
                { showEmptyMessage && <p style={{textAlign: 'center'}}><i>Nothing to display&#8230;</i></p> }
            </div>
        )
    }

    /**
     * Redirect to page to update an investment in the user's account.
     */
    function updateInvestment(event, item) {
        navigate(`/update-investment?id=${event.target.id}&mdeposit=${item.monthlyDeposit}&rate=${item.interestRate}&years=${item.numOfYears}`);
    }

    /**
     * Remove an investment from the user's account.
     */
    function removeInvestment(event) {
        fetch(`http://localhost:5000/api/investments/${event.target.id}`, {
            method: 'DELETE',
            credentials: 'include'
        })
        .then((res) => {
            if (res.status === 204) {
                // re-populate array and update page
                getInvestments();
            }
        })
    }

    /**
     * Calculates the total balance of an investment.
     * 
     * @param {*} item A collection of the investment's data.
     * @returns {number} The total balance of the investment after calculations.
     */
    function calculateTotal(item) {
        let balance = item.initialAmount;
        const monthlyInterestRate = (item.interestRate / 100) / 12;

        for (let year = 1; year <= item.numOfYears; ++year) {
            let interestEarnedThisYear = 0.0;

            for (let month = 1; month <= 12; ++month) {
                let monthlyInterestEarned = 0.0;

                balance += item.monthlyDeposit;
                monthlyInterestEarned += balance * monthlyInterestRate;
                interestEarnedThisYear += monthlyInterestEarned;

                balance += monthlyInterestEarned;
            }
        }

        return balance.toFixed(2);
    }

    /**
     * Get and display the user's investments from the server.
     */
    function getInvestments() {
        fetch('http://localhost:5000/api/investments', {
            method: 'GET',
            credentials: 'include',
            headers: {
            'Content-Type': 'application/json',
            }
        })
        .then((res) => {
            if (res.status === 401) {
                return Promise.reject('Unauthorized');
            } else if (res.status === 200) {
                return res.json();
            }
        })
        .then((data) => {
            setListItems(data.map(function (item, index) {
                if (index === 0) {
                    setShowEmptyMessage(false);
                }

                return (
                    <li key={item._id}>
                        <h3 style={{
                            borderBottom: '1px lightgray solid',
                            paddingBottom: '1em'
                        }}>Investment {index + 1}</h3>
                        <p>Initial Amount<br /><br />${item.initialAmount}</p>
                        <p>Monthly Deposit<br /><br />${item.monthlyDeposit}</p>
                        <p>Interest Rate<br />&#40;Compounded Monthly&#41;<br /><br />{item.interestRate}%</p>
                        <p>Number of Years<br /><br />{item.numOfYears}</p>
                        <h4>Total: ${calculateTotal(item)}</h4>
                        <button type="button" id={item._id} onClick={(event) => {
                            updateInvestment(event, item);
                        }}>Update</button>
                        <button type="button" id={item._id} onClick={removeInvestment}>Remove</button>
                    </li>
                )
            }));
        })
    }

    // fetch the investment data from server
    useEffect(() => {
        getInvestments();
    }, [])

    return (
        <>
            <EmptyMessage />
            <ul>
                {listItems}
            </ul>
        </>
    )
}

export default Home
