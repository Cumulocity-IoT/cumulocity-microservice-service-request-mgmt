# ServiceRequestPatchRqBody
## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
| **status** | [**ServiceRequestStatus**](ServiceRequestStatus.md) |  | [optional] [default to null] |
| **priority** | [**ServiceRequestPriority**](ServiceRequestPriority.md) |  | [optional] [default to null] |
| **title** | **String** | Service request title / summary | [optional] [default to null] |
| **description** | **String** | Service request detailed description | [optional] [default to null] |
| **isActive** | **Boolean** | Service request active flag, shows if the service request is active! | [optional] [default to null] |
| **externalId** | **String** | Service request external ID, contains the service request object ID of the external system. | [optional] [default to null] |
| **order** | [**ServiceOrder**](ServiceOrder.md) |  | [optional] [default to null] |
| **customProperties** | **Map** | Custom specific properties | [optional] [default to null] |

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)

