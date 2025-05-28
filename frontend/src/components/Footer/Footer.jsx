const Footer = () => {
  return (
    <>
      <footer className="bg-base-200 p-4 text-center">
        <div className="container mx-auto">
          <p className="opacity-70">© {new Date().getFullYear()} Solar Watch</p>
        </div>
      </footer>
    </>
  )
}

export default Footer;