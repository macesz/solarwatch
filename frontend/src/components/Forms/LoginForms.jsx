import React from "react";
import {useState} from "react";
import {loginUser} from "../../Service/apiService.js";
import {useNavigate} from "react-router-dom";
import {useAuth} from "../../Context/AuthContext.jsx";
import UsernameInputField from "../Inputs/UsernameInputField.jsx";
import PasswordInputField from "../Inputs/PasswordInputField.jsx";

const LoginForm = ({onCancel}) => {
  const {login} = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const userData = await loginUser({username, password});
      login(userData);
      navigate("/home");
    } catch (error) {
      console.log(error);
      navigate("/login");
      alert("Failed to login, incorrect credentials.");
    }

  }

  return (
    <form onSubmit={handleLogin}>
      <div>

        <h1 className="flex justify-center gap-4 mt-4">Login to SolarWatch</h1>

        <UsernameInputField
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />

        <PasswordInputField
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

      </div>

      <div className="flex justify-center gap-4 mt-4">
        <button className="btn btn-soft btn-success" type="submit">Login</button>
        <button className="btn btn-soft btn-secondary" type="button" onClick={onCancel}>Cancel</button>
      </div>

    </form>
  )
}

export default LoginForm;