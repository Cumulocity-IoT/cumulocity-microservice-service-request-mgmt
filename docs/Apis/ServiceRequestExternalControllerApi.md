# ServiceRequestExternalControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getServiceRequestList1**](ServiceRequestExternalControllerApi.md#getServiceRequestList1) | **GET** /api/adapter/service/request | GET service request list |
| [**updateServiceRequestIsActiveById**](ServiceRequestExternalControllerApi.md#updateServiceRequestIsActiveById) | **PUT** /api/adapter/service/request/{serviceRequestId}/active | UPDATE service request active status by Id |
| [**updateServiceRequestStatusById**](ServiceRequestExternalControllerApi.md#updateServiceRequestStatusById) | **PUT** /api/adapter/service/request/{serviceRequestId}/status | UPDATE service request status by Id |


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

No authorization required

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

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

