# ServiceRequestExternalControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getServiceRequestByExternalId**](ServiceRequestExternalControllerApi.md#getServiceRequestByExternalId) | **GET** /api/service/request/external/{serviceRequestExternalId} | GET service request by external Id |
| [**syncServiceRequest**](ServiceRequestExternalControllerApi.md#syncServiceRequest) | **POST** /api/service/request/external | SYNC service request into external object |
| [**updateServiceRequestByExternalId**](ServiceRequestExternalControllerApi.md#updateServiceRequestByExternalId) | **PATCH** /api/service/request/external/{serviceRequestExternalId} | UPDATE service request by external Id |


<a name="getServiceRequestByExternalId"></a>
# **getServiceRequestByExternalId**
> ServiceRequest getServiceRequestByExternalId(serviceRequestExternalId)

GET service request by external Id

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **serviceRequestExternalId** | **String**|  | [default to null] |

### Return type

[**ServiceRequest**](../Models/ServiceRequest.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="syncServiceRequest"></a>
# **syncServiceRequest**
> ServiceRequestRef syncServiceRequest(ServiceRequestRef)

SYNC service request into external object

    Triggers the adapter to update or create new external object at external system. If ServiceRequestRef contains already an externalId it is most likely an update. If the request body contains only internalId a new object must be created.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **ServiceRequestRef** | [**ServiceRequestRef**](../Models/ServiceRequestRef.md)|  | |

### Return type

[**ServiceRequestRef**](../Models/ServiceRequestRef.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="updateServiceRequestByExternalId"></a>
# **updateServiceRequestByExternalId**
> ServiceRequest updateServiceRequestByExternalId(serviceRequestExternalId, ServiceRequestPatchRqBody)

UPDATE service request by external Id

    Updates specific service request.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **serviceRequestExternalId** | **String**|  | [default to null] |
| **ServiceRequestPatchRqBody** | [**ServiceRequestPatchRqBody**](../Models/ServiceRequestPatchRqBody.md)|  | |

### Return type

[**ServiceRequest**](../Models/ServiceRequest.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

