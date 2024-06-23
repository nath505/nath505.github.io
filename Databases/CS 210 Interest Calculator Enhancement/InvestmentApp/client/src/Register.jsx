import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

/**
 * Component for registering a new user account.
 */
function Register() {
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
     * Submit the form to register a new user account.
     */
    function submitForm(event) {
        event.preventDefault();

        if (validateForm()) {
            fetch('http://localhost:5000/api/register', {
                method: 'POST',
                headers: {
                'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                username: username,
                password: password
                }),
            })
            .then((res) => {
                if (res.status === 400) {
                    setError("Error: User already exists");
                    setShowError(true);
                }
                else if (res.status == 204) {
                    setError("Registration successful! You will be redirected after 5 seconds");
                    setShowError(true);
                    // navigate back to the login page after registration
                    setTimeout(() => {
                        navigate('/login');
                    }, 5000);
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
        <div className="Register">
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
                <button type="submit">Sign Up</button>
            </form>
            <Footer />
        </div>
    )
}

/**
 * Display a greeting on the registration page.
 */
function Greeting() {
    return (
        <div className="Greeting">
            <h1>Thanks for registering!</h1>
            <h2>Please fill out all fields</h2>
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

export default Register
