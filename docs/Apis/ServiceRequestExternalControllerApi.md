# ServiceRequestExternalControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getServiceRequestCommentList1**](ServiceRequestExternalControllerApi.md#getServiceRequestCommentList1) | **GET** /api/adapter/service/request/{serviceRequestId}/comment | Returns all user comments of specific service request by internal Id. |
| [**getServiceRequestList1**](ServiceRequestExternalControllerApi.md#getServiceRequestList1) | **GET** /api/adapter/service/request | GET service request list |
| [**updateServiceRequestIsActiveById**](ServiceRequestExternalControllerApi.md#updateServiceRequestIsActiveById) | **PUT** /api/adapter/service/request/{serviceRequestId}/active | UPDATE service request active status by Id |
| [**updateServiceRequestStatusById**](ServiceRequestExternalControllerApi.md#updateServiceRequestStatusById) | **PUT** /api/adapter/service/request/{serviceRequestId}/status | UPDATE service request status by Id |


<a name="getServiceRequestCommentList1"></a>
# **getServiceRequestCommentList1**
> List getServiceRequestCommentList1(serviceRequestId)

Returns all user comments of specific service request by internal Id.

    Each service request can have n comments. This endpoint returns the complete list of user comments of a specific service request.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **serviceRequestId** | **String**|  | [default to null] |

### Return type

[**List**](../Models/ServiceRequestComment.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getServiceRequestList1"></a>
# **getServiceRequestList1**
> List getServiceRequestList1(assigned, serviceRequestIds)

GET service request list

    Returns a list of all service requests in IoT Platform. Additional query parameter allow to filter that list. Parameter assigned&#x3D;false returns all service requests which are not yet synchronized to external system. Parameter assigned&#x3D;true returns all synchronized service requests.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **assigned** | **Boolean**| Filter, assigned &#x3D;&#x3D; \&quot;true\&quot; returns all service request with external Id assigned, assigned &#x3D;&#x3D; \&quot;false\&quot; returns service requests which doesn&#39;t have external Id assigned. If query parameter is not set all service requests will be returned. | [optional] [default to null] |
| **serviceRequestIds** | [**List**](../Models/String.md)| Filter by service request IDs, returns all service requests with the IDs defined in that list | [optional] [default to null] |

### Return type

[**List**](../Models/ServiceRequest.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="updateServiceRequestIsActiveById"></a>
# **updateServiceRequestIsActiveById**
> ServiceRequest updateServiceRequestIsActiveById(serviceRequestId, body)

UPDATE service request active status by Id

    Updates specific service status request.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **serviceRequestId** | **String**|  | [default to null] |
| **body** | **Boolean**|  | |

### Return type

[**ServiceRequest**](../Models/ServiceRequest.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="updateServiceRequestStatusById"></a>
# **updateServiceRequestStatusById**
> ServiceRequest updateServiceRequestStatusById(serviceRequestId, ServiceRequestStatus)

UPDATE service request status by Id

    Updates specific service status request.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **serviceRequestId** | **String**|  | [default to null] |
| **ServiceRequestStatus** | [**ServiceRequestStatus**](../Models/ServiceRequestStatus.md)|  | |

### Return type

[**ServiceRequest**](../Models/ServiceRequest.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

