# ServiceRequestPatchRqBody
## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
| **status** | [**ServiceRequestStatus**](ServiceRequestStatus.md) | Service request status | [optional] [default to null] |
| **priority** | [**ServiceRequestPriority**](ServiceRequestPriority.md) | Servcie request priority | [optional] [default to null] |
| **title** | **String** | Service request title / summary | [optional] [default to null] |
| **description** | **String** | Service request detailed description | [optional] [default to null] |
| **isActive** | **Boolean** | Service request active flag, shows if the service request is active! | [optional] [default to null] |
| **externalId** | **String** | Service request external ID, contains the service request object ID of the external system. | [optional] [default to null] |
| **order** | [**ServiceOrder**](ServiceOrder.md) | Service Order | [optional] [default to null] |
| **fieldAssignee** | **String** | Assignee (service technician) of the service request, shows who is currently assigned to the service task. | [optional] [default to null] |
| **fieldScheduleStart** | **Date** | Schedule start time of the service task, shows when the service task should be started. | [optional] [default to null] |
| **fieldScheduleEnd** | **Date** | Schedule end time of the service task, shows when the service task should be finished. | [optional] [default to null] |
| **fieldScheduleDue** | **Date** | Schedule due time of the service task, shows when the service task is due. | [optional] [default to null] |
| **fieldProgressPercentage** | **Integer** | Progress percentage of the service task, shows the current progress of the service task. | [optional] [default to null] |
| **fsmLink** | **String** | FSM link of the service task, shows the link to the FSM system. | [optional] [default to null] |
| **customProperties** | **Map** | Custom specific properties | [optional] [default to null] |

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)

