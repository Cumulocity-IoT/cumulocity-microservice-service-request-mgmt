# ServiceRequestCommentControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createServiceRequestComment**](ServiceRequestCommentControllerApi.md#createServiceRequestComment) | **POST** /api/service/request/{serviceRequestId}/comment | Add new service request comment to specific service request. |
| [**deleteServiceRequestCommentById**](ServiceRequestCommentControllerApi.md#deleteServiceRequestCommentById) | **DELETE** /api/service/request/comment/{commentId} | DELETE service request comment by Id |
| [**getServiceRequestCommentList**](ServiceRequestCommentControllerApi.md#getServiceRequestCommentList) | **GET** /api/service/request/{serviceRequestId}/comment | Returns all comments of specific service request by internal Id. |
| [**patchServiceRequestCommentById**](ServiceRequestCommentControllerApi.md#patchServiceRequestCommentById) | **PUT** /api/service/request/comment/{commentId} | PATCH service request comment by Id |


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

No authorization required

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

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

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

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="patchServiceRequestCommentById"></a>
# **patchServiceRequestCommentById**
> ServiceRequestComment patchServiceRequestCommentById(commentId, ServiceRequestComment)

PATCH service request comment by Id

    updates specific service request comment. This operation is only allowed by owner of comment!

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **commentId** | **String**|  | [default to null] |
| **ServiceRequestComment** | [**ServiceRequestComment**](../Models/ServiceRequestComment.md)|  | |

### Return type

[**ServiceRequestComment**](../Models/ServiceRequestComment.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

