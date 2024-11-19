# Retrieve the events and convert from JSON
$alarms = c8y alarms list --status ACKNOWLEDGED --select id,text,sr_EventId -p 1000 | ConvertFrom-Json

$counter = 0

$outputFile = "output.txt"

# Filter and process the events
$alarms | ForEach-Object {
    # Check if the event has a certain status
    $counter++

    Write-Output "---$($counter)----- Processing alarm: $($_.id)"
    $event = c8y events get --id $_.sr_EventId --select id,sr_Status | ConvertFrom-Json

    if ($event.sr_Status.id -eq "20") {
        # Print out the relevant information
        Write-Output "Event ID: $($event.id)"
        Write-Output "Status: $($event.sr_Status)"
    }
}