# Trackney 💸

Trackney is a clean, modern, and intuitive Android personal finance and expense tracking application. It allows users to log their daily income and expenses, group them by customizable categories, and monitor their monthly balance and spending patterns at a glance.

---

## 🚀 Key Features

* **Monthly Balance Dashboard**:
  * Displays **Total Balance**, **Total Income**, and **Total Expense** for the selected month.
  * Simple month-to-month navigation with a month picker.
* **Transaction Management**:
  * Lists transactions grouped chronologically by date.
  * Easy transaction creation with fields for amount, date/time, category, and optional notes/memos.
  * Detailed transaction view with options to update or delete.
* **Custom Category Management**:
  * Organizes transactions under specific Expense and Income categories.
  * Supports adding, editing, and deleting categories dynamically.
  * Built-in safety checks prevent deleting categories that are currently linked to existing transactions.
* **Database Pre-population**:
  * Automatically seeds default categories upon first run:
    * **Expenses**: *Food, Grocery, Transport, Entertainment, Utility, Others*
    * **Income**: *Salary, Others*

---

## 🛠️ Tech Stack & Architecture

Trackney is built using modern Android development best practices and libraries:

* **Language**: [Kotlin](https://kotlinlang.org/) (100% Kotlin codebase)
* **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material 3](https://m3.material.io/) for dynamic color support, clean typography, and a modern design.
* **Navigation**: [Jetpack Navigation 3](https://developer.android.com/guide/navigation) – utilizes the modern type-safe Compose-first navigation APIs with Kotlinx Serialization.
* **Database / Local Storage**: [Room SQLite Database](https://developer.android.com/training/data-storage/room) – handles local data persistence with custom converters for dates/times and compiler processing via **Kotlin Symbol Processing (KSP)**.
* **Dependency Injection**: [Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android) – simplifies dependency management and lifecycle scoping across repositories, DAOs, and ViewModels.
* **Asynchronous Programming**: [Kotlin Coroutines & Flow](https://kotlinlang.org/docs/flow.html) – used for reactive data streams and UI state updates in a Unidirectional Data Flow (UDF) pattern.
* **Architecture Pattern**: MVVM (Model-View-ViewModel) – cleanly separates UI representation from the business logic and database access.

---

## 📂 Project Structure

```text
app/src/main/java/com/felixjhonata/trackney/
├── MainActivity.kt                  # Main entry point and Navigation3 host
├── TrackneyApplication.kt           # Custom Application class initiating Hilt
├── shared/                          # Shared data layers and utilities
│   └── model/
│       ├── dao/                     # Room DAOs (TransactionDao, CategoryDao)
│       ├── di/                      # Hilt DI Modules (DatabaseModule, DispatchersModule)
│       ├── entity/                  # Database Entities (Transaction, Category)
│       ├── repository/              # Repository implementations
│       └── TransactionType.kt       # Enum for INCOME and EXPENSE
├── home/                            # Home / Dashboard feature
│   ├── model/                       # Home UI State & Event models
│   ├── view/                        # Compose layouts for the Dashboard
│   └── viewmodel/                   # HomeViewModel managing state
├── add_edit_transaction/            # Transaction creation/modification feature
│   ├── model/                       # Screen events and state models
│   ├── view/                        # Compose layouts (Add/Edit Transaction screens)
│   └── viewmodel/                   # ViewModels managing add/edit flows
├── manage_category/                 # Category administration feature
│   ├── model/                       # Category UI state and dialog definitions
│   ├── view/                        # Category management layouts & dialogs
│   └── viewmodel/                   # ManageCategoryViewModel
└── ui/theme/                        # Material 3 colors, typography, and themes
```

---

## ⚙️ Getting Started

### Prerequisites
* **Android Studio** (Koala or newer recommended)
* **JDK 17** or higher
* Android SDK 36 (target version)

### Build and Run
Clone this repository and open the project in Android Studio. You can build and run the app on an emulator or a physical device using Gradle:

```bash
# Build the project
./gradlew assembleDebug

# Run unit tests
./gradlew test
```
