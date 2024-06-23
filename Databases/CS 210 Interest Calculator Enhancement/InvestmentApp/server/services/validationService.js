/**
 * Service for validating user credentials for logins and registrations.
 */
export function userValidationService(req, res, next) {
    const body = req.body;
    
    // validate user credentials
    const user = body.username;
    const pass = body.password;

    // check if fields are empty
    if (!user || !pass) {
        return res.status(400).json({ 
            error: "Both fields are required"
        });
    }

    next();
}

/**
 * Service for validating investment data.
 */
export function dataValidationService(req, res, next) {
    const body = req.body;
    
    // validate investment data
    const amount = body.initialAmount;
    const deposit = body.monthlyDeposit;
    const rate = body.interestRate;
    const years = body.numOfYears;

    if (amount) {
        if (typeof amount !== 'number') {
            return res
                .status(400)
                .send("Amount must be a number")
        } else if (amount <= 0) {
            return res
                .status(400)
                .send("Amount must be greater than zero")
        }
    }
    
    if (typeof deposit !== 'number') {
        return res
            .status(400)
            .send("Monthly deposit must be a number")
    } else if (deposit <= 0) {
        return res
            .status(400)
            .send("Monthly deposit must be greater than zero")
    } else if (typeof rate !== 'number') {
        return res
            .status(400)
            .send("Interest rate must be a number")
    } else if (rate <= 0) {
        return res
            .status(400)
            .send("Interest rate must be greater than zero")
    } else if (typeof years !== 'number') {
        return res
            .status(400)
            .send("Number of years must be a number")
    } else if (years <= 0) {
        return res
            .status(400)
            .send("Number of years must be greater than zero")
    } else if (!(years % 1 === 0)) {
        return res
            .status(400)
            .send("Number of years must be an integer")
    }

    next();
}
