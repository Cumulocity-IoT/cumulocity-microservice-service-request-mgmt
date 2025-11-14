
$response = c8y events list --type c8y_ServiceRequest --fragmentType sr_SyncStatus --fragmentValue "ACTIVE" --withTotalElements | ConvertFrom-Json

$totalElements = $response.statistics.totalElements
$pages = [math]::Ceiling($totalElements / 2000)

$pages = [math]::Ceiling($totalElements / 2000)

Write-Output "Total Elements: $totalElements"
Write-Output "Pages: $pages"

for ($page = 1; $page -le $pages; $page++) {
    Write-Output "Processing page $page of $pages"
    $eventResult = c8y events list --type c8y_ServiceRequest --fragmentType sr_SyncStatus --fragmentValue "ACTIVE" --pageSize 2000 --currentPage $page --outputTemplate '[{"eventId": item.id, "id": item.sr_AlarmRef[0].id, "externalId": item.sr_ExternalId} for item in output]' | ConvertFrom-Json
    
    # Process events in parallel with 10 threads
    $eventResult | ForEach-Object -Parallel {
        $result = c8y alarms get --id $_.id --view off 2>$null
        if ($LASTEXITCODE -ne 0) {
            $output = "Service Request: $($_.eventId), External ID: $($_.externalId), Alarm ID: $($_.id) not found."
            Write-Output $output
            Add-Content -Path "AlarmNotFound.txt" -Value $output
        }
    } -ThrottleLimit 10
}