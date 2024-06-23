import database from './database.js'

const userSchema = new database.Schema({
  username: String,
  password: String
}, { versionKey: false });

userSchema.set('strictQuery', true);

const User = database.model('User', userSchema);

export default User
