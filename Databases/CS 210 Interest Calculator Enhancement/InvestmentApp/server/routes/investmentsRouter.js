import express from 'express'
import { dataValidationService } from '../services/validationService.js'
import authService from '../services/authService.js'
import Data from '../models/dataModel.js'

const router = express.Router();

// add new investment for user
router.post('/api/investments', [authService, dataValidationService], (req, res) => {
    const userId = res.locals.user._id;
    const body = req.body;

    // create new investment associated with user id
    const newData = new Data ({
        userId: userId,
        initialAmount: body.initialAmount,
        monthlyDeposit: body.monthlyDeposit,
        interestRate: body.interestRate,
        numOfYears: body.numOfYears
    })

    // save new investment to database
    newData.save();
    
    res.status(204).send("Investment added successfully")
})

// get investments associated with a user
router.get('/api/investments', authService, async (req, res) => {
    const user = res.locals.user;

    // get investment data by user id
    const data = await Data.find({ userId: user._id }, { userId: 0 });
    
    res.status(200).send(data)
})

// update an investment in database
router.put('/api/investments/:id', [authService, dataValidationService], async (req, res) => {
    const body = req.body;

    // find and update requested investment by id
    await Data.findOneAndUpdate({ _id: req.params.id }, {
        monthlyDeposit: body.monthlyDeposit,
        interestRate: body.interestRate,
        numOfYears: body.numOfYears
    })

    res.status(204).send("Updated successfully")
})

// remove an investment from database
router.delete('/api/investments/:id', authService, async (req, res) => {
    // find and delete requested investment by id
    await Data.findOneAndDelete({ _id: req.params.id });

    res.status(204).send("Deleted successfully")
})

export default router
