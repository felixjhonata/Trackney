# AGENTS.md: The AI Agent Protocol

## 1. Technical Stack & Architecture

* **Architecture:** MVVM + Clean Architecture (Single Module).
* **Domain:** Pure Kotlin (No Android dependencies).
* **Data:** Room DB for local persistence (Offline-only).
* **UI:** Jetpack Compose with Material 3.


* **Navigation:** **Navigation 3** (State-driven). Do not use legacy String-based routes.
* **Dependency Injection:** Hilt.
* **Constraint:** Always import `hiltViewModel` from `androidx.hilt.lifecycle.viewmodel.compose`.


* **Data Types:** Use `Double` for all transaction and budget amounts.

## 2. UI Implementation Pattern (Stateless Content)

Every screen must be split into two specific composables:

1. **Wrapper Composable:**
* Handles Hilt ViewModel injection.
* Collects `uiState` as state.
* Uses `LaunchedEffect` to listen to a `SharedFlow<UIEvent>` for one-shot actions (e.g., Show Snackbar).
* Passes state and the `onUserEvent` lambda to the Content Composable.


2. **Content Composable:**
* **Stateless.**
* Parameters: `(state: YourUiState, onUserEvent: (YourEvent) -> Unit)`.
* Must have a corresponding `@Preview` using mock state.



## 3. State & Event Handling

* **UI State:** Use a single `data class` for each screen's state.
* **User Events:** Use a sealed class/interface for user intents, handled via a single `onUserEvent(event)` function in the ViewModel.
* **UI Events:** Use a `MutableSharedFlow` in the ViewModel for one-shot UI triggers like Snackbars.
* **Style:** Use raw Material 3 components and `MaterialTheme.colorScheme`. Ask the user before defining custom hex colors.

## 4. Testing & Error Handling

* **Testing:** Use **MockK** exclusively for unit tests. No other mocking libraries.
* **Logic Validation:** New business logic must include a unit test in `src/test`.
* **Errors:** Surface errors to the user via Snackbars triggered by the `UIEvent` flow.

## 5. Review & Modification Protocol

* **Code Generation:** Provide only the specific diffs or functions requested. Do not output the entire file unless it is a new file or the context is small.
* **Review Decision:** Every review must start with **APPROVE** or **REJECT**.
* **Criteria for REJECT:** Logic in UI, violating Clean Architecture layers, or improper Navigation 3 implementation.
* **Pragmatic Approval:** If the code is functional and safe, Approve it but provide a list of "Critical Nits" for naming, performance, or refactoring.


* **Documentation:** Maintain "self-documenting" code. Avoid KDoc unless the logic is exceptionally complex.

## 6. Reference Patterns

### ViewModel Structure

```kotlin
@HiltViewModel
class FeatureViewModel @Inject constructor(
    private val useCase: FeatureUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(FeatureUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<FeatureUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onUserEvent(event: FeatureEvent) {
        when(event) {
            is FeatureEvent.Submit -> { /* Logic */ }
        }
    }
}
```

### Compose Structure

```kotlin
@Composable
fun FeatureScreen(
    viewModel: FeatureViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is FeatureUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    FeatureContent(state = state, onUserEvent = viewModel::onUserEvent)
}
```

## 7. GitHub Pull Request Review & Comments Protocol

When asked to fetch or read unresolved comments/reviews on a pull request using the GitHub CLI (`gh`), follow this protocol:

1. **Determine Repository Owner and Repo Name**:
   Retrieve the repository information by running `gh pr view <number>` or checking git remote configurations if needed.

2. **Query PR Review Threads via GraphQL API**:
   To accurately retrieve the resolution status (`isResolved`) of comment threads, use the GraphQL API. Run the following command:
   ```powershell
   gh api graphql -F owner="<owner>" -F repo="<repo>" -F number=<number> -f query='query($owner: String!, $repo: String!, $number: Int!) { repository(owner: $owner, name: $repo) { pullRequest(number: $number) { reviewThreads(first: 50) { nodes { isResolved comments(first: 10) { nodes { id body path line url } } } } } } }'
   ```

3. **Filter and Format Unresolved Comments**:
   * Identify all threads where `isResolved` is `false`.
   * For each unresolved comment, extract the `body`, `path`, `line`, and `url`.
   * Present them to the user, providing clickable file links formatted using the `file://` scheme and pointing to the specific line number (e.g., `[filename](file:///absolute/path/to/file#Lline)`).

## 8. Pull/Merge Request Description Format

All Pull Request (PR) or Merge Request (MR) descriptions must follow the structured format demonstrated in PR #15:

1. **High-Level Overview**: A 1-2 sentence paragraph explaining the primary goals, context, and motivation of the pull request.
2. **Summary of Changes**: 
   * Header: `### 📝 Summary of Changes`
   * Format: A numbered list of logical groupings (e.g., UI, Database, Business Logic).
   * Sub-bullets: Detail specific changes using hyphens (`-`).
3. **Overall Effect**:
   * Header: `### 🚀 Overall Effect`
   * Format: A brief paragraph describing the user experience impact, performance improvements, data integrity guarantees, or other high-level effects of the PR.
4. **File Summary**:
   * Header: `### 📁 File Summary`
   * Format: Subsection headers for new, modified, and removed files:
     * `#### 🆕 New Files (<count>)` followed by list of bulleted paths (`* path`)
     * `#### 🛠️ Modified Files (<count>)` followed by list of bulleted paths (`* path`)
     * `#### ❌ Removed Files (<count>)` followed by list of bulleted paths (`* path`)
5. **Dividers**: Sections are separated by a horizontal rule (`---`).

### Template:
```markdown
<High-level description of changes and what they enable.>

---

### 📝 Summary of Changes

1. **<Logical Category 1 (e.g. UI & Page Flow)>**:
   - <Specific change description>
   - <Specific change description>

2. **<Logical Category 2 (e.g. Database Refactoring)>**:
   - <Specific change description>
   - <Specific change description>

---

### 🚀 Overall Effect
<Description of the overall impact on the user, performance, or database stability.>

---

### 📁 File Summary

#### 🆕 New Files (<count>)
* `path/to/new/file`

#### 🛠️ Modified Files (<count>)
* `path/to/modified/file`

#### ❌ Removed Files (<count>)
* `path/to/removed/file` (or *None* if zero)
```
