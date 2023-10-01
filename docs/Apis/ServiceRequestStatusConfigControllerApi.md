# ServiceRequestStatusConfigControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createServiceRequestStatusConfigList**](ServiceRequestStatusConfigControllerApi.md#createServiceRequestStatusConfigList) | **POST** /api/service/request/status | CREATE or UPDATE service request status list |
| [**deleteServiceRequestStatusConfigById**](ServiceRequestStatusConfigControllerApi.md#deleteServiceRequestStatusConfigById) | **DELETE** /api/service/request/status/{statusId} | DELETE service request status by Id |
| [**getServiceRequestStatusConfigById**](ServiceRequestStatusConfigControllerApi.md#getServiceRequestStatusConfigById) | **GET** /api/service/request/status/{statusId} | GET service request status by Id |
| [**getServiceRequestStatusConfigList**](ServiceRequestStatusConfigControllerApi.md#getServiceRequestStatusConfigList) | **GET** /api/service/request/status | GET service request status list |


<a name="createServiceRequestStatusConfigList"></a>
# **createServiceRequestStatusConfigList**
> ServiceRequestStatusConfig createServiceRequestStatusConfigList(ServiceRequestStatusConfig)

CREATE or UPDATE service request status list

    Creates or updates complete status list.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **ServiceRequestStatusConfig** | [**List**](../Models/ServiceRequestStatusConfig.md)|  | |

### Return type

[**ServiceRequestStatusConfig**](../Models/ServiceRequestStatusConfig.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="deleteServiceRequestStatusConfigById"></a>
# **deleteServiceRequestStatusConfigById**
> deleteServiceRequestStatusConfigById(statusId)

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

<a name="getServiceRequestStatusConfigById"></a>
# **getServiceRequestStatusConfigById**
> ServiceRequestStatusConfig getServiceRequestStatusConfigById(statusId)

GET service request status by Id

    Returns specific service request status by Id

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **statusId** | **String**|  | [default to null] |

### Return type

[**ServiceRequestStatusConfig**](../Models/ServiceRequestStatusConfig.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getServiceRequestStatusConfigList"></a>
# **getServiceRequestStatusConfigList**
> List getServiceRequestStatusConfigList()

GET service request status list

    Returns complete service request status list

### Parameters
This endpoint does not need any parameter.

### Return type

[**List**](../Models/ServiceRequestStatusConfig.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

