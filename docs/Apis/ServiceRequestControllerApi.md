# ServiceRequestControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createServiceRequest**](ServiceRequestControllerApi.md#createServiceRequest) | **POST** /api/service/request/ | CREATE service request |
| [**deleteServiceRequestById**](ServiceRequestControllerApi.md#deleteServiceRequestById) | **DELETE** /api/service/request/{serviceRequestId} | DELETE service request by Id |
| [**getServiceRequestById**](ServiceRequestControllerApi.md#getServiceRequestById) | **GET** /api/service/request/{serviceRequestId} | GET service request by Id |
| [**getServiceRequestList**](ServiceRequestControllerApi.md#getServiceRequestList) | **GET** /api/service/request/ | GET service request list |
| [**updateServiceRequestById**](ServiceRequestControllerApi.md#updateServiceRequestById) | **PATCH** /api/service/request/{serviceRequestId} | PATCH service request by Id |


<a name="createServiceRequest"></a>
# **createServiceRequest**
> ServiceRequest createServiceRequest(ServiceRequestPostRqBody)

CREATE service request

    Creates a new service request object at Cumulocity IoT Platform.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **ServiceRequestPostRqBody** | [**ServiceRequestPostRqBody**](../Models/ServiceRequestPostRqBody.md)|  | |

### Return type

[**ServiceRequest**](../Models/ServiceRequest.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="deleteServiceRequestById"></a>
# **deleteServiceRequestById**
> deleteServiceRequestById(serviceRequestId)

DELETE service request by Id

    Delete a service request object at Cumulocity IoT. Related object at external system will not be deleted!

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **serviceRequestId** | **Integer**|  | [default to null] |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

<a name="getServiceRequestById"></a>
# **getServiceRequestById**
> ServiceRequest getServiceRequestById(serviceRequestId)

GET service request by Id

    Returns service request by internal Id

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **serviceRequestId** | **Integer**|  | [default to null] |

### Return type

[**ServiceRequest**](../Models/ServiceRequest.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getServiceRequestList"></a>
# **getServiceRequestList**
> RequestListServiceRequest getServiceRequestList(deviceId, all, pageSize, withTotalPages)

GET service request list

    Returns a list of all service requests in IoT Platform. Additional query parameters allow to filter that list. The default configuration will return all service requests which are not closed! With parameter all&#x3D;true, all service requests will be returned without fillter.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **deviceId** | **String**| Filter, returns all service request equal device Id | [optional] [default to null] |
| **all** | **Boolean**| filter, \&quot;true\&quot; returns all service request, \&quot;false\&quot; (default) returns only active service requests. | [optional] [default to null] |
| **pageSize** | **Integer**| Indicates how many entries of the collection shall be returned. The upper limit for one page is 2,000 objects. | [optional] [default to null] |
| **withTotalPages** | **Boolean**| When set to true, the returned result will contain in the statistics object the total number of pages. Only applicable on range queries. | [optional] [default to null] |

### Return type

[**RequestListServiceRequest**](../Models/RequestListServiceRequest.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="updateServiceRequestById"></a>
# **updateServiceRequestById**
> ServiceRequest updateServiceRequestById(serviceRequestId, ServiceRequestPatchRqBody)

PATCH service request by Id

    Updates specific service request.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **serviceRequestId** | **Integer**|  | [default to null] |
| **ServiceRequestPatchRqBody** | [**ServiceRequestPatchRqBody**](../Models/ServiceRequestPatchRqBody.md)|  | |

### Return type

[**ServiceRequest**](../Models/ServiceRequest.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

