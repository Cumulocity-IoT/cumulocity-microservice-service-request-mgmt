# ServiceRequestExternalControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getServiceRequestByExternalId**](ServiceRequestExternalControllerApi.md#getServiceRequestByExternalId) | **GET** /api/adapter/service/request/{serviceRequestExternalId} | GET service request by external Id |
| [**getServiceRequestList1**](ServiceRequestExternalControllerApi.md#getServiceRequestList1) | **GET** /api/adapter/service/request | GET service request list |
| [**syncServiceRequest**](ServiceRequestExternalControllerApi.md#syncServiceRequest) | **POST** /api/adapter/service/request/{serviceRequestId} | SYNC service request into external object |
| [**updateServiceRequestByExternalId**](ServiceRequestExternalControllerApi.md#updateServiceRequestByExternalId) | **PUT** /api/adapter/service/request/{serviceRequestId} | UPDATE service request by external Id |


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

<a name="getServiceRequestList1"></a>
# **getServiceRequestList1**
> List getServiceRequestList1(assigned)

GET service request list

    Returns a list of all service requests in IoT Platform. Additional query parameter allow to filter that list. Parameter assigned&#x3D;false returns all service requests which are not assigned to external object. Parameter assigned&#x3D;true returns all assigned service requests.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **assigned** | **Boolean**| filter, \&quot;true\&quot; returns all service request with external Id assigned, \&quot;false\&quot; returns service requests which doesn&#39;t have external Id assigned. | [default to null] |

### Return type

[**List**](../Models/ServiceRequest.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="syncServiceRequest"></a>
# **syncServiceRequest**
> ServiceRequestRef syncServiceRequest(serviceRequestId)

SYNC service request into external object

    Triggers the adapter to update or create new external object at external system. If ServiceRequestRef contains already an externalId it is most likely an update. If the request body contains only internalId a new object must be created.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **serviceRequestId** | **String**|  | [default to null] |

### Return type

[**ServiceRequestRef**](../Models/ServiceRequestRef.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="updateServiceRequestByExternalId"></a>
# **updateServiceRequestByExternalId**
> ServiceRequest updateServiceRequestByExternalId(serviceRequestId, ServiceRequestPatchRqBody)

UPDATE service request by external Id

    Updates specific service request.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **serviceRequestId** | **String**|  | [default to null] |
| **ServiceRequestPatchRqBody** | [**ServiceRequestPatchRqBody**](../Models/ServiceRequestPatchRqBody.md)|  | |

### Return type

[**ServiceRequest**](../Models/ServiceRequest.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

