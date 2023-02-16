# ServiceObjectControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createServiceObject**](ServiceObjectControllerApi.md#createServiceObject) | **POST** /api/service/object | Add new service object. |
| [**getServiceObjecttById**](ServiceObjectControllerApi.md#getServiceObjecttById) | **GET** /api/service/object/{serviceObjectId} | GET service object by Id |


<a name="createServiceObject"></a>
# **createServiceObject**
> ServiceObject createServiceObject(ServiceObject)

Add new service object.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **ServiceObject** | [**ServiceObject**](../Models/ServiceObject.md)|  | |

### Return type

[**ServiceObject**](../Models/ServiceObject.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="getServiceObjecttById"></a>
# **getServiceObjecttById**
> ServiceObject getServiceObjecttById(serviceObjectId)

GET service object by Id

    Returns service object by internal Id

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **serviceObjectId** | **Long**|  | [default to null] |

### Return type

[**ServiceObject**](../Models/ServiceObject.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

