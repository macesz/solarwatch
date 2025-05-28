const DateInputField = ({value, onChange}) => {
  return (
    <input
      type="date"
      className="input"
      value={value}
      onChange={onChange}
    />
  )
}

export default DateInputField;