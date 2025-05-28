import React from "react";
import {useNavigate} from "react-router-dom";
import LoginForm from "../../components/Forms/LoginForms";

const Login = () => {
  const navigate = useNavigate();

  const handleCancel = () => {
    navigate("/");
  }

  return (
    <div className="flex justify-center pt-12 min-h-screen">
      <LoginForm onCancel={handleCancel} />
    </div>
  )
}

export default Login;
