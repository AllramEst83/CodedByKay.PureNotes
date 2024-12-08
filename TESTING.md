# TESTING.md

## Overview

This document tracks the testing efforts for the **PureNotes** app. It includes prioritized
components, testing goals, and the current status.

---

## Phase 1: Foundation (Logic and Utilities)

### **1. Utils**

| File                  | Status       | Notes                                                                                               |
|-----------------------|--------------|-----------------------------------------------------------------------------------------------------|
| `dateUtils.kt`        | ✅ Tested     | Tests added for parsing, formatting, and validation of dates. Further edge case validation pending. |
| `stringBuildUtils.kt` | ✅ Tested     | String manipulation tests added. Review for additional edge cases (e.g., null inputs).              |
| `deviceUtils.kt`      | ✅ Tested     | Get or create device id test created.                                                               |
| `DisplayUtils.kt`     | ✅ Tested     | Measures screen size.                                                                               |
| `navUtils.kt`         | ❌ Not Tested | Navigation helpers need verification.                                                               |

### **2. Services**

| Service                 | Status       | Notes                                     |
|-------------------------|--------------|-------------------------------------------|
| `DataPopulationService` | ❌ Not Tested | Needs validation for data initialization. |
| `NotificationService`   | ❌ Not Tested | Pending tests for notification handling.  |
| `ImageStorageService`   | ❌ Not Tested | Verify image save, retrieve, and delete.  |
| `UUIDService`           | ❌ Not Tested | Test unique ID generation logic.          |

### **3. Managers**

| Manager                 | Status       | Notes                                      |
|-------------------------|--------------|--------------------------------------------|
| `ThemeDataStoreManager` | ❌ Not Tested | Test storing/retrieving theme preferences. |
| `LocaleManager`         | ❌ Not Tested | Validate language/locale switching.        |
| `AppNavHostManager`     | ❌ Not Tested | Ensure navigation setup correctness.       |

---

## Phase 2: State Management (ViewModels)

| ViewModel                | Status       | Notes                                           |
|--------------------------|--------------|-------------------------------------------------|
| `SettingsPageViewModel`  | ❌ Not Tested | Pending tests for theme and settings updates.   |
| `ToDoGroupPageViewModel` | ❌ Not Tested | Validate grouping, sorting logic.               |
| `ToDoPageViewModel`      | ❌ Not Tested | Test adding/updating tasks and state emissions. |

---

## Phase 3: Reusable UI Components

| Component                 | Status       | Notes                                           |
|---------------------------|--------------|-------------------------------------------------|
| `RoundCheckbox.kt`        | ❌ Not Tested | Verify toggling state behavior.                 |
| `ToDoListItem.kt`         | ❌ Not Tested | Ensure correct display of task states.          |
| `ToDoListContainer.kt`    | ❌ Not Tested | Test rendering and interaction with list items. |
| `ShowTimePickerDialog.kt` | ❌ Not Tested | Validate proper appearance and data return.     |

---

## Phase 4: Screens and Navigation

### **1. Pages**

| Page               | Status       | Notes                                       |
|--------------------|--------------|---------------------------------------------|
| `ToDoPage.kt`      | ❌ Not Tested | Test list updates for add/remove/mark.      |
| `ToDoGroupPage.kt` | ❌ Not Tested | Validate group interactions and navigation. |
| `SettingsPage.kt`  | ❌ Not Tested | Test theme and locale changes.              |

### **2. Navigation**

| Navigation Component | Status       | Notes                                       |
|----------------------|--------------|---------------------------------------------|
| `AppNavHostManager`  | ❌ Not Tested | Ensure seamless navigation between screens. |

---

## Phase 5: System Integration

### **1. Receivers**

| Receiver               | Status       | Notes                                       |
|------------------------|--------------|---------------------------------------------|
| `NotificationReceiver` | ❌ Not Tested | Verify handling notification actions.       |
| `MarkAsDoneReceiver`   | ❌ Not Tested | Validate marking task as done via receiver. |

### **2. Workers**

| Worker                           | Status       | Notes                                      |
|----------------------------------|--------------|--------------------------------------------|
| `FetchWidgetDataOnStartupWorker` | ❌ Not Tested | Test data fetching and widget updates.     |
| `NotificationWorker`             | ❌ Not Tested | Ensure periodic notifications are working. |

---

## Phase 6: End-to-End Tests

| Scenario                                | Status       | Notes                                                   |
|-----------------------------------------|--------------|---------------------------------------------------------|
| Create, group, set reminder, and notify | ❌ Not Tested | Simulate full workflow for ToDo creation and reminders. |
| Change theme or locale                  | ❌ Not Tested | Verify app updates appropriately.                       |
| Mark task as done via widget            | ❌ Not Tested | Validate widget-task interaction.                       |

---

## Progress Tracker

| Phase                  | Total Components | Tested | Coverage % |
|------------------------|------------------|--------|------------|
| **Utils**              | 5                | 3      | 60%        |
| **Services**           | 4                | 0      | 0%         |
| **Managers**           | 3                | 0      | 0%         |
| **ViewModels**         | 3                | 0      | 0%         |
| **UI Components**      | 4                | 0      | 0%         |
| **Pages**              | 3                | 0      | 0%         |
| **System Integration** | 4                | 0      | 0%         |
| **E2E Tests**          | 3                | 0      | 0%         |

---

### Notes:

- Update the **Status** column as you write tests (`✅ Tested`, `❌ Not Tested`, `⚠️ Partial`).
- Add specific notes or blockers for any component as needed.
