# set all alarms with status CLEARED that are part of an open IoT notification to status ACKNOWLEDGED again^

# Retrieve the events and convert from JSON
$serviceReqList = c8y events list --type c8y_ServiceRequest --fragmentType sr_SyncStatus --fragmentValue "ACTIVE" --select id,text,time,source.id,sr_Status.id,sr_AlarmRef.0.id --includeAll | ConvertFrom-Json


$outputFile = "clearedAlarmsWithOpenNotification.txt"

# Filter and process the events
$serviceReqList | ForEach-Object {
    # Check if the event has a certain status
    $counter++

    Write-Output "---$($counter)----- Processing service request: $($_.id)"
    $alarm = c8y alarms get --id $_.sr_AlarmRef.id --select id,status,severity | ConvertFrom-Json
    Write-Output "Alarm ID: $($_.sr_AlarmRef), Severtiy: $($alarm.severity), Status: $($alarm.status)"

    if ($alarm.status -eq "CLEARED" -and $alarm.severity -ne "CRITICAL") {
        # Print out the relevant information
        #Write-Output "Cleared Alarm found, ID: $($_.sr_AlarmRef.id)"
        $output = "Cleared Alarm ID: $($_.sr_AlarmRef.id), Severtiy: $($alarm.severity) Service Request ID: $($_.id), Status: $($_.sr_Status.id), Source ID: $($_.source.id)"
        Write-Output $output
        Add-Content -Path $outputFile -Value $output
        c8y alarms update --id $($_.sr_AlarmRef.id) --status ACKNOWLEDGED --force
        $output2 = "Alarm: $($_.sr_AlarmRef.id) updated to Status: ACKNOWLEDGED"
        Write-Output $output2
        Add-Content -Path $outputFile -Value $output2
    }
}
