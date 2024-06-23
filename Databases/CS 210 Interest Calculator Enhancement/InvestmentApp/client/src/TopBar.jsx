import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Link } from 'react-router-dom'

/**
 * Component for displaying the top bar.
 */
function TopBar() {
    const [username, setUsername] = useState("");

    // enable navigation to other pages
    const navigate = useNavigate();

    /**
     * Fetch the logout route and redirect to login
     * page if successful.
     */
    function logOut() {
        fetch('http://localhost:5000/api/logout', {
        method: 'POST',
        credentials: 'include',
        headers: {
        'Content-Type': 'application/json',
        }})
        .then((res) => {
            if (res.status === 200) {
                navigate('/login');
            }
        })
    }

    // get username to display in top bar
    useEffect(() => {
        fetch('http://localhost:5000/api/users/:id', {
            method: 'GET',
            credentials: 'include',
            headers: {
            'Content-Type': 'application/json',
            }})
            .then((res) => {
                if (res.status === 401) {
                    navigate('/login');
                    return Promise.reject('Unauthorized');
                } else if (res.status === 200) {
                    return res.json();
                }
            })
            .then((data) => {
                setUsername(data.username);
            })
        }, [])

    return (
        <div className="TopBar">
            <h1>Your Investments</h1>
            <h2><Link to='#' onClick={logOut}>Log Out</Link></h2>
            <p>Logged in as <b>{username}</b></p>
            <p><Link to="/add-investment">Add new investment</Link></p>
        </div>
    )
}

export default TopBar
