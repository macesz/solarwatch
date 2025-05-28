const SolarWatchContent = ({ searchCompleted, solarData }) => {
  return (
    <>
      {!searchCompleted && (
        <div className="text-center py-16">
          <div className="max-w-md mx-auto bg-base-200 rounded-lg p-8 shadow-md">
            <h2 className="text-xl font-semibold mb-2">Welcome to Solar Watch</h2>
            <p className="opacity-70 mb-4">
              Enter a city name and date to discover sunrise and sunset times.
            </p>
            <div className="w-16 h-16 mx-auto mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                   className="text-secondary w-full h-full" strokeWidth="2" strokeLinecap="round"
                   strokeLinejoin="round">
                <circle cx="12" cy="12" r="5"/>
                <line x1="12" y1="1" x2="12" y2="3"/>
                <line x1="12" y1="21" x2="12" y2="23"/>
                <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/>
                <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
                <line x1="1" y1="12" x2="3" y2="12"/>
                <line x1="21" y1="12" x2="23" y2="12"/>
                <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/>
                <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
              </svg>
            </div>
          </div>
        </div>
      )}

      {searchCompleted && (
        <div className="space-y-8">
          <section className="bg-base-200 rounded-lg p-6 shadow-md">
            <h2 className="text-xl font-semibold mb-4">Solar Information</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="bg-base-300 p-4 rounded-md">
                <div className="text-sm opacity-70">City</div>
                <div className="text-lg font-medium">{solarData.city}</div>
              </div>
              <div className="bg-base-300 p-4 rounded-md">
                <div className="text-sm opacity-70">Country</div>
                <div className="text-lg font-medium">{solarData.country}</div>
              </div>
              <div className="bg-base-300 p-4 rounded-md">
                <div className="text-sm text-secondary">Sunrise</div>
                <div className="text-lg font-medium">{solarData.sunrise}</div>
              </div>
              <div className="bg-base-300 p-4 rounded-md">
                <div className="text-sm text-accent">Sunset</div>
                <div className="text-lg font-medium">{solarData.sunset}</div>
              </div>
            </div>
          </section>
        </div>
      )}
    </>
  );
};

export default SolarWatchContent;