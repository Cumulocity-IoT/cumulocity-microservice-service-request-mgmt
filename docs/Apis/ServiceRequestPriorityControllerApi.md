# ServiceRequestPriorityControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createServiceRequestPriorityList**](ServiceRequestPriorityControllerApi.md#createServiceRequestPriorityList) | **POST** /api/service/request/priority | CREATE or UPDATE complete priority list |
| [**deleteServiceRequestpriorityById**](ServiceRequestPriorityControllerApi.md#deleteServiceRequestpriorityById) | **DELETE** /api/service/request/priority/{priorityOrdinal} | DELETE service request priority |
| [**getServiceRequestPriorityList**](ServiceRequestPriorityControllerApi.md#getServiceRequestPriorityList) | **GET** /api/service/request/priority | GET service request priority list |
| [**getServiceRequestpriorityById**](ServiceRequestPriorityControllerApi.md#getServiceRequestpriorityById) | **GET** /api/service/request/priority/{priorityOrdinal} | GET service request priority by ordinal |


<a name="createServiceRequestPriorityList"></a>
# **createServiceRequestPriorityList**
> List createServiceRequestPriorityList(ServiceRequestPriority)

CREATE or UPDATE complete priority list

    Creates or updates complete priority list.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **ServiceRequestPriority** | [**List**](../Models/ServiceRequestPriority.md)|  | |

### Return type

[**List**](../Models/ServiceRequestPriority.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="deleteServiceRequestpriorityById"></a>
# **deleteServiceRequestpriorityById**
> deleteServiceRequestpriorityById(priorityOrdinal)

DELETE service request priority

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **priorityOrdinal** | **Long**|  | [default to null] |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

<a name="getServiceRequestPriorityList"></a>
# **getServiceRequestPriorityList**
> List getServiceRequestPriorityList()

GET service request priority list

    Returns complete list of priorities which are available.

### Parameters
This endpoint does not need any parameter.

### Return type

[**List**](../Models/ServiceRequestPriority.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getServiceRequestpriorityById"></a>
# **getServiceRequestpriorityById**
> ServiceRequestPriority getServiceRequestpriorityById(priorityOrdinal)

GET service request priority by ordinal

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **priorityOrdinal** | **Long**|  | [default to null] |

### Return type

[**ServiceRequestPriority**](../Models/ServiceRequestPriority.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

