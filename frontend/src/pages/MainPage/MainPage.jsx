import { useState } from "react";
import { getSolarTimes } from "../../Service/apiService.js";
import { useAuth } from "../../Context/AuthContext.jsx";
import LoadingComponent from "../../components/Loading/LoadingComponent.jsx";
import SearchBar from "../../components/SearchBar/SearchBar.jsx";
import SolarWatchContent from "../../components/Content/SolarWatchContent.jsx";


export const MainPage = () => {
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const [searchCompleted, setSearchCompleted] = useState(false);
  const [solarData, setSolarData] = useState({
    city: 'city',
    country: 'country',
    sunrise: 'sunrise',
    sunset: 'sunset'
  });

  const handleSearch = async (city, countryCode, date, stateCode) => {
    try {
      setLoading(true);
      setSearchCompleted(false);

      const response = await getSolarTimes(user, city, countryCode, date, stateCode);

      setSolarData({
        city: city,
        country: countryCode,
        sunrise: response.results.sunrise,
        sunset: response.results.sunset,
        ...response
      });

      setSearchCompleted(true);
      setLoading(false);
    } catch (error) {
      console.error(error.message);
      setLoading(false);
      setSearchCompleted(false);
    }
  };

  if (loading) return (
    <div className="flex items-center justify-center h-screen">
      <LoadingComponent />
    </div>
  );

  return (
      <div className="w-full max-w-sm sm:max-w-lg md:max-w-2xl lg:max-w-3xl mx-auto px-4 py-8 bg-base-100">
    <div className="mb-8">
      <SolarWatchContent
        searchCompleted={searchCompleted}
        solarData={solarData}
      />
    </div>

    <div className="max-w-lg mx-auto mb-8">
      <SearchBar onSearch={handleSearch}/>
    </div>
  </div>
  );
};
