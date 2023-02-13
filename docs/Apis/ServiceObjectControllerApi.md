# ServiceObjectControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createServiceRequestCommentList1**](ServiceObjectControllerApi.md#createServiceRequestCommentList1) | **POST** /api/service/object/ | Add new service object. |
| [**getServiceObjecttById**](ServiceObjectControllerApi.md#getServiceObjecttById) | **GET** /api/service/object/{serviceObjectId} | GET service object by Id |


<a name="createServiceRequestCommentList1"></a>
# **createServiceRequestCommentList1**
> ServiceObject createServiceRequestCommentList1(ServiceObject)

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

