# Documentation for Cumulocity Service Request API

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *http://localhost:8080*

| Class | Method | HTTP request | Description |
|------------ | ------------- | ------------- | -------------|
| *ActiveServiceRequestCounterControllerApi* | [**refresh**](Apis/ActiveServiceRequestCounterControllerApi.md#refresh) | **POST** /api/service/request/device/sr_ActiveStatus/refresh | Triggers an asynchronous REFRESH of the active service request counter (sr_ActiveStatus) |
| *ContextConfigControllerApi* | [**applyContextConfigsToAlarm**](Apis/ContextConfigControllerApi.md#applyContextConfigsToAlarm) | **POST** /api/context/config/apply/alarm/{alarmId} | APPLY context configurations to alarm |
*ContextConfigControllerApi* | [**createContextConfig**](Apis/ContextConfigControllerApi.md#createContextConfig) | **POST** /api/context/config | CREATE context configuration |
*ContextConfigControllerApi* | [**deleteContextConfigById**](Apis/ContextConfigControllerApi.md#deleteContextConfigById) | **DELETE** /api/context/config/{configId} | DELETE context configuration by Id |
*ContextConfigControllerApi* | [**getContextConfigById**](Apis/ContextConfigControllerApi.md#getContextConfigById) | **GET** /api/context/config/{configId} | GET context configuration by Id |
*ContextConfigControllerApi* | [**getContextConfigList**](Apis/ContextConfigControllerApi.md#getContextConfigList) | **GET** /api/context/config | GET all context configurations |
*ContextConfigControllerApi* | [**updateContextConfig**](Apis/ContextConfigControllerApi.md#updateContextConfig) | **PUT** /api/context/config/{configId} | UPDATE context configuration |
| *ServiceRequestCommentControllerApi* | [**createServiceRequestComment**](Apis/ServiceRequestCommentControllerApi.md#createServiceRequestComment) | **POST** /api/service/request/{serviceRequestId}/comment | Add new service request comment to specific service request. |
*ServiceRequestCommentControllerApi* | [**deleteServiceRequestCommentById**](Apis/ServiceRequestCommentControllerApi.md#deleteServiceRequestCommentById) | **DELETE** /api/service/request/comment/{commentId} | DELETE service request comment by Id |
*ServiceRequestCommentControllerApi* | [**downloadServiceRequestCommentAttachment**](Apis/ServiceRequestCommentControllerApi.md#downloadServiceRequestCommentAttachment) | **GET** /api/service/request/comment/{commentId}/attachment | DOWNLOAD attachment for specific comment |
*ServiceRequestCommentControllerApi* | [**getServiceRequestCommentList**](Apis/ServiceRequestCommentControllerApi.md#getServiceRequestCommentList) | **GET** /api/service/request/{serviceRequestId}/comment | Returns all comments of specific service request by internal Id. |
*ServiceRequestCommentControllerApi* | [**patchServiceRequestCommentById**](Apis/ServiceRequestCommentControllerApi.md#patchServiceRequestCommentById) | **PUT** /api/service/request/comment/{commentId} | PUT service request comment by Id |
*ServiceRequestCommentControllerApi* | [**uploadServiceRequestCommentAttachment**](Apis/ServiceRequestCommentControllerApi.md#uploadServiceRequestCommentAttachment) | **POST** /api/service/request/comment/{commentId}/attachment | UPLOAD attachment for specific comment |
| *ServiceRequestControllerApi* | [**addAlarmRefToServiceRequest**](Apis/ServiceRequestControllerApi.md#addAlarmRefToServiceRequest) | **PUT** /api/service/request/{serviceRequestId}/alarm | Add alarm reference to service request |
*ServiceRequestControllerApi* | [**addEventRefToServiceRequest**](Apis/ServiceRequestControllerApi.md#addEventRefToServiceRequest) | **PUT** /api/service/request/{serviceRequestId}/event | Add event reference to service request |
*ServiceRequestControllerApi* | [**createServiceRequest**](Apis/ServiceRequestControllerApi.md#createServiceRequest) | **POST** /api/service/request | CREATE service request |
*ServiceRequestControllerApi* | [**deleteServiceRequestById**](Apis/ServiceRequestControllerApi.md#deleteServiceRequestById) | **DELETE** /api/service/request/{serviceRequestId} | DELETE service request by Id |
*ServiceRequestControllerApi* | [**downloadServiceRequestAttachment**](Apis/ServiceRequestControllerApi.md#downloadServiceRequestAttachment) | **GET** /api/service/request/{serviceRequestId}/attachment | DOWNLOAD attachment for specific service request |
*ServiceRequestControllerApi* | [**getServiceRequestById**](Apis/ServiceRequestControllerApi.md#getServiceRequestById) | **GET** /api/service/request/{serviceRequestId} | GET service request by Id |
*ServiceRequestControllerApi* | [**getServiceRequestList**](Apis/ServiceRequestControllerApi.md#getServiceRequestList) | **GET** /api/service/request | GET service request list |
*ServiceRequestControllerApi* | [**updateServiceRequestById**](Apis/ServiceRequestControllerApi.md#updateServiceRequestById) | **PUT** /api/service/request/{serviceRequestId} | PUT service request by Id |
*ServiceRequestControllerApi* | [**uploadServiceRequestAttachment**](Apis/ServiceRequestControllerApi.md#uploadServiceRequestAttachment) | **POST** /api/service/request/{serviceRequestId}/attachment | UPLOAD attachment for specific service request |
| *ServiceRequestExternalControllerApi* | [**getServiceRequestCommentList1**](Apis/ServiceRequestExternalControllerApi.md#getServiceRequestCommentList1) | **GET** /api/adapter/service/request/{serviceRequestId}/comment | Returns all user comments of specific service request by internal Id. |
*ServiceRequestExternalControllerApi* | [**getServiceRequestList1**](Apis/ServiceRequestExternalControllerApi.md#getServiceRequestList1) | **GET** /api/adapter/service/request | GET service request list |
*ServiceRequestExternalControllerApi* | [**updateServiceRequestById1**](Apis/ServiceRequestExternalControllerApi.md#updateServiceRequestById1) | **PUT** /api/adapter/service/request/{serviceRequestId} | PUT service request by Id |
*ServiceRequestExternalControllerApi* | [**updateServiceRequestIsActiveById**](Apis/ServiceRequestExternalControllerApi.md#updateServiceRequestIsActiveById) | **PUT** /api/adapter/service/request/{serviceRequestId}/active | UPDATE service request active status by Id |
*ServiceRequestExternalControllerApi* | [**updateServiceRequestStatusById**](Apis/ServiceRequestExternalControllerApi.md#updateServiceRequestStatusById) | **PUT** /api/adapter/service/request/{serviceRequestId}/status | UPDATE service request status by Id |
| *ServiceRequestPriorityControllerApi* | [**createServiceRequestPriorityList**](Apis/ServiceRequestPriorityControllerApi.md#createServiceRequestPriorityList) | **POST** /api/service/request/priority | CREATE or UPDATE complete priority list |
*ServiceRequestPriorityControllerApi* | [**deleteServiceRequestpriorityById**](Apis/ServiceRequestPriorityControllerApi.md#deleteServiceRequestpriorityById) | **DELETE** /api/service/request/priority/{priorityOrdinal} | DELETE service request priority |
*ServiceRequestPriorityControllerApi* | [**getServiceRequestPriorityList**](Apis/ServiceRequestPriorityControllerApi.md#getServiceRequestPriorityList) | **GET** /api/service/request/priority | GET service request priority list |
*ServiceRequestPriorityControllerApi* | [**getServiceRequestpriorityById**](Apis/ServiceRequestPriorityControllerApi.md#getServiceRequestpriorityById) | **GET** /api/service/request/priority/{priorityOrdinal} | GET service request priority by ordinal |
| *ServiceRequestStatusConfigControllerApi* | [**createServiceRequestStatusConfigList**](Apis/ServiceRequestStatusConfigControllerApi.md#createServiceRequestStatusConfigList) | **POST** /api/service/request/status | CREATE or UPDATE service request status list |
*ServiceRequestStatusConfigControllerApi* | [**deleteServiceRequestStatusConfigById**](Apis/ServiceRequestStatusConfigControllerApi.md#deleteServiceRequestStatusConfigById) | **DELETE** /api/service/request/status/{statusId} | DELETE service request status by Id |
*ServiceRequestStatusConfigControllerApi* | [**getServiceRequestStatusConfigById**](Apis/ServiceRequestStatusConfigControllerApi.md#getServiceRequestStatusConfigById) | **GET** /api/service/request/status/{statusId} | GET service request status by Id |
*ServiceRequestStatusConfigControllerApi* | [**getServiceRequestStatusConfigList**](Apis/ServiceRequestStatusConfigControllerApi.md#getServiceRequestStatusConfigList) | **GET** /api/service/request/status | GET service request status list |


<a name="documentation-for-models"></a>
## Documentation for Models

 - [ContextApplyRules](./Models/ContextApplyRules.md)
 - [ContextConfig](./Models/ContextConfig.md)
 - [ContextData](./Models/ContextData.md)
 - [ContextPredicate](./Models/ContextPredicate.md)
 - [ContextSettings](./Models/ContextSettings.md)
 - [DeviceIds](./Models/DeviceIds.md)
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

