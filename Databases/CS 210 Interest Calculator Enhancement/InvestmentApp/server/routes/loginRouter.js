import express from 'express'
import User from '../models/userModel.js'
import { userValidationService } from '../services/validationService.js'
import jwt from 'jsonwebtoken'
import bcrypt from 'bcrypt'

const router = express.Router();

// log into user account
router.post('/api/login', userValidationService, async (req, res) => {
    const { username, password } = req.body;

    // find user by username
    const user = await User.findOne({ username: username });

    // check if user exists and compare hashes
    if (!(user && (bcrypt.compareSync(password, user.password)))) {
        return res.status(401).json({
            error: 'Invalid username or password'
        })
    }

    const userForToken = {
        id: user._id,
        username: user.username
    }

    // create a new token upon logging in
    const token = jwt.sign(userForToken, process.env.JWT_SECRET);

    // add username to current session
    req.session.user = user.username;

    res
        .status(200)
        .cookie('Authorization', token)
        .send('Logged in successfully')
})

export default router
