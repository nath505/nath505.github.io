import database from './database.js'

const dataSchema = new database.Schema({
  userId: String,
  initialAmount: Number,
  monthlyDeposit: Number,
  interestRate: Number,
  numOfYears: Number
}, { versionKey: false, collection: 'data' });

dataSchema.set('strictQuery', true);

const Data = database.model('Data', dataSchema);

export default Data
