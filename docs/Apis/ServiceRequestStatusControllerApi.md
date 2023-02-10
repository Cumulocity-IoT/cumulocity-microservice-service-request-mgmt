# ServiceRequestStatusControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createServiceRequestStatusList**](ServiceRequestStatusControllerApi.md#createServiceRequestStatusList) | **POST** /api/service/request/status/ | CREATE or UPDATE service request status list |
| [**deleteServiceRequestStatusById**](ServiceRequestStatusControllerApi.md#deleteServiceRequestStatusById) | **DELETE** /api/service/request/status/{statusId} | DELETE service request status by Id |
| [**getServiceRequestStatusById**](ServiceRequestStatusControllerApi.md#getServiceRequestStatusById) | **GET** /api/service/request/status/{statusId} | GET service request status by Id |
| [**getServiceRequestStatusList**](ServiceRequestStatusControllerApi.md#getServiceRequestStatusList) | **GET** /api/service/request/status/ | GET service request status list |


<a name="createServiceRequestStatusList"></a>
# **createServiceRequestStatusList**
> ServiceRequestStatus createServiceRequestStatusList(ServiceRequestStatus)

CREATE or UPDATE service request status list

    Creates or updates complete status list.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **ServiceRequestStatus** | [**List**](../Models/ServiceRequestStatus.md)|  | |

### Return type

[**ServiceRequestStatus**](../Models/ServiceRequestStatus.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="deleteServiceRequestStatusById"></a>
# **deleteServiceRequestStatusById**
> deleteServiceRequestStatusById(statusId)

DELETE service request status by Id

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **statusId** | **String**|  | [default to null] |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

<a name="getServiceRequestStatusById"></a>
# **getServiceRequestStatusById**
> ServiceRequestStatus getServiceRequestStatusById(statusId)

GET service request status by Id

    Returns specific service request status by Id

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **statusId** | **String**|  | [default to null] |

### Return type

[**ServiceRequestStatus**](../Models/ServiceRequestStatus.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getServiceRequestStatusList"></a>
# **getServiceRequestStatusList**
> List getServiceRequestStatusList()

GET service request status list

    Returns complete service request status list

### Parameters
This endpoint does not need any parameter.

### Return type

[**List**](../Models/ServiceRequestStatus.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

