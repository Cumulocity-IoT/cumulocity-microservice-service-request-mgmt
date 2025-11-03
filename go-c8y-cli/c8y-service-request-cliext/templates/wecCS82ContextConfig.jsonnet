{
    description: "CS82 Smoke detector Alarm Context",
    apply: {
        devicePredicate: [
            { 
                fragment: "ec_ControlType",
                regex: "^17$"
            }
        ],
        alarmPredicate: [
            { 
                fragment: "type",
                regex: "^ec_S:112/\\d+$"
            }
        ]
    },
    config: {
        dateFrom: "-1d",
        dateTo: "0d",
        datapoints: ["MHAI1.H1RMSA3", "MHAI1.H1RMSA1"],
        events: ["ec_HistoricalStatusEvent"]
    }
}