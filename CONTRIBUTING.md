# Contributing to Peanut-Butter

**Language**: [한국어](docs/ko/CONTRIBUTING.md)

Thank you for your interest in improving Peanut-Butter. This document describes the recommended workflow, coding standards, and quality gates for contributions.

---
## Table of Contents
1. Philosophy & Scope
2. Code of Conduct
3. Getting Started
4. Project Structure
5. Build & Test
6. Coding Standards
7. API Design Guidelines
8. Logging & Time Zone Features
9. Dependency Policy
10. Commit & Branch Strategy
11. Pull Request Checklist
12. Testing Guidelines
13. Performance & Benchmarking
14. Deprecation & Breaking Changes
15. Release & Versioning Workflow
16. Security & Responsible Disclosure
17. FAQ

---
## 1. Philosophy & Scope
Peanut-Butter provides pragmatic, production‑oriented utilities (validation, logging, coroutine instrumentation, time zone management). New features should:
- Be broadly applicable (not application‑specific)
- Favor clarity over cleverness
- Remain lightweight (minimal external dependencies)
- Preserve backward compatibility unless there is a compelling reason

## 2. Code of Conduct
Be respectful, constructive, and concise. Harassment, discrimination, or toxic behavior will not be tolerated. Assume good intent. Help others get better.

## 3. Getting Started
### Prerequisites
- Java 17+
- Kotlin 1.9.25
- Gradle 8.10 (Wrapper included)

### Clone & Build
```bash
git clone https://github.com/snowykte0426/peanut-butter.git
cd peanut-butter
./gradlew clean build
```

### Import in IDE
Use IntelliJ IDEA. Enable Kotlin code style: "Kotlin official".

## 4. Project Structure (Relevant High-Level)
```
src/main/kotlin/com/github/...    Kotlin logging + timezone utilities
src/main/java/com/github/...      Java validation annotations (kept Java-friendly)
src/test/...                      JUnit + Kotest tests
docs/                             Release notes & usage guide
build.gradle.kts                  Build config (publishing, dependencies)
```

## 5. Build & Test
Common tasks:
```bash
./gradlew build           # Compile + test + assemble
./gradlew test            # Run unit tests
./gradlew javadocJar      # Build Javadoc artifact
./gradlew publishToMavenLocal  # (If configuring publishing locally)
```
All tests MUST pass before a PR is merged.

## 6. Coding Standards
### General
- Prefer Kotlin for new features unless Java interop requires Java source.
- Keep public APIs null‑safe.
- Avoid reflection unless strictly necessary.
- Keep method names descriptive; avoid abbreviations.

### Kotlin
- Apply Kotlin official style (default IntelliJ formatting).
- Top-level extension utilities belong in logically grouped files (e.g., `TimeZoneExtensions.kt`).
- Use `internal` for non-API surface helpers.
- Use expression bodies for trivial one-liners when readable.

### Java
- Validation annotations remain Java-friendly; retain POJO compatibility.
- Avoid Lombok (not used in project).

### Error Handling
- Throw `IllegalArgumentException` for invalid caller input.
- Wrap unexpected internal failures only if adding context; otherwise let them propagate.

## 7. API Design Guidelines
When adding public functions or classes:
- Provide KDoc / Javadoc (purpose, params, return, exceptions, examples if subtle).
- Keep names stable; if you must rename, mark old symbol `@Deprecated` with clear replacement.
- Prefer overloaded methods or optional parameters instead of flag booleans.
- Keep coroutine APIs clearly suffixed (`Async`) only when they materially differ from sync variants.
- Mark Kotlin extension functions with `@JvmName` if overloading ambiguity arises.
- Preserve binary compatibility (avoid changing parameter order / type in existing signatures).

## 8. Logging & Time Zone Features
### Logging
- Use existing helpers (`logInfo`, `logInfoAsync`, etc.) instead of direct SLF4J calls in new code where contextual consistency matters.
- Avoid logging inside tight inner loops unless guarded by conditional helpers (e.g., `logDebugIf`).

### Time Zone Utilities
- Do not mutate default JVM timezone directly outside `TimeZoneInitializer` or `withTimeZone` unless a justified extension is introduced.
- Ensure any new timezone‑related helper enforces validation through `SupportedTimeZone.fromString`.

## 9. Dependency Policy
- Minimize new external dependencies; justify each addition in PR description.
- Version ranges are explicit & pinned (no dynamic `+`).
- Prefer standard JDK / Kotlin stdlib features first.
- Remove unused dependencies when refactoring.

## 10. Commit & Branch Strategy
### Branching
- `master`: Stable, releasable.
- Feature branches: `feature/<short-name>`
- Fix branches: `fix/<issue-id-or-context>`

### Conventional Commits
Format: `type(scope?): description`
Types: `feat`, `fix`, `docs`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`, `style`.
Examples:
```
feat(logging): add coroutine context trace helper
fix(timezone): handle invalid enum fallback message
```

## 11. Pull Request Checklist
Before marking "Ready":
- [ ] All tests pass (`./gradlew test`)
- [ ] Added / updated tests for new logic
- [ ] No unnecessary debug statements / println
- [ ] Public APIs documented
- [ ] No unrelated formatting churn
- [ ] Dependencies justified (if added)
- [ ] RELEASE_NOTES.md updated (top section) if user-visible change
- [ ] Deprecated items include replacement guidance

## 12. Testing Guidelines
- Use JUnit 5 + Kotest (already configured) for clarity and expressive assertions.
- Coroutine code: use `runTest { ... }` (kotlinx-coroutines-test) to avoid flakiness.
- Name tests using `method_underCondition_expectedResult` or spec style for Kotest.
- Validate edge conditions (null inputs, empty lists, invalid timezone strings, concurrent logging scenarios).
- Avoid real sleeps; rely on virtual time where possible in coroutine tests.

## 13. Performance & Benchmarking
If a change may impact performance:
- Measure before & after (micro-benchmark or simple timing harness).
- Do not prematurely optimize; prefer readability unless a hotspot is proven.
- Avoid allocating on hot paths (e.g., inside logging guards) unless necessary.

## 14. Deprecation & Breaking Changes
### Deprecation Policy
- Mark with `@Deprecated(message = "Use X", ReplaceWith("X"))` in Kotlin.
- Keep deprecated APIs for at least one MINOR cycle before removal.

### Breaking Changes
A breaking change must:
1. Be justified (e.g., security, correctness, unsalvageable design flaw)
2. Be documented in `docs/RELEASE_NOTES.md` under **Breaking Changes**
3. Provide a migration note and, if feasible, transitional wrappers

## 15. Release & Versioning Workflow
1. Ensure `docs/RELEASE_NOTES.md` has a completed section for the new version at the top.
2. Bump `version` in `build.gradle.kts` (Semantic Versioning).
3. Run full build: `./gradlew clean build`.
4. Tag: `git tag vX.Y.Z && git push origin vX.Y.Z`.
5. (Optional) Publish artifacts via CI / JitPack consumption automatically resolves tagged build.

## 16. Security & Responsible Disclosure
Do NOT open public issues for suspected vulnerabilities. Instead contact the maintainer directly (email in README). Provide:
- Affected versions
- Reproduction steps
- Impact assessment

## 17. FAQ
| Question | Answer |
|----------|--------|
| "Can I add a new logging backend?" | Yes—ensure SLF4J abstraction remains the only compile dependency. Document replacement in README if significant. |
| "Why Kotlin for new modules?" | Offers concise extensions & coroutine support while retaining Java interop via proper annotations. |
| "How to add a new timezone?" | Extend `SupportedTimeZone` enum; update documentation & metadata JSON if user configurable. |
| "Do I need to update release notes for internal refactors?" | Not unless user-visible API/behavior changes. |

---
## Quick Reference
```bash
# Build & test
./gradlew clean build

# Run only tests
./gradlew test

# Publish locally (if configured)
./gradlew publishToMavenLocal
```

Thank you for contributing to Peanut-Butter.

