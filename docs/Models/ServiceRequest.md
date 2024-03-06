# ServiceRequest
## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
| **id** | **String** | Internal ID, set by Cumulocity | [default to null] |
| **type** | **String** | Service request type | [default to null] |
| **status** | [**ServiceRequestStatus**](ServiceRequestStatus.md) |  | [default to null] |
| **priority** | [**ServiceRequestPriority**](ServiceRequestPriority.md) |  | [optional] [default to null] |
| **title** | **String** | Service request title / summary | [default to null] |
| **description** | **String** | Service request detailed description | [optional] [default to null] |
| **source** | [**ServiceRequestSource**](ServiceRequestSource.md) |  | [default to null] |
| **alarmRef** | [**ServiceRequestDataRef**](ServiceRequestDataRef.md) |  | [optional] [default to null] |
| **alarmRefList** | [**Set**](ServiceRequestDataRef.md) | Cumulocity Alarm reference list | [optional] [default to null] |
| **eventRef** | [**ServiceRequestDataRef**](ServiceRequestDataRef.md) |  | [optional] [default to null] |
| **seriesRef** | [**ServiceRequestDataRef**](ServiceRequestDataRef.md) |  | [optional] [default to null] |
| **creationTime** | **Date** | Creation date time | [default to null] |
| **lastUpdated** | **Date** | Update date time | [default to null] |
| **owner** | **String** | Creator / owner | [default to null] |
| **isActive** | **Boolean** | Service request active flag, shows if the service request is active! | [optional] [default to null] |
| **isClosed** | **Boolean** | Service request closed flag, shows if the service request is closed! This fragment is basicaly used for retention rules. | [optional] [default to null] |
| **externalId** | **String** | Service request external ID, contains the service request object ID of the external system. | [optional] [default to null] |
| **attachment** | [**ServiceRequestAttachment**](ServiceRequestAttachment.md) |  | [optional] [default to null] |
| **customProperties** | **Map** | Custom specific properties | [optional] [default to null] |

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)

