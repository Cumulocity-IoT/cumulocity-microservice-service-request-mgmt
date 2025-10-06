# **Feature Request: Alarm/Event Data Context for Data Explorer**

Feature: Alarm Data Context for Data Explorer  
Status: New Request  
Date: September 19, 2025  
Requester: Alexander Pester  
Customer: Enercon

### **1\. User Story**

As an Enercon service user,  
I want to save, reload and share specific data context for Data Explorer (including data points, event/alarm types, and a time window) that is linked / related to an alarm in terms of time  
so that I can quickly recall the exact context and data surrounding that alarm for faster analysis, troubleshooting, and reporting without having to manually reconfigure the view each time.

### **2\. Feature Description & Scope**

#### **2.1. Core Concept**

This feature introduces the concept of a "Data Context" for the Data Explorer. A Context is a saved, immutable configuration of the Data Explorer's state that is tied to a specific alarm instance. When the device or Cumulocity creates a significant alarm, the user should be able to create an alarm context. This context captures the complete analytical view relevant to that alarm.

Side note:

Enercon considered materializing a complete context as a snapshot of relevant data to create an immutable copy of potentially changing data, such as inventory objects, alarm statuses, and operations. However, this approach was ultimately not pursued because the immutability was only deemed necessary for unchanging data like events and measurements.

#### **2.2. Global Context Configuration**

A context configuration is a global configuration which defines:

* **Data Points:** The exact list of measurements/data series that are relevant for the context at the time of creation of the alarm.  
* **Event Types:** The exact list of event types that are relevant for the context at the time of creation of the alarm.  
* **Alarm Types (optional):** The exact list of alarms that are relevant for the context at the time of creation of the alarm.  
* **Time Window:** A *relative* time range. For example: "15 minutes (-15m) before the alarm to 30 minutes (+30m) after the alarm."

It also contains rules to apply for the alarm and the corresponding device.

* **onDevice:** Defines a device predicate which must be true in order to apply this context configuration to the alarm  
* **onAlarm:** Defines an alarm predicate which must be true in order to apply this context configuration to the alarm

Example Context Configuration:

```json
{
  "apply": {
    "devicePredicate ": [
        { 
            "fragment": "ec_ControlSoftware",
            "equals": "CS82"
        }
    ]
    "meaPredicate": [
        { 
            "fragment": "type",
            "equals": "ec_S:242/232"
        }
    ]
  },
  "config": {
    "dateFrom": "-1d",
    "dateTo": "0d",
    "datapoints": ["MHAI1.H1RMSA1", "MHAI1.H1RMSA2"]
    "events": ["ec_S:242/232", "ec_S:66/11"]
    }
  }
}
```

#### 

#### **2.3. Alarm Context**

The context will be stored at the alarm and must carry the following configuration details:

* **Data Points:** The exact list of measurements/data series that are relevant for the context at the time of creation of the critical main event.  
* **Event Types:** The exact list of event types that are relevant for the context at the time of creation of the alarm.  
* **Alarm Types:** The exact list of alarms that are relevant for the context at the time of creation of the alarm.  
* **Custom Time Window:** The relevant time window of the context. This is a time range calculated from the timestamp of the associated alarm and the relative time configuration.

The Alarm Context is used later for the Data Explorer to initialize the queries. The concept is similar to the Data Explorer Configuration.


Example Alarm with Context:

```json
{
  "id": "123456789",
  "status": "ACTIVE",
  "type": "ec_S:242/232",
  "severity": "CRITICAL",
  "text": "S:242/232 Remote PC - Connection error",
  "time": "2025-09-21T18:12:09.000Z",
  "context": {
    "dateFrom": "2025-09-20T18:12:09.000",
    "dateTo": "2025-09-21T18:12:09.000",
    "datapoints": ["MHAI1.H1RMSA1", "MHAI1.H1RMSA2"],
    "events": ["ec_S:66/11", "ec_S:66/11"]
  }
}
```

#### **2.4. User Workflow**

**Creating a Context at Enercon:**

1. A service user is analyzing alarms in the Cockpit.  
2. The user selects the specific alarm from an alarm list.  
3. A new UI option, such as a "Create Context" or "Save View for Alarm" button, becomes available.  
4. Upon clicking this button, the system will store the context by applying the context configuration at the selected alarm.  
5. After storing the context element in the alarm, the user can directly jump to Data Explorer with a preloaded Alarm Context.

**Loading a Context:**

1. Another service user is reviewing a list of historical alarms.  
2. Next to an alarm that has an associated context, there will be a clear indicator, such as a "View Context," "Load View," or camera icon button.  
3. The user clicks this button.  
4. The application navigates to the Data Explorer.  
5. The Data Explorer automatically loads the exact configuration saved in the context of the alarm:  
   * The time window is automatically selected  
   * The graph is populated with the pre-selected data points, events and alarms.  
   * The related main alarm is clearly marked and alarm text is displayed

The user is immediately presented with the complete, relevant analytical view without any manual setup.

### **3\. Acceptance Criteria**

* **Given** a user wants to create a context for a specific alarm,  
  * **When** they select an alarm and choose the "Create Context" option,  
  * **Then** the system must save the alarm Context (data points, event/alarm types, time window) based on the context configuration and the selected alarm.  
* **Given** a user is viewing a list or timeline of historical alarms  
  * **When** an alarm has a context associated with it,  
  * **Then** a distinct UI element (e.g., an icon button) must be displayed to indicate the context's availability.  
* **Given** a user clicks the "View Context" button for an alarm,  
  * **When** the Data Explorer loads,  
  * **Then** it must perfectly replicate the saved context's configuration.  
  * **And** the time window must be correctly centered around the timestamp of the selected main event.  
  * **And** all previously selected data points, events, and alarms must be active and displayed.  
* **Given** a context has been created,  
  * **When** a user later changes their default Data Explorer view,  
  * **Then** the saved context must remain unchanged. It is an immutable record.

### **4\. UI/UX Considerations**

* **Context Creation:** The action to create an alarm context should be intuitive and contextually placed. Consider adding it to the “Alarms” standard dashboard at the cockpit.  
* **Context Indication:** A clear and consistent icon should be used to signify that a Context is available for an alarm. The icon should have a tooltip explaining what it does (e.g., "Load analysis context").  
* **Loading Feedback:** When a user clicks to load a context, provide brief feedback (e.g., a loading spinner with text like "Loading Context...") to manage expectations as the data is being fetched and rendered.  
* **Show Alarm information:** When a Context is loaded, the Data Explorer UI could display a persistent, non-intrusive notification banner at the top, like: "Viewing Context for 'System Overload Alarm' (2025-09-19 10:15:32)." This banner could include a button to "Exit Context View" and return to a default state.  
* **Context Configuration management:** Can be delivered later, a json configuration via REST API would be enough for Enercon. The configurations will not change often and need no UI support for that in the near future.

### **5\. Business Value**

* **Improved Efficiency:** Drastically reduces the time required for recurring analyses and post-mortem investigations. Analysts can jump directly to the relevant context instead of rebuilding it from scratch.  
* **Enhanced Collaboration:** Allows engineers and analysts to share a common, pre-configured view of an event, ensuring everyone is looking at the same data and context. This standardizes troubleshooting and reporting.  
* **Knowledge Retention:** Captures the expert knowledge of the person who initially configured the view. This "best practice" view for analyzing a specific type of event is preserved and easily accessible for future incidents or for training purposes.  
* **Reduced User Error:** Eliminates the risk of users forgetting to add a critical data point or setting the wrong time window when analyzing a past event, leading to more accurate conclusions.