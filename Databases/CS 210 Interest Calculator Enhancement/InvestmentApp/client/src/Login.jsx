import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Link } from 'react-router-dom'

/**
 * Component for login functionality.
 */
function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
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
     * Validate the form's username and password fields.
     */
    function validateForm() {
        return username.length > 0 && password.length > 0;
    }

    /**
     * Submit the form to log in to user's account.
     */
    function submitForm(event) {
        event.preventDefault();

        if (validateForm()) {
            fetch('http://localhost:5000/api/login', {
                method: 'POST',
                credentials: 'include',
                headers: {
                'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                username: username,
                password: password
                }),
            })
            .then((res) => {
                if (res.status === 401) {
                    setError("Error: Username or password is incorrect");
                    setShowError(true);
                }
                else if (res.status == 200) {
                    navigate('/home');
                }
            })
        } else {
            setError("Error: One or both fields are empty");
            setShowError(true);
        }
    }

    /**
     * Update values to appear in the form's fields.
     */
    function updateValues(event) {
        let value = event.target.value;
        let name = event.target.name;

        if (name === "username") {
            setUsername(value);
        } else if (name === "password") {
            setPassword(value);
        }
    }

    return (
        <div className="Login">
            <Greeting />
            <form onSubmit={submitForm}>
                <ErrorMessage />
                <label>Username <input type="text"
                    name="username"
                    value={username}
                    onChange={updateValues} />
                </label> &nbsp;
                <label>Password <input type="password"
                    name="password"
                    value={password}
                    onChange={updateValues} />
                </label> &nbsp;
                <button type="submit">Login</button>
            </form>
            <Footer />
        </div>
    )
}

/**
 * Display a greeting on the login page.
 */
function Greeting() {
    return (
        <div className="Greeting">
            <h1>Welcome to SimplyInvest</h1>
            <h2>Please log in to continue</h2>
            <p>No account? <Link to='/register'>Sign up here.</Link></p>
        </div>
    )
}

/**
 * Display a footer on the bottom of the page.
 */
function Footer() {
    return (
        <footer>
        <p>Disclaimer: This web application is for educational purposes only and does not represent any actual entity,
            within the financial sector or otherwise.</p>
        </footer>
    )
}

export default Login
