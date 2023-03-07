# Documentation for OpenAPI definition

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *http://localhost:8080*

| Class | Method | HTTP request | Description |
|------------ | ------------- | ------------- | -------------|
| *ServiceObjectControllerApi* | [**createServiceObject**](Apis/ServiceObjectControllerApi.md#createserviceobject) | **POST** /api/service/object | Add new service object. |
*ServiceObjectControllerApi* | [**getServiceObjecttById**](Apis/ServiceObjectControllerApi.md#getserviceobjecttbyid) | **GET** /api/service/object/{serviceObjectId} | GET service object by Id |
| *ServiceRequestCommentControllerApi* | [**createServiceRequestComment**](Apis/ServiceRequestCommentControllerApi.md#createservicerequestcomment) | **POST** /api/service/request/{serviceRequestId}/comment | Add new service request comment to specific service request. |
*ServiceRequestCommentControllerApi* | [**deleteServiceRequestCommentById**](Apis/ServiceRequestCommentControllerApi.md#deleteservicerequestcommentbyid) | **DELETE** /api/service/request/comment/{commentId} | DELETE service request comment by Id |
*ServiceRequestCommentControllerApi* | [**getServiceRequestCommentList**](Apis/ServiceRequestCommentControllerApi.md#getservicerequestcommentlist) | **GET** /api/service/request/{serviceRequestId}/comment | Returns all comments of specific service request by internal Id. |
*ServiceRequestCommentControllerApi* | [**patchServiceRequestCommentById**](Apis/ServiceRequestCommentControllerApi.md#patchservicerequestcommentbyid) | **PUT** /api/service/request/comment/{commentId} | PUT service request comment by Id |
| *ServiceRequestControllerApi* | [**createServiceRequest**](Apis/ServiceRequestControllerApi.md#createservicerequest) | **POST** /api/service/request | CREATE service request |
*ServiceRequestControllerApi* | [**deleteServiceRequestById**](Apis/ServiceRequestControllerApi.md#deleteservicerequestbyid) | **DELETE** /api/service/request/{serviceRequestId} | DELETE service request by Id |
*ServiceRequestControllerApi* | [**downloadServiceRequestAttachment**](Apis/ServiceRequestControllerApi.md#downloadservicerequestattachment) | **GET** /api/service/request/{serviceRequestId}/attachment | DOWNLOAD attachment for specific service request |
*ServiceRequestControllerApi* | [**getServiceRequestById**](Apis/ServiceRequestControllerApi.md#getservicerequestbyid) | **GET** /api/service/request/{serviceRequestId} | GET service request by Id |
*ServiceRequestControllerApi* | [**getServiceRequestList**](Apis/ServiceRequestControllerApi.md#getservicerequestlist) | **GET** /api/service/request | GET service request list |
*ServiceRequestControllerApi* | [**updateServiceRequestById**](Apis/ServiceRequestControllerApi.md#updateservicerequestbyid) | **PUT** /api/service/request/{serviceRequestId} | PUT service request by Id |
*ServiceRequestControllerApi* | [**uploadServiceRequestAttachment**](Apis/ServiceRequestControllerApi.md#uploadservicerequestattachment) | **POST** /api/service/request/{serviceRequestId}/attachment | UPLOAD attachment for specific service request |
| *ServiceRequestExternalControllerApi* | [**getServiceRequestByExternalId**](Apis/ServiceRequestExternalControllerApi.md#getservicerequestbyexternalid) | **GET** /api/service/request/external/{serviceRequestExternalId} | GET service request by external Id |
*ServiceRequestExternalControllerApi* | [**syncServiceRequest**](Apis/ServiceRequestExternalControllerApi.md#syncservicerequest) | **POST** /api/service/request/external | SYNC service request into external object |
*ServiceRequestExternalControllerApi* | [**updateServiceRequestByExternalId**](Apis/ServiceRequestExternalControllerApi.md#updateservicerequestbyexternalid) | **PUT** /api/service/request/external/{serviceRequestExternalId} | UPDATE service request by external Id |
| *ServiceRequestPriorityControllerApi* | [**createServiceRequestPriorityList**](Apis/ServiceRequestPriorityControllerApi.md#createservicerequestprioritylist) | **POST** /api/service/request/priority | CREATE or UPDATE complete priority list |
*ServiceRequestPriorityControllerApi* | [**deleteServiceRequestpriorityById**](Apis/ServiceRequestPriorityControllerApi.md#deleteservicerequestprioritybyid) | **DELETE** /api/service/request/priority/{priorityOrdinal} | DELETE service request priority |
*ServiceRequestPriorityControllerApi* | [**getServiceRequestPriorityList**](Apis/ServiceRequestPriorityControllerApi.md#getservicerequestprioritylist) | **GET** /api/service/request/priority | GET service request priority list |
*ServiceRequestPriorityControllerApi* | [**getServiceRequestpriorityById**](Apis/ServiceRequestPriorityControllerApi.md#getservicerequestprioritybyid) | **GET** /api/service/request/priority/{priorityOrdinal} | GET service request priority by ordinal |
| *ServiceRequestStatusControllerApi* | [**createServiceRequestStatusList**](Apis/ServiceRequestStatusControllerApi.md#createservicerequeststatuslist) | **POST** /api/service/request/status | CREATE or UPDATE service request status list |
*ServiceRequestStatusControllerApi* | [**deleteServiceRequestStatusById**](Apis/ServiceRequestStatusControllerApi.md#deleteservicerequeststatusbyid) | **DELETE** /api/service/request/status/{statusId} | DELETE service request status by Id |
*ServiceRequestStatusControllerApi* | [**getServiceRequestStatusById**](Apis/ServiceRequestStatusControllerApi.md#getservicerequeststatusbyid) | **GET** /api/service/request/status/{statusId} | GET service request status by Id |
*ServiceRequestStatusControllerApi* | [**getServiceRequestStatusList**](Apis/ServiceRequestStatusControllerApi.md#getservicerequeststatuslist) | **GET** /api/service/request/status | GET service request status list |


<a name="documentation-for-models"></a>
## Documentation for Models

 - [RequestListServiceRequest](./Models/RequestListServiceRequest.md)
 - [RequestListServiceRequestComment](./Models/RequestListServiceRequestComment.md)
 - [ServiceObject](./Models/ServiceObject.md)
 - [ServiceRequest](./Models/ServiceRequest.md)
 - [ServiceRequestAttachment](./Models/ServiceRequestAttachment.md)
 - [ServiceRequestComment](./Models/ServiceRequestComment.md)
 - [ServiceRequestCommentRqBody](./Models/ServiceRequestCommentRqBody.md)
 - [ServiceRequestDataRef](./Models/ServiceRequestDataRef.md)
 - [ServiceRequestPatchRqBody](./Models/ServiceRequestPatchRqBody.md)
 - [ServiceRequestPostRqBody](./Models/ServiceRequestPostRqBody.md)
 - [ServiceRequestPriority](./Models/ServiceRequestPriority.md)
 - [ServiceRequestRef](./Models/ServiceRequestRef.md)
 - [ServiceRequestSource](./Models/ServiceRequestSource.md)
 - [ServiceRequestStatus](./Models/ServiceRequestStatus.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

All endpoints do not require authorization.
