# ServiceRequestStatusConfig
## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
| **id** | **String** | Internal id of status | [default to null] |
| **name** | **String** | Name of status | [default to null] |
| **alarmStatusTransition** | **String** | Set Alarm status when this status of Service Request status is set. (Transition) | [optional] [default to null] |
| **isClosedTransition** | **Boolean** | Closes Service Request when this status of Service Request is set. | [optional] [default to null] |
| **isDeactivateTransition** | **Boolean** | Deactivates Service Request when this status of Service Request is set. The service request disappears of the list. | [optional] [default to null] |
| **excludeForCounter** | **Boolean** | All active (not closed) Service Requests are counted on device manage object. However, with with parameter the status can be excluded and will not be counted! | [optional] [default to null] |
| **icon** | **String** | Icon name of the status which should be shown at the UI | [optional] [default to null] |

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)

