# ServiceRequestControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**addAlarmRefToServiceRequest**](ServiceRequestControllerApi.md#addAlarmRefToServiceRequest) | **PUT** /api/service/request/{serviceRequestId}/alarm | Add alarm reference to service request |
| [**addEventRefToServiceRequest**](ServiceRequestControllerApi.md#addEventRefToServiceRequest) | **PUT** /api/service/request/{serviceRequestId}/event | Add event reference to service request |
| [**createServiceRequest**](ServiceRequestControllerApi.md#createServiceRequest) | **POST** /api/service/request | CREATE service request |
| [**deleteServiceRequestById**](ServiceRequestControllerApi.md#deleteServiceRequestById) | **DELETE** /api/service/request/{serviceRequestId} | DELETE service request by Id |
| [**downloadServiceRequestAttachment**](ServiceRequestControllerApi.md#downloadServiceRequestAttachment) | **GET** /api/service/request/{serviceRequestId}/attachment | DOWNLOAD attachment for specific service request |
| [**getServiceRequestById**](ServiceRequestControllerApi.md#getServiceRequestById) | **GET** /api/service/request/{serviceRequestId} | GET service request by Id |
| [**getServiceRequestList**](ServiceRequestControllerApi.md#getServiceRequestList) | **GET** /api/service/request | GET service request list |
| [**updateServiceRequestById**](ServiceRequestControllerApi.md#updateServiceRequestById) | **PUT** /api/service/request/{serviceRequestId} | PUT service request by Id |
| [**uploadServiceRequestAttachment**](ServiceRequestControllerApi.md#uploadServiceRequestAttachment) | **POST** /api/service/request/{serviceRequestId}/attachment | UPLOAD attachment for specific service request |


<a name="addAlarmRefToServiceRequest"></a>
# **addAlarmRefToServiceRequest**
> ServiceRequest addAlarmRefToServiceRequest(serviceRequestId, ServiceRequestDataRef)

Add alarm reference to service request

    Add alarm reference to service request

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **serviceRequestId** | **String**|  | [default to null] |
| **ServiceRequestDataRef** | [**ServiceRequestDataRef**](../Models/ServiceRequestDataRef.md)|  | |

### Return type

[**ServiceRequest**](../Models/ServiceRequest.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="addEventRefToServiceRequest"></a>
# **addEventRefToServiceRequest**
> ServiceRequest addEventRefToServiceRequest(serviceRequestId, ServiceRequestDataRef)

Add event reference to service request

    Add event reference to service request

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **serviceRequestId** | **String**|  | [default to null] |
| **ServiceRequestDataRef** | [**ServiceRequestDataRef**](../Models/ServiceRequestDataRef.md)|  | |

### Return type

[**ServiceRequest**](../Models/ServiceRequest.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

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

[basicAuth](../README.md#basicAuth)

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
| **serviceRequestId** | **String**|  | [default to null] |

### Return type

null (empty response body)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

<a name="downloadServiceRequestAttachment"></a>
# **downloadServiceRequestAttachment**
> List downloadServiceRequestAttachment(serviceRequestId)

DOWNLOAD attachment for specific service request

    Download attachment from service request

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **serviceRequestId** | **String**|  | [default to null] |

### Return type

**List**

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/octet-stream

<a name="getServiceRequestById"></a>
# **getServiceRequestById**
> ServiceRequest getServiceRequestById(serviceRequestId)

GET service request by Id

    Returns service request by internal Id

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **serviceRequestId** | **String**|  | [default to null] |

### Return type

[**ServiceRequest**](../Models/ServiceRequest.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getServiceRequestList"></a>
# **getServiceRequestList**
> RequestListServiceRequest getServiceRequestList(sourceId, statusList, priorityList, all, pageSize, currentPage, orderBy, withTotalPages, type)

GET service request list

    Returns a list of all service requests in IoT Platform. Additional query parameters allow to filter that list. The default configuration will return all service requests which are not closed! With parameter all&#x3D;true, all service requests will be returned without fillter.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **sourceId** | **String**| Filter, returns all service request equal source Id | [optional] [default to null] |
| **statusList** | [**List**](../Models/String.md)| Filter, returns all service request with status defined in that list | [optional] [default to null] |
| **priorityList** | [**List**](../Models/Long.md)| Filter, returns all service request with priority defined in that list | [optional] [default to null] |
| **all** | **Boolean**| filter, \&quot;true\&quot; returns all service request, \&quot;false\&quot; (default) returns only active service requests. | [optional] [default to null] |
| **pageSize** | **Integer**| Indicates how many entries of the collection shall be returned. The upper limit for one page is 2,000 objects. | [optional] [default to null] |
| **currentPage** | **Integer**| The current page of the paginated results. | [optional] [default to null] |
| **orderBy** | [**List**](../Models/String.md)| OrderBy, orders list by status and/or priority and/or timestamp. | [optional] [default to null] |
| **withTotalPages** | **Boolean**| When set to true, the returned result will contain in the statistics object the total number of pages. Only applicable on range queries. | [optional] [default to null] |
| **type** | **String**| Filter, returns all service request of the type defined. Current supported types are: alarm, note, maintenance, downtime, other | [optional] [default to null] |

### Return type

[**RequestListServiceRequest**](../Models/RequestListServiceRequest.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="updateServiceRequestById"></a>
# **updateServiceRequestById**
> ServiceRequest updateServiceRequestById(serviceRequestId, ServiceRequestPatchRqBody)

PUT service request by Id

    Updates specific service request.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **serviceRequestId** | **String**|  | [default to null] |
| **ServiceRequestPatchRqBody** | [**ServiceRequestPatchRqBody**](../Models/ServiceRequestPatchRqBody.md)|  | |

### Return type

[**ServiceRequest**](../Models/ServiceRequest.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="uploadServiceRequestAttachment"></a>
# **uploadServiceRequestAttachment**
> uploadServiceRequestAttachment(serviceRequestId, file, force)

UPLOAD attachment for specific service request

    Upload attachment from service request

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **serviceRequestId** | **String**|  | [default to null] |
| **file** | **File**| Mulitpart file, attachment | [default to null] |
| **force** | **Boolean**| Controls if the attachment can be overwritten. force &#x3D;&#x3D; true means file will be overwritten if exists, otherwise a http 409 will be returned. | [default to null] |

### Return type

null (empty response body)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

