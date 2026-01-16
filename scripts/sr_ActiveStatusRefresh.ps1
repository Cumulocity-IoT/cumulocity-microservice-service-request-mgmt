# Fetch the devices which need to be refreshed.
$output = c8y inventory find --query "ec_Service.ec_WindFarmId eq 100117" -p 100 --select id --output csv

# Process the output and filter out empty lines
$deviceIds = $output -split "`n" | Where-Object { $_.Trim() -ne "" }

# Create the JSON object
$jsonObject = @{
    deviceIds = $deviceIds
}

# Convert to JSON
$json = $jsonObject | ConvertTo-Json -Compress

# Output to console and call the CLI extension
Write-Output $json
c8y service-request-cliext device refresh --template $json
