import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Login from './Login'
import Register from './Register'
import Home from './Home'
import AddInvestment from './AddInvestment'
import UpdateInvestment from './UpdateInvestment'

function App() {
  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path='/' element={<Login />} />
          <Route path='/login' element={<Login />} />
          <Route path='/register' element={<Register />} />
          <Route path='/home' element={<Home />} />
          <Route path='/add-investment' element={<AddInvestment />} />
          <Route path='/update-investment' element={<UpdateInvestment />} />
        </Routes>
      </BrowserRouter>
    </>
  )
}

export default App
