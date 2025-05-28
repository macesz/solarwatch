import {useState} from "react";
import SearchInputField from "../Inputs/SearchInputField";
import DateInputField from "../Inputs/DateInputField";

const SearchBar = ({onSearch}) => {
  const [city, setCity] = useState('');
  const [countryCode, setCountryCode] = useState('');
  const [stateCode, setStateCode] = useState('');
  const [date, setDate] = useState('');

  const handleCityChange = (e) => {
    setCity(e.target.value);
  }

  const handleCountryCodeChange = (e) => {
    setCountryCode(e.target.value);
  }

  const handleStateCodeChange = (e) => {
    setStateCode(e.target.value);
  }

  const handleDateChange = (e) => {
    setDate(e.target.value);
  }

  const handleSubmit = (e) => {
    e.preventDefault();
    if (city.trim() && countryCode.trim() && date) {
      onSearch(city, countryCode, date, stateCode || null);
    }
  }

  return (
    <form className="flex flex-col items-center space-y-4" onSubmit={handleSubmit}>

      <SearchInputField
        placeholder={"Enter a city"}
        value={city}
        onChange={handleCityChange}
        required={true} 
      />

      <SearchInputField
        placeholder={"Country code (e.g., GB, US, DE)"}
        value={countryCode}
        onChange={handleCountryCodeChange}
        maxLength={3}
        required={true} 
      />

      <SearchInputField
        placeholder={"State code (optional, e.g., CA, NY)"}
        value={stateCode}
        onChange={handleStateCodeChange}
        maxLength={2}
        required={false} 
      />

      <DateInputField
        value={date}
        onChange={handleDateChange}
        required={true} 
      />

      <button className="btn btn-soft btn-success" type="submit">Go</button>

    </form>
  );
}

export default SearchBar;