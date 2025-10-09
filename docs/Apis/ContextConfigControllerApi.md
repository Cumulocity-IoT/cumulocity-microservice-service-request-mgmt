# ContextConfigControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**applyContextConfigsToAlarm**](ContextConfigControllerApi.md#applyContextConfigsToAlarm) | **POST** /api/context/config/apply/alarm/{alarmId} | APPLY context configurations to alarm |
| [**createContextConfig**](ContextConfigControllerApi.md#createContextConfig) | **POST** /api/context/config | CREATE context configuration |
| [**deleteContextConfigById**](ContextConfigControllerApi.md#deleteContextConfigById) | **DELETE** /api/context/config/{configId} | DELETE context configuration by Id |
| [**getContextConfigById**](ContextConfigControllerApi.md#getContextConfigById) | **GET** /api/context/config/{configId} | GET context configuration by Id |
| [**getContextConfigList**](ContextConfigControllerApi.md#getContextConfigList) | **GET** /api/context/config | GET all context configurations |
| [**updateContextConfig**](ContextConfigControllerApi.md#updateContextConfig) | **PUT** /api/context/config/{configId} | UPDATE context configuration |


<a name="applyContextConfigsToAlarm"></a>
# **applyContextConfigsToAlarm**
> ContextData applyContextConfigsToAlarm(alarmId)

APPLY context configurations to alarm

    Applies all matching context configurations to a specific alarm by alarm ID.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **alarmId** | **String**|  | [default to null] |

### Return type

[**ContextData**](../Models/ContextData.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="createContextConfig"></a>
# **createContextConfig**
> ContextConfig createContextConfig(ContextConfig)

CREATE context configuration

    Creates a new context configuration for alarm data context.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **ContextConfig** | [**ContextConfig**](../Models/ContextConfig.md)|  | |

### Return type

[**ContextConfig**](../Models/ContextConfig.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="deleteContextConfigById"></a>
# **deleteContextConfigById**
> deleteContextConfigById(configId)

DELETE context configuration by Id

    Deletes a context configuration by Id

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **configId** | **String**|  | [default to null] |

### Return type

null (empty response body)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

<a name="getContextConfigById"></a>
# **getContextConfigById**
> ContextConfig getContextConfigById(configId)

GET context configuration by Id

    Returns specific context configuration by Id

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **configId** | **String**|  | [default to null] |

### Return type

[**ContextConfig**](../Models/ContextConfig.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getContextConfigList"></a>
# **getContextConfigList**
> List getContextConfigList()

GET all context configurations

    Returns complete list of context configurations

### Parameters
This endpoint does not need any parameter.

### Return type

[**List**](../Models/ContextConfig.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="updateContextConfig"></a>
# **updateContextConfig**
> ContextConfig updateContextConfig(configId, ContextConfig)

UPDATE context configuration

    Updates an existing context configuration.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **configId** | **String**|  | [default to null] |
| **ContextConfig** | [**ContextConfig**](../Models/ContextConfig.md)|  | |

### Return type

[**ContextConfig**](../Models/ContextConfig.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

