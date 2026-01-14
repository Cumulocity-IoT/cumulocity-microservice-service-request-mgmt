# ActiveServiceRequestCounterControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**refresh**](ActiveServiceRequestCounterControllerApi.md#refresh) | **POST** /api/service/request/device/sr_ActiveStatus/refresh | Triggers an asynchronous REFRESH of the active service request counter (sr_ActiveStatus) |


<a name="refresh"></a>
# **refresh**
> refresh(DeviceIds)

Triggers an asynchronous REFRESH of the active service request counter (sr_ActiveStatus)

    Refreshes the active service request counter (sr_ActiveStatus) for the given managed object IDs.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **DeviceIds** | [**DeviceIds**](../Models/DeviceIds.md)|  | |

### Return type

null (empty response body)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: Not defined

