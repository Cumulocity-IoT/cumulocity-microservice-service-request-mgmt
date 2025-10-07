{
    description: "WEC CS82 context config",
    apply: {
        devicePredicate: [
            { 
                fragment: "ec_ControlSoftware",
                regex: "^CS82$"
            }
        ],
        alarmPredicate: [
            { 
                fragment: "type",
                regex: "^ec_S:242\\/232$"
            }
        ]
    },
    config: {
        dateFrom: "-1d",
        dateTo: "0d",
        datapoints: ["MHAI1.H1RMSA1", "MHAI1.H1RMSA2"],
        events: ["ec_S:242/232", "ec_S:66/11"]
    },
    isActive: true
}