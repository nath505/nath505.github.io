import express from 'express'
import User from '../models/userModel.js'
import authService from '../services/authService.js'

const router = express.Router();

// get the current list of users
router.get('/api/users', (req, res) => {
    User.find({}).then(users => {
        res.json(users)
    })
})

// get username of logged in user
router.get('/api/users/:id', authService, (req, res) => {
    const user = res.locals.user;

    res.status(200).json({
        username: user.username
    })
})

export default router
