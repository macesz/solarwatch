import React from "react";
import {useNavigate} from "react-router-dom";
import RegistrationForm from "../../components/Forms/RegistrationFrom";

const Register = () => {
  const navigate = useNavigate();

  const handleCancel = () => {
    navigate("/");
  }

  return (
    <div className="flex justify-center pt-12 min-h-screen">
      <RegistrationForm onCancel={handleCancel} />
    </div>
  )
}

export default Register;
