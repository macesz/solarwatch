$prompt_with_default = {
    param (
        [string]$var_name,
        [string]$default_value
    )
    $input = Read-Host "$var_name (press Enter to accept default: [$default_value])"
    if ($input -eq "") {
        return $default_value
    }
    return $input
}

Write-Host "Configuring environment variables..."
Write-Host ""
Write-Host "You need your own OpenWeatherMap API key. You can get a free one at https://home.openweathermap.org/users/sign_up"
Write-Host ""
Write-Host "If you have an existing Docker volume for this project with custom PostgreSQL credentials, please enter those values when prompted."
Write-Host ""
Write-Host "Otherwise you can customize values according to your preferences or press Enter to accept the default values shown in the [brackets]."
Write-Host "The values are saved to a .env file. When you want to launch the application again, you can just use the 'command docker-compose up' to start the containers"
Write-Host ""

$API_KEY = & $prompt_with_default "API_KEY" "Your own OpenWeatherMap API key"
$SPRING_DATASOURCE_USERNAME = & $prompt_with_default "SPRING_DATASOURCE_USERNAME" "postgres"
$SPRING_DATASOURCE_PASSWORD = & $prompt_with_default "SPRING_DATASOURCE_PASSWORD" "admin1234"
$GEOCODING_BASE_URL = & $prompt_with_default "GEOCODING_BASE_URL" "https://api.openweathermap.org/geo/1.0/direct"
$SUNRISESUNSET_BASE_URL = & $prompt_with_default "SUNRISESUNSET_BASE_URL" "https://api.sunrise-sunset.org/json"
$SECRET_KEY = & $prompt_with_default "SECRET_KEY" "Jitr5pYjU6d9ERzvRtUC3M1YST6P/O/FTqR/EK3wLpc="
$EXPIRATION = & $prompt_with_default "EXPIRATION" "86400000"
$DB_URL = & $prompt_with_default "DB_URL" "jdbc:postgresql://db:5432/solarwatch"

$envFilePath = "../.env"
Set-Content -Path $envFilePath -Value @"
# JWT Configuration
SECRET_KEY=$SECRET_KEY
EXPIRATION=$EXPIRATION

API_KEY=$API_KEY
GEOCODING_BASE_URL=$GEOCODING_BASE_URL
SUNRISESUNSET_BASE_URL=$SUNRISESUNSET_BASE_URL

# Database Configuration
DB_URL=$DB_URL
DB_USERNAME=$SPRING_DATASOURCE_USERNAME
DB_PASSWORD=$SPRING_DATASOURCE_PASSWORD

# PostgreSQL Environment Variables
SPRING_DATASOURCE_USERNAME=$SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD
"@

Write-Host ".env file created with your values or defaults."

Set-Location -Path ..

Write-Host "Starting Docker containers..."

docker-compose up