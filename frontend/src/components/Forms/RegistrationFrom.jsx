import React, { useState } from "react"
import { registerUser } from "../../Service/apiService.js";
import {useNavigate} from "react-router-dom";
import UsernameInputField from "../Inputs/UsernameInputField.jsx";
import PasswordInputField from "../Inputs/PasswordInputField.jsx";

const RegistrationForm = ({ user, onCancel }) => {
  const navigate = useNavigate();
  const [username, setUsername] = useState(user?.username || '');
  const [password, setPassword] = useState(user?.password || '');
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const onSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');
    setSuccessMessage('');

    if (!username || !password) {
      setErrorMessage("Username and password cannot be empty.");
      return;
    }

    try {
      const response = await registerUser({ username, password });

      if (response && response.status === 200) {
        setSuccessMessage("Registration successful!");
        setUsername('');
        setPassword('');
        navigate('/login');
      } else {
        setErrorMessage("Registration failed. Please try again.");
      }

    } catch (error) {
      setErrorMessage("An error occurred during registration. Please try again.");
      console.error(error);
    }
  };

  return (
    <form className="registration-form" onSubmit={onSubmit}>
      {errorMessage && <div className="error-message">{errorMessage}</div>}
      {successMessage && <div className="success-message">{successMessage}</div>}
      <div>

        <h1 className="flex justify-center gap-4 mt-4">Register a new user</h1>

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
        <button className="btn btn-soft btn-success" type="submit">Register</button>
        <button className="btn btn-soft btn-secondary" type="button" onClick={onCancel}>Cancel</button>
      </div>
    </form>
  );
};

export default RegistrationForm;
