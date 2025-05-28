import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import React from 'react'
import './index.css'
import {createBrowserRouter, RouterProvider} from 'react-router-dom'
import Layout from './pages/Layout/Layout'
import Login from './pages/Login/Login'
import Register from './pages/Register/Register'
import ProtectedRoute from './components/ProtectedRoute' // Adjust path as needed
import { AuthProvider } from './Context/AuthContext'
import { MainPage } from './pages/MainPage/MainPage'

const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout/>,
    children: [
      {
        path: "/register",
        element: <Register/>
      },
      {
        path: "/login", 
        element: <Login/>
      },
      {
        path: "/home",
        element: <ProtectedRoute><MainPage/></ProtectedRoute>
      }
    ]
  }
])  

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <AuthProvider>
      <RouterProvider router={router}/>
    </AuthProvider>
  </StrictMode>
)
