import axios from "axios";

export const registerUser = async (user) => {
  try {
    const response = await axios.post("/api/user/register", user);
    return response.data;
  } catch (error) {
    console.error("Registration error:", error);
    throw error;
  }
};

export const loginUser = async (user) => {
  try {
    const response = await axios.post("/api/user/signin", user);
    return response.data;
  } catch (error) {
    console.error("Login error:", error);
    throw error;
  }
};

export const getSolarTimes = async (user, city, countryCode, date, stateCode = null) => {
  const params = {
    city,
    countryCode,
    date
  };

  if (stateCode) {
    params.stateCode = stateCode;
  }

  try {
    const response = await axios.get('/api/sunset-sunrise', {
      params,
      headers: {
        'Authorization': `Bearer ${user.jwtToken}`,
      }
    });

    return response.data;
  } catch (error) {
    console.error('Error fetching solar times:', error);
    throw error;
  }
};