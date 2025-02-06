# ServiceRequestCommentControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createServiceRequestComment**](ServiceRequestCommentControllerApi.md#createServiceRequestComment) | **POST** /api/service/request/{serviceRequestId}/comment | Add new service request comment to specific service request. |
| [**deleteServiceRequestCommentById**](ServiceRequestCommentControllerApi.md#deleteServiceRequestCommentById) | **DELETE** /api/service/request/comment/{commentId} | DELETE service request comment by Id |
| [**downloadServiceRequestCommentAttachment**](ServiceRequestCommentControllerApi.md#downloadServiceRequestCommentAttachment) | **GET** /api/service/request/comment/{commentId}/attachment | DOWNLOAD attachment for specific comment |
| [**getServiceRequestCommentList**](ServiceRequestCommentControllerApi.md#getServiceRequestCommentList) | **GET** /api/service/request/{serviceRequestId}/comment | Returns all comments of specific service request by internal Id. |
| [**patchServiceRequestCommentById**](ServiceRequestCommentControllerApi.md#patchServiceRequestCommentById) | **PUT** /api/service/request/comment/{commentId} | PUT service request comment by Id |
| [**uploadServiceRequestCommentAttachment**](ServiceRequestCommentControllerApi.md#uploadServiceRequestCommentAttachment) | **POST** /api/service/request/comment/{commentId}/attachment | UPLOAD attachment for specific comment |


<a name="createServiceRequestComment"></a>
# **createServiceRequestComment**
> ServiceRequestComment createServiceRequestComment(serviceRequestId, ServiceRequestCommentRqBody)

Add new service request comment to specific service request.

    Each service request can have n comments. This endpoint adds a new comment to a specific service request.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **serviceRequestId** | **String**|  | [default to null] |
| **ServiceRequestCommentRqBody** | [**ServiceRequestCommentRqBody**](../Models/ServiceRequestCommentRqBody.md)|  | |

### Return type

[**ServiceRequestComment**](../Models/ServiceRequestComment.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="deleteServiceRequestCommentById"></a>
# **deleteServiceRequestCommentById**
> deleteServiceRequestCommentById(commentId)

DELETE service request comment by Id

    deletes specific service request comment. This operation is only allowed by owner of comment!

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **commentId** | **String**|  | [default to null] |

### Return type

null (empty response body)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

<a name="downloadServiceRequestCommentAttachment"></a>
# **downloadServiceRequestCommentAttachment**
> List downloadServiceRequestCommentAttachment(commentId)

DOWNLOAD attachment for specific comment

    Download attachment for comment

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **commentId** | **String**|  | [default to null] |

### Return type

**List**

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/octet-stream

<a name="getServiceRequestCommentList"></a>
# **getServiceRequestCommentList**
> List getServiceRequestCommentList(serviceRequestId, pageSize, currentPage, withTotalPages)

Returns all comments of specific service request by internal Id.

    Each service request can have n comments. This endpoint returns the complete list of comments of a specific service request.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **serviceRequestId** | **String**|  | [default to null] |
| **pageSize** | **Integer**| Indicates how many entries of the collection shall be returned. The upper limit for one page is 2,000 objects. | [optional] [default to null] |
| **currentPage** | **Integer**| The current page of the paginated results. | [optional] [default to null] |
| **withTotalPages** | **Boolean**| When set to true, the returned result will contain in the statistics object the total number of pages. Only applicable on range queries. | [optional] [default to null] |

### Return type

[**List**](../Models/ServiceRequestComment.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="patchServiceRequestCommentById"></a>
# **patchServiceRequestCommentById**
> ServiceRequestComment patchServiceRequestCommentById(commentId, ServiceRequestCommentRqBody)

PUT service request comment by Id

    updates specific service request comment. This operation is only allowed by owner of comment!

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **commentId** | **String**|  | [default to null] |
| **ServiceRequestCommentRqBody** | [**ServiceRequestCommentRqBody**](../Models/ServiceRequestCommentRqBody.md)|  | |

### Return type

[**ServiceRequestComment**](../Models/ServiceRequestComment.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="uploadServiceRequestCommentAttachment"></a>
# **uploadServiceRequestCommentAttachment**
> uploadServiceRequestCommentAttachment(commentId, file, force)

UPLOAD attachment for specific comment

    Upload attachment for service request comment

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **commentId** | **String**|  | [default to null] |
| **file** | **File**| Mulitpart file, attachment | [default to null] |
| **force** | **Boolean**| Controls if the attachment can be overwritten. force &#x3D;&#x3D; true means file will be overwritten if exists, otherwise a http 409 will be returned. | [default to null] |

### Return type

null (empty response body)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

