# Peanut-Butter 🥜🧈

A comprehensive utility library for Java development, providing essential tools and helper functions.

## Features

### Field Validation Annotations

Custom validation annotations based on Jakarta Bean Validation.

#### @FieldEquals
Validates that specified fields have equal values.

#### @FieldNotEquals  
Validates that specified fields have different values.

### Key Features

- **Explicit field validation**: Specify fields to validate using `fields` array
- **Multiple validations**: Apply multiple validation rules to a single class
- **Custom messages**: Customize error messages with `message` attribute
- **Jakarta Bean Validation compatible**: Fully compatible with standard validation framework
- **Inheritance support**: Validates fields from parent classes
- **Null-safe**: Safe handling of null values

## Installation

### Gradle (build.gradle.kts)

```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.0.0")
    
    // Jakarta Bean Validation dependencies
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    implementation("org.glassfish:jakarta.el:4.0.2")
}
```

### Via JitPack

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.0.0")
}
```

## Quick Example

```java
@FieldEquals(fields = {"password", "passwordConfirm"}, message = "Passwords must match")
@FieldNotEquals(fields = {"username", "password"}, message = "Username and password must be different")
public class UserForm {
    private String username;
    private String password;
    private String passwordConfirm;
    // getters and setters...
}
```

## Requirements

- Java 17+
- Jakarta Bean Validation API 3.0+

## License

MIT License - see [LICENSE](LICENSE) file for details.

## Contributing

Issues, feature requests, and pull requests are always welcome!

## Author

**Kim Tae Eun** (snowykte0426)
- Email: snowykte0426@naver.com
- GitHub: [@snowykte0426](https://github.com/snowykte0426)