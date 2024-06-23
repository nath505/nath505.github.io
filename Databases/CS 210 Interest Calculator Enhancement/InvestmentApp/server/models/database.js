// MongoDB database
import mongoose from 'mongoose'

const dbUrl = process.env.DB_HOST || '127.0.0.1';

mongoose.connect(`mongodb://${dbUrl}/database`);

export default mongoose
