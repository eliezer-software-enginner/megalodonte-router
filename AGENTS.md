# AGENTS.md - Megalodonte Router Development Guide

This document provides essential information for agentic coding agents working in this JavaFX routing library project.

## Build Commands

This is a Gradle-based Java project using Kotlin DSL. All commands should be run from the project root.

### Core Build Commands
```bash
# Build the entire project
./gradlew build

# Clean build directory
./gradlew clean

# Assemble JAR artifacts only
./gradlew assemble

# Create all distribution artifacts (sources, javadoc, JAR)
./gradlew jar javadocJar sourcesJar

# Run all checks (includes tests)
./gradlew check
```

### Test Commands
```bash
# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.example.MyTest"

# Run a single test method
./gradlew test --tests "com.example.MyTest.myTestMethod"

# Run tests with fail-fast (stop on first failure)
./gradlew test --fail-fast

# Run tests in debug mode (listens on port 5005)
./gradlew test --debug-jvm

# Dry run to see which tests would be executed
./gradlew test --test-dry-run
```

### Publishing
```bash
# Publish to local Maven repository
./gradlew publishToMavenLocal

# Publish to configured remote repositories
./gradlew publish
```

## Code Style Guidelines

### Language and Framework
- **Language**: Java 17 (using Java toolchain)
- **Build System**: Gradle with Kotlin DSL
- **UI Framework**: JavaFX (version 17.0.10)
- **Testing**: JUnit 5 with Mockito

### Import Organization
- Group imports by: standard Java libraries, third-party libraries, project packages
- Use fully qualified imports for clarity in this small library
- No wildcard imports (`*`)
- Example order:
  ```java
  import java.util.*;
  import java.util.function.*;
  
  import javafx.scene.*;
  import javafx.stage.*;
  import megalodonte.components.*;
  ```

### Naming Conventions
- **Classes**: PascalCase (e.g., `Router`, `RouteNotFoundException`)
- **Methods**: camelCase (e.g., `spawnWindow`, `resolveRoute`)
- **Constants**: UPPER_SNAKE_CASE (though rarely used in this functional style)
- **Packages**: lowercase with dots (e.g., `megalodonte.router`)
- **Records**: Follow class naming, use descriptive field names

### Code Structure
- **Public API First**: All public classes/interfaces should have comprehensive Javadoc
- **Functional Style**: Prefer functions, records, and immutable structures
- **Explicit Error Handling**: Use checked exceptions where appropriate, runtime exceptions for usage errors
- **Records for DTOs**: Use `record` for data carriers like `Route`, `RouteProps`, `ResolvedRoute`

### Documentation Standards
- All public APIs must have Javadoc comments
- Use `<p>` tags for paragraph breaks in Javadoc
- Include `@param` and `@return` tags where applicable
- Document exceptions with `@throws` when declared
- Example format (see Router.java:23-29):
  ```java
  /**
   * Brief description in one sentence.
   *
   * <p>Detailed description with context and usage examples.</p>
   *
   * @param paramName description of parameter
   * @return description of return value
   * @throws ExceptionType when and why this exception is thrown
   */
  ```

### Error Handling Patterns
- **Route Resolution**: Throw `RouteNotFoundException` for unmatched routes
- **Navigation**: Provide both exception-throwing and error-handling-callback versions
- **Reflection Operations**: Catch `ReflectiveOperationException` and handle appropriately
- **Validation**: Fail fast with descriptive messages

### Testing Guidelines
- Use JUnit 5 (`@Test`, `@BeforeEach`, `@DisplayName`)
- Mock dependencies with Mockito
- Test both happy path and error conditions
- Name test methods descriptively: `methodName_whenCondition_expectedResult()`
- Example test structure:
  ```java
  @Test
  @DisplayName("should navigate to valid route without errors")
  void navigateTo_whenValidRoute_shouldSetScene() {
      // Arrange
      // Act  
      // Assert
  }
  ```

### Dependencies and Libraries
- **JavaFX**: Use `javafx.controls` module (configured in build.gradle.kts)
- **Testing**: JUnit 5 platform, Jupiter engine, Mockito core/jupiter
- **Megalodonte Components**: External dependency `megalodonte:components:1.0.0`
- **Java Toolchain**: Java 17 (explicitly configured)

### Package Structure
```
src/main/java/megalodonte/router/
├── Router.java           # Main routing engine
├── RouteParamsAware.java # Interface for parameter injection
└── RouteNotFoundException.java # Custom exception

src/test/java/megalodonte/router/  # (when added)
```

### Method Design Patterns
- **Factory Functions**: Use `Function<Router, Object>` for screen instantiation
- **Error Callbacks**: Provide `Consumer<Exception>` parameters for graceful error handling
- **Builder Pattern**: Consider for complex configuration (though not currently used)
- **Record Returns**: Use records for multi-value returns

### Performance Considerations
- Route resolution uses linear search - acceptable for small route sets
- Window lifecycle management uses `List<SpawnedWindow>` - fine for typical desktop apps
- Reflection used only for screen instantiation via `render()` method
- Consider caching resolved routes if performance becomes an issue

### Security Notes
- No authentication/authorization - this is a UI routing library only
- Route parameters are strings - validate as needed in application code
- Window management follows JavaFX security model
- No external network calls or file system access

## Development Workflow

1. Make changes to source code
2. Run `./gradlew test` to ensure tests pass
3. Run `./gradlew build` to verify compilation
4. Run `./gradlew check` for full validation
5. Use `./gradlew javadoc` to verify documentation quality
6. Test with real JavaFX applications before publishing

## Common Gotchas

- **JavaFX Modules**: Only `javafx.controls` is included - add other modules as needed
- **Route Patterns**: Use `${param}` syntax for dynamic segments, not `:param`
- **Window Management**: Remember to call `stage.setOnHidden()` for cleanup
- **Reflection**: Screen classes must have a `render()` method returning a `Component`
- **Testing**: JavaFX tests may require special test setup for headless environments