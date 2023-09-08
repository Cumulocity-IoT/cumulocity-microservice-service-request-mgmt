# ServiceRequestComment
## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
| **id** | **String** | Internal id of comment | [default to null] |
| **owner** | **String** | creator / owner  | [default to null] |
| **source** | [**ServiceRequestSource**](ServiceRequestSource.md) |  | [default to null] |
| **creationTime** | **Date** | creation time | [default to null] |
| **lastUpdated** | **Date** | Update date time | [default to null] |
| **text** | **String** | comment text | [optional] [default to null] |
| **type** | **String** | comment type enumeration (USER, SYSTEM) | [default to null] |
| **serviceRequestId** | **String** | Internal id of service request | [default to null] |
| **externalId** | **String** | Service request comment external ID, contains the service request comment object ID of the external system. | [optional] [default to null] |
| **attachment** | [**ServiceRequestAttachment**](ServiceRequestAttachment.md) |  | [optional] [default to null] |

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)

