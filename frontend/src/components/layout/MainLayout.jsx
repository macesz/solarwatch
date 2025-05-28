import React from 'react'  
import { Outlet, Link } from 'react-router-dom'  

const MainLayout = () => {  
  return (  
    <div className="min-h-screen bg-slate-50">  
      {/* Navigation Header */}  
      <header className="bg-white shadow-sm">  
        <div className="container mx-auto px-4 py-4 flex justify-center items-center">
          <Link to="/" className="text-xl font-bold text-blue-600">  
            Sunrise Tracker  
          </Link>  
          
          <nav className="space-x-4">  
            <Link to="/" className="text-gray-600 hover:text-blue-600">  
              Home  
            </Link>  
            <Link to="/auth" className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">  
              Sign In  
            </Link>  
          </nav>  
        </div>  
      </header>  
      
      {/* Main Content */}  
      <main className="container mx-auto px-4 py-8">  
        <Outlet />  
      </main>  
      
      {/* Footer */}  
      <footer className="bg-gray-800 text-white py-6">  
        <div className="container mx-auto px-4 text-center">  
          <p>© {new Date().getFullYear()} Sunrise Tracker. All rights reserved.</p>  
        </div>  
      </footer>  
    </div>  
  )  
}  

export default MainLayout