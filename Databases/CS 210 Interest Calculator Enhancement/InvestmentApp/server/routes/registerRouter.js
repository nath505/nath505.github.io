import express from 'express'
import  { userValidationService } from '../services/validationService.js'
import User from '../models/userModel.js'
import bcrypt from 'bcrypt'

const router = express.Router();

// register for new user account
router.post('/api/register', userValidationService, async (req, res) => {
    let user = req.body;
    const numOfSaltRounds = 10;

    // search for an existing user with same username
    const existingUser = await User.findOne({ username: user.username });

    if (existingUser) {
        return res.status(400).json({
            error: "User already exists"
        });
    }

    // salt and hash the password before it gets stored in database
    const salt = bcrypt.genSaltSync(numOfSaltRounds);
    const hash = bcrypt.hashSync(user.password, salt);

    // create new user data
    user = new User({
        username: user.username,
        password: hash
    })

    // save new user in database
    user.save();

    res
        .status(204)
        .send('User added successfully')
})

export default router
