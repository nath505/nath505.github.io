import dotenv from 'dotenv'
import express, { json } from 'express'
import cookieParser from 'cookie-parser'
import session from 'express-session'

// get the routers
import usersRouter from './server/routes/usersRouter.js'
import loginRouter from './server/routes/loginRouter.js'
import logoutRouter from './server/routes/logoutRouter.js'
import registerRouter from './server/routes/registerRouter.js'
import investmentsRouter from './server/routes/investmentsRouter.js'

// create a new Express server
const app = express();

// initialize .env variables
dotenv.config();

// use JSON parser
app.use(json());

// use cookies for authentication purposes
app.use(cookieParser());

const port = process.env.PORT || 5000;
const origin = process.env.ORIGIN || 3000;

app.listen(port, () => {
  console.log(`Server running on port ${port}`);
})

// enable CORS
app.use((req, res, next) => {
  res.append('Access-Control-Allow-Origin', `http://localhost:${origin}`);
  res.append('Access-Control-Allow-Credentials', 'true');
  res.append('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept');
  res.append('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE');
  next();
});

// handle session data
app.use(session({
  resave: false,
  saveUninitialized: true,
  secret: process.env.SESSION_SECRET
}));

// connect the routers
app.use('/', usersRouter);
app.use('/', loginRouter);
app.use('/', logoutRouter);
app.use('/', registerRouter);
app.use('/', investmentsRouter);

export default app
