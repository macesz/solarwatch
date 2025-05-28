import React from "react";
import {Link} from "react-router-dom";

const Navbar = () => {
  return (

    <div className="navbar bg-base-100 shadow-sm">

      <div className="flex-1">
        <Link className="btn btn-ghost text-xl" to={"/"}>
          SolarWatch
        </Link>
      </div>

      <div className="flex-none">
        <ul className="menu menu-horizontal px-1">

          <li>
            <Link className="btn btn-ghost txt-xl" to="/register">
              Register
            </Link>
          </li>

          <li>
            <Link className="btn btn-ghost txt-xl" to="/login">
              Login
            </Link>
          </li>

        </ul>
      </div>
    </div>

  )
}

export default Navbar;
