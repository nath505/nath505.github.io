import User from '../models/userModel.js'
import jwt from 'jsonwebtoken'

/**
 * Service for authenticating users.
 */
async function authService(req, res, next) {
    if (req.session.user === undefined) {
        return res.status(401).json({ error: 'User not logged in' })
    }
    
    // decode the token passed by request
    const decodedToken = jwt.verify(req.cookies.Authorization, process.env.JWT_SECRET);
    // find the user by id retrieved from decoded token
    const user = await User.findById(decodedToken.id);

    if (!user) {
        return res.status(400).json({ 
        error: "User not found"
        })
    }

    // pass along user data to next middleware
    res.locals.user = user;

    next();
}

export default authService
