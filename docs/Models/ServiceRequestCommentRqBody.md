# ServiceRequestCommentRqBody
## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
| **text** | **String** | comment text | [default to null] |
| **externalId** | **String** | Service request comment external ID, contains the service request comment object ID of the external system. | [optional] [default to null] |
| **isClosed** | **Boolean** | Service request closed flag, shows if the service request comment is closed! This fragment is basicaly used for retention rules. | [optional] [default to null] |
| **type** | **String** | comment type enumeration (USER, SYSTEM) | [default to null] |
| **owner** | **String** | creator / owner  | [default to null] |
| **attachment** | [**ServiceRequestAttachment**](ServiceRequestAttachment.md) |  | [optional] [default to null] |

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)

