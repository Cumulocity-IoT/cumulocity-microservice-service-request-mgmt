# ServiceRequest
## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
| **id** | **String** | Internal ID, set by Cumulocity | [default to null] |
| **type** | **String** | Service request type | [default to null] |
| **status** | [**ServiceRequestStatus**](ServiceRequestStatus.md) | Service request status | [default to null] |
| **priority** | [**ServiceRequestPriority**](ServiceRequestPriority.md) | Servcie request priority | [optional] [default to null] |
| **title** | **String** | Service request title / summary | [default to null] |
| **description** | **String** | Service request detailed description | [optional] [default to null] |
| **source** | [**ServiceRequestSource**](ServiceRequestSource.md) | Cumulocity Device (managed object) reference | [default to null] |
| **alarmRef** | [**ServiceRequestDataRef**](ServiceRequestDataRef.md) | Cumulocity Alarm reference, deprecated, use alarmRefList instead of alarmRef! | [optional] [default to null] |
| **alarmRefList** | [**Set**](ServiceRequestDataRef.md) | Cumulocity Alarm reference list | [optional] [default to null] |
| **eventRef** | [**ServiceRequestDataRef**](ServiceRequestDataRef.md) | Cumulocity Event reference | [optional] [default to null] |
| **eventRefList** | [**Set**](ServiceRequestDataRef.md) | Cumulocity Event reference list | [optional] [default to null] |
| **seriesRef** | [**ServiceRequestDataRef**](ServiceRequestDataRef.md) | Cumulocity Measurement series reference | [optional] [default to null] |
| **creationTime** | **Date** | Creation date time | [default to null] |
| **lastUpdated** | **Date** | Update date time | [default to null] |
| **owner** | **String** | Creator / owner | [default to null] |
| **isActive** | **Boolean** | Service request active flag, shows if the service request is active! | [optional] [default to null] |
| **isClosed** | **Boolean** | Service request closed flag, shows if the service request is closed! This fragment is basicaly used for retention rules. | [optional] [default to null] |
| **externalId** | **String** | Service request external ID, contains the service request object ID of the external system. | [optional] [default to null] |
| **attachment** | [**ServiceRequestAttachment**](ServiceRequestAttachment.md) | File attachment of Service Request | [optional] [default to null] |
| **order** | [**ServiceOrder**](ServiceOrder.md) | Service Order, reference object to any kind of service order. Be aware the the order object must be complete. Partial order objects will set all not initialized properties to null! | [optional] [default to null] |
| **fieldAssignee** | **String** | Assignee (service technician) of the service request, shows who is currently assigned to the service task. | [optional] [default to null] |
| **fieldScheduleStart** | **Date** | Schedule start time of the service task, shows when the service task should be started. | [optional] [default to null] |
| **fieldScheduleEnd** | **Date** | Schedule end time of the service task, shows when the service task should be finished. | [optional] [default to null] |
| **fieldScheduleDue** | **Date** | Schedule due time of the service task, shows when the service task is due. | [optional] [default to null] |
| **fieldProgressPercentage** | **Integer** | Progress percentage of the service task, shows the current progress of the service task. | [optional] [default to null] |
| **fsmLink** | **String** | FSM link of the service task, shows the link to the FSM system. | [optional] [default to null] |
| **customProperties** | **Map** | Custom specific properties | [optional] [default to null] |

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)

