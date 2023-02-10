# ServiceRequestPostRqBody
## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
| **type** | **String** | Service request type | [default to null] |
| **status** | [**ServiceRequestStatus**](ServiceRequestStatus.md) |  | [default to null] |
| **priority** | [**ServiceRequestPriority**](ServiceRequestPriority.md) |  | [optional] [default to null] |
| **title** | **String** | Service request title / summary | [default to null] |
| **description** | **String** | Service request detailed description | [optional] [default to null] |
| **deviceRef** | [**ServiceRequestDataRef**](ServiceRequestDataRef.md) |  | [default to null] |
| **alarmRef** | [**ServiceRequestDataRef**](ServiceRequestDataRef.md) |  | [optional] [default to null] |
| **eventRef** | [**ServiceRequestDataRef**](ServiceRequestDataRef.md) |  | [optional] [default to null] |
| **seriesRef** | [**ServiceRequestDataRef**](ServiceRequestDataRef.md) |  | [optional] [default to null] |
| **owner** | **String** | Creator / owner | [default to null] |
| **customProperties** | **Map** | Custom specific properties | [optional] [default to null] |

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)

