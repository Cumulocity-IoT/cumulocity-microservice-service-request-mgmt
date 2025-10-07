# Documentation for Cumulocity Service Request API

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *http://localhost:8080*

| Class | Method | HTTP request | Description |
|------------ | ------------- | ------------- | -------------|
| *ContextConfigControllerApi* | [**createContextConfig**](Apis/ContextConfigControllerApi.md#createcontextconfig) | **POST** /api/context/config | CREATE context configuration |
*ContextConfigControllerApi* | [**deleteContextConfigById**](Apis/ContextConfigControllerApi.md#deletecontextconfigbyid) | **DELETE** /api/context/config/{configId} | DELETE context configuration by Id |
*ContextConfigControllerApi* | [**getContextConfigById**](Apis/ContextConfigControllerApi.md#getcontextconfigbyid) | **GET** /api/context/config/{configId} | GET context configuration by Id |
*ContextConfigControllerApi* | [**getContextConfigList**](Apis/ContextConfigControllerApi.md#getcontextconfiglist) | **GET** /api/context/config | GET all context configurations |
*ContextConfigControllerApi* | [**updateContextConfig**](Apis/ContextConfigControllerApi.md#updatecontextconfig) | **PUT** /api/context/config/{configId} | UPDATE context configuration |
| *ServiceRequestCommentControllerApi* | [**createServiceRequestComment**](Apis/ServiceRequestCommentControllerApi.md#createservicerequestcomment) | **POST** /api/service/request/{serviceRequestId}/comment | Add new service request comment to specific service request. |
*ServiceRequestCommentControllerApi* | [**deleteServiceRequestCommentById**](Apis/ServiceRequestCommentControllerApi.md#deleteservicerequestcommentbyid) | **DELETE** /api/service/request/comment/{commentId} | DELETE service request comment by Id |
*ServiceRequestCommentControllerApi* | [**downloadServiceRequestCommentAttachment**](Apis/ServiceRequestCommentControllerApi.md#downloadservicerequestcommentattachment) | **GET** /api/service/request/comment/{commentId}/attachment | DOWNLOAD attachment for specific comment |
*ServiceRequestCommentControllerApi* | [**getServiceRequestCommentList**](Apis/ServiceRequestCommentControllerApi.md#getservicerequestcommentlist) | **GET** /api/service/request/{serviceRequestId}/comment | Returns all comments of specific service request by internal Id. |
*ServiceRequestCommentControllerApi* | [**patchServiceRequestCommentById**](Apis/ServiceRequestCommentControllerApi.md#patchservicerequestcommentbyid) | **PUT** /api/service/request/comment/{commentId} | PUT service request comment by Id |
*ServiceRequestCommentControllerApi* | [**uploadServiceRequestCommentAttachment**](Apis/ServiceRequestCommentControllerApi.md#uploadservicerequestcommentattachment) | **POST** /api/service/request/comment/{commentId}/attachment | UPLOAD attachment for specific comment |
| *ServiceRequestControllerApi* | [**addAlarmRefToServiceRequest**](Apis/ServiceRequestControllerApi.md#addalarmreftoservicerequest) | **PUT** /api/service/request/{serviceRequestId}/alarm | Add alarm reference to service request |
*ServiceRequestControllerApi* | [**addEventRefToServiceRequest**](Apis/ServiceRequestControllerApi.md#addeventreftoservicerequest) | **PUT** /api/service/request/{serviceRequestId}/event | Add event reference to service request |
*ServiceRequestControllerApi* | [**createServiceRequest**](Apis/ServiceRequestControllerApi.md#createservicerequest) | **POST** /api/service/request | CREATE service request |
*ServiceRequestControllerApi* | [**deleteServiceRequestById**](Apis/ServiceRequestControllerApi.md#deleteservicerequestbyid) | **DELETE** /api/service/request/{serviceRequestId} | DELETE service request by Id |
*ServiceRequestControllerApi* | [**downloadServiceRequestAttachment**](Apis/ServiceRequestControllerApi.md#downloadservicerequestattachment) | **GET** /api/service/request/{serviceRequestId}/attachment | DOWNLOAD attachment for specific service request |
*ServiceRequestControllerApi* | [**getServiceRequestById**](Apis/ServiceRequestControllerApi.md#getservicerequestbyid) | **GET** /api/service/request/{serviceRequestId} | GET service request by Id |
*ServiceRequestControllerApi* | [**getServiceRequestList**](Apis/ServiceRequestControllerApi.md#getservicerequestlist) | **GET** /api/service/request | GET service request list |
*ServiceRequestControllerApi* | [**updateServiceRequestById**](Apis/ServiceRequestControllerApi.md#updateservicerequestbyid) | **PUT** /api/service/request/{serviceRequestId} | PUT service request by Id |
*ServiceRequestControllerApi* | [**uploadServiceRequestAttachment**](Apis/ServiceRequestControllerApi.md#uploadservicerequestattachment) | **POST** /api/service/request/{serviceRequestId}/attachment | UPLOAD attachment for specific service request |
| *ServiceRequestExternalControllerApi* | [**getServiceRequestCommentList1**](Apis/ServiceRequestExternalControllerApi.md#getservicerequestcommentlist1) | **GET** /api/adapter/service/request/{serviceRequestId}/comment | Returns all user comments of specific service request by internal Id. |
*ServiceRequestExternalControllerApi* | [**getServiceRequestList1**](Apis/ServiceRequestExternalControllerApi.md#getservicerequestlist1) | **GET** /api/adapter/service/request | GET service request list |
*ServiceRequestExternalControllerApi* | [**updateServiceRequestIsActiveById**](Apis/ServiceRequestExternalControllerApi.md#updateservicerequestisactivebyid) | **PUT** /api/adapter/service/request/{serviceRequestId}/active | UPDATE service request active status by Id |
*ServiceRequestExternalControllerApi* | [**updateServiceRequestStatusById**](Apis/ServiceRequestExternalControllerApi.md#updateservicerequeststatusbyid) | **PUT** /api/adapter/service/request/{serviceRequestId}/status | UPDATE service request status by Id |
| *ServiceRequestPriorityControllerApi* | [**createServiceRequestPriorityList**](Apis/ServiceRequestPriorityControllerApi.md#createservicerequestprioritylist) | **POST** /api/service/request/priority | CREATE or UPDATE complete priority list |
*ServiceRequestPriorityControllerApi* | [**deleteServiceRequestpriorityById**](Apis/ServiceRequestPriorityControllerApi.md#deleteservicerequestprioritybyid) | **DELETE** /api/service/request/priority/{priorityOrdinal} | DELETE service request priority |
*ServiceRequestPriorityControllerApi* | [**getServiceRequestPriorityList**](Apis/ServiceRequestPriorityControllerApi.md#getservicerequestprioritylist) | **GET** /api/service/request/priority | GET service request priority list |
*ServiceRequestPriorityControllerApi* | [**getServiceRequestpriorityById**](Apis/ServiceRequestPriorityControllerApi.md#getservicerequestprioritybyid) | **GET** /api/service/request/priority/{priorityOrdinal} | GET service request priority by ordinal |
| *ServiceRequestStatusConfigControllerApi* | [**createServiceRequestStatusConfigList**](Apis/ServiceRequestStatusConfigControllerApi.md#createservicerequeststatusconfiglist) | **POST** /api/service/request/status | CREATE or UPDATE service request status list |
*ServiceRequestStatusConfigControllerApi* | [**deleteServiceRequestStatusConfigById**](Apis/ServiceRequestStatusConfigControllerApi.md#deleteservicerequeststatusconfigbyid) | **DELETE** /api/service/request/status/{statusId} | DELETE service request status by Id |
*ServiceRequestStatusConfigControllerApi* | [**getServiceRequestStatusConfigById**](Apis/ServiceRequestStatusConfigControllerApi.md#getservicerequeststatusconfigbyid) | **GET** /api/service/request/status/{statusId} | GET service request status by Id |
*ServiceRequestStatusConfigControllerApi* | [**getServiceRequestStatusConfigList**](Apis/ServiceRequestStatusConfigControllerApi.md#getservicerequeststatusconfiglist) | **GET** /api/service/request/status | GET service request status list |


<a name="documentation-for-models"></a>
## Documentation for Models

 - [ContextApplyRules](./Models/ContextApplyRules.md)
 - [ContextConfig](./Models/ContextConfig.md)
 - [ContextPredicate](./Models/ContextPredicate.md)
 - [ContextSettings](./Models/ContextSettings.md)
 - [ErrorResponseBody](./Models/ErrorResponseBody.md)
 - [RequestListServiceRequest](./Models/RequestListServiceRequest.md)
 - [RequestListServiceRequestComment](./Models/RequestListServiceRequestComment.md)
 - [ServiceOrder](./Models/ServiceOrder.md)
 - [ServiceRequest](./Models/ServiceRequest.md)
 - [ServiceRequestAttachment](./Models/ServiceRequestAttachment.md)
 - [ServiceRequestComment](./Models/ServiceRequestComment.md)
 - [ServiceRequestCommentRqBody](./Models/ServiceRequestCommentRqBody.md)
 - [ServiceRequestDataRef](./Models/ServiceRequestDataRef.md)
 - [ServiceRequestPatchRqBody](./Models/ServiceRequestPatchRqBody.md)
 - [ServiceRequestPostRqBody](./Models/ServiceRequestPostRqBody.md)
 - [ServiceRequestPriority](./Models/ServiceRequestPriority.md)
 - [ServiceRequestSource](./Models/ServiceRequestSource.md)
 - [ServiceRequestStatus](./Models/ServiceRequestStatus.md)
 - [ServiceRequestStatusConfig](./Models/ServiceRequestStatusConfig.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

<a name="basicAuth"></a>
### basicAuth

- **Type**: HTTP basic authentication

