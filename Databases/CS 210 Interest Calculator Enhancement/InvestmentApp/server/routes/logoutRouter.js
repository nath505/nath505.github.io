import express from 'express'

const router = express.Router();

// log out of user account
router.post('/api/logout', (req, res) => {
    // delete current user session upon logging out
    req.session.destroy();

    res
        .status(200)
        .clearCookie('Authorization') // delete cookie used for login
        .send('Cookie cleared')
})

export default router
