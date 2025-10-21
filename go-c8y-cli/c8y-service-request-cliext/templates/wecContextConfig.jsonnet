{
    description: "Microservice Template alarm context",
    apply: {
        devicePredicate: [
            { 
                fragment: "type",
                regex: "^c8y_MQTTDevice$"
            }
        ],
        alarmPredicate: [
            { 
                fragment: "type",
                regex: "^my_TestAlarm([1-9]|[1-9][0-9])$"
            }
        ]
    },
    config: {
        dateFrom: "-1d",
        dateTo: "0d",
        datapoints: ["Gn1.RotSpd", "Gn1.Power"],
        events: ["my_StatusChangeEvent", "my_OtherEvent"],
        alarms: ["my_Alarm1", "my_Alarm2"]
    }
}