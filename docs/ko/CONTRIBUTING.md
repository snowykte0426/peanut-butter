# Peanut-Butter에 기여하기

**Language**: [English](../../CONTRIBUTING.md)

Peanut-Butter 개선에 관심을 가져주셔서 감사합니다. 이 문서는 기여를 위한 권장 워크플로, 코딩 표준 및 품질 기준을 설명합니다.

---
## 목차
1. 철학과 범위
2. 행동 강령
3. 시작하기
4. 프로젝트 구조
5. 빌드 & 테스트
6. 코딩 표준
7. API 설계 가이드라인
8. 로깅 & 시간대 기능
9. 의존성 정책
10. 커밋 & 브랜치 전략
11. 풀 리퀘스트 체크리스트
12. 테스트 가이드라인
13. 성능 & 벤치마킹
14. 폐지(Deprecation) & 브레이킹 체인지
15. 릴리즈 & 버저닝 워크플로
16. 보안 & 책임감 있는 공개
17. FAQ

---
## 1. 철학과 범위
Peanut-Butter는 실용적이고 프로덕션 지향적인 유틸리티(검증, 로깅, 코루틴 계측, 시간대 관리)를 제공합니다. 새로운 기능은 다음과 같아야 합니다:
- 널리 적용 가능할 것 (애플리케이션별 특화가 아님)
- 영리함보다 명확성을 선호할 것
- 가벼울 것 (최소한의 외부 의존성)
- 설득력 있는 이유가 없는 한 하위 호환성을 유지할 것

## 2. 행동 강령
존중하고, 건설적이며, 간결하게 행동하세요. 괴롭힘, 차별, 독성 행동은 용납되지 않습니다. 선의를 가정하세요. 다른 사람들이 더 나아지도록 도우세요.

## 3. 시작하기
### 전제 조건
- Java 17+
- Kotlin 1.9.25
- Gradle 8.10 (래퍼 포함)

### 클론 & 빌드
```bash
git clone https://github.com/snowykte0426/peanut-butter.git
cd peanut-butter
./gradlew clean build
```

### IDE에서 가져오기
IntelliJ IDEA를 사용하세요. Kotlin 코드 스타일을 활성화하세요: "Kotlin official".

## 4. 프로젝트 구조 (관련 고수준)
```
src/main/kotlin/com/github/...    Kotlin 로깅 + 시간대 유틸리티
src/main/java/com/github/...      Java 검증 어노테이션 (Java 친화적으로 유지)
src/test/...                      JUnit + Kotest 테스트
docs/                             릴리즈 노트 & 사용 가이드
build.gradle.kts                  빌드 설정 (퍼블리싱, 의존성)
```

## 5. 빌드 & 테스트
일반적인 작업:
```bash
./gradlew build           # 컴파일 + 테스트 + 어셈블
./gradlew test            # 단위 테스트 실행
./gradlew javadocJar      # Javadoc 아티팩트 빌드
./gradlew publishToMavenLocal  # (로컬 퍼블리싱 설정 시)
```
PR이 병합되기 전에 모든 테스트가 통과해야 합니다.

## 6. 코딩 표준
### 일반
- Java 상호 운용성을 위해 Java 소스가 필요하지 않다면 새 기능은 Kotlin을 선호하세요.
- 공개 API를 null 안전하게 유지하세요.
- 꼭 필요하지 않다면 리플렉션을 피하세요.
- 메서드 이름을 명확하게 하세요; 약어를 피하세요.

### Kotlin
- Kotlin 공식 스타일을 적용하세요 (기본 IntelliJ 형식).
- 톱레벨 확장 유틸리티는 논리적으로 그룹화된 파일에 속합니다 (예: `TimeZoneExtensions.kt`).
- API가 아닌 헬퍼에는 `internal`을 사용하세요.
- 읽기 쉬울 때 간단한 한 줄 표현식 바디를 사용하세요.

### Java
- 검증 어노테이션은 Java 친화적으로 유지하세요; POJO 호환성을 보유하세요.
- Lombok을 피하세요 (프로젝트에서 사용하지 않음).

### 에러 처리
- 잘못된 호출자 입력에 대해 `IllegalArgumentException`을 던지세요.
- 컨텍스트를 추가하는 경우에만 예상치 못한 내부 실패를 래핑하세요; 그렇지 않으면 전파하세요.

## 7. API 설계 가이드라인
공개 함수나 클래스를 추가할 때:
- KDoc / Javadoc을 제공하세요 (목적, 매개변수, 반환값, 예외, 복잡한 경우 예제).
- 이름을 안정적으로 유지하세요; 이름을 변경해야 한다면 이전 심볼을 `@Deprecated`로 표시하고 명확한 대체재를 제공하세요.
- 플래그 불린 대신 오버로드된 메서드나 옵셔널 매개변수를 선호하세요.
- 동기 변형과 실질적으로 다를 때만 코루틴 API에 명확하게 접미사(`Async`)를 붙이세요.
- 오버로딩 모호성이 발생할 때 Kotlin 확장 함수에 `@JvmName`을 표시하세요.
- 바이너리 호환성을 보존하세요 (기존 시그니처에서 매개변수 순서/타입 변경 피하기).

## 8. 로깅 & 시간대 기능
### 로깅
- 컨텍스트 일관성이 중요한 새 코드에서는 직접 SLF4J 호출 대신 기존 헬퍼(`logInfo`, `logInfoAsync` 등)를 사용하세요.
- 조건부 헬퍼(예: `logDebugIf`)로 보호되지 않는 한 빡빡한 내부 루프에서 로깅을 피하세요.

### 시간대 유틸리티
- 정당한 확장이 도입되지 않는 한 `TimeZoneInitializer` 또는 `withTimeZone` 외부에서 기본 JVM 시간대를 직접 변경하지 마세요.
- 새로운 시간대 관련 헬퍼가 `SupportedTimeZone.fromString`을 통해 검증을 강제하도록 하세요.

## 9. 의존성 정책
- 새 외부 의존성을 최소화하세요; PR 설명에서 각 추가를 정당화하세요.
- 버전 범위는 명시적이고 고정됩니다 (동적 `+` 없음).
- 먼저 표준 JDK / Kotlin stdlib 기능을 선호하세요.
- 리팩터링할 때 사용하지 않는 의존성을 제거하세요.

## 10. 커밋 & 브랜치 전략
### 브랜치
- `master`: 안정적, 릴리즈 가능.
- 기능 브랜치: `feature/<간단한-이름>`
- 수정 브랜치: `fix/<이슈-id-또는-컨텍스트>`

### 관례적 커밋
형식: `type(scope?): description`
타입: `feat`, `fix`, `docs`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`, `style`.
예제:
```
feat(logging): add coroutine context trace helper
fix(timezone): handle invalid enum fallback message
```

## 11. 풀 리퀘스트 체크리스트
"Ready" 표시하기 전에:
- [ ] 모든 테스트 통과 (`./gradlew test`)
- [ ] 새 로직에 대한 테스트 추가/업데이트
- [ ] 불필요한 디버그 문/println 없음
- [ ] 공개 API 문서화
- [ ] 관련 없는 형식 변경 없음
- [ ] 의존성 정당화 (추가된 경우)
- [ ] 사용자에게 보이는 변경 사항인 경우 RELEASE_NOTES.md 업데이트 (상단 섹션)
- [ ] 폐지된 항목에 대체 가이드 포함

## 12. 테스트 가이드라인
- 명확성과 표현적 어설션을 위해 JUnit 5 + Kotest (이미 구성됨)를 사용하세요.
- 코루틴 코드: 불안정성을 피하기 위해 `runTest { ... }` (kotlinx-coroutines-test) 사용.
- `method_underCondition_expectedResult` 또는 Kotest용 spec 스타일을 사용하여 테스트 이름을 지정하세요.
- 경계 조건 검증 (null 입력, 빈 리스트, 잘못된 시간대 문자열, 동시 로깅 시나리오).
- 실제 대기를 피하세요; 코루틴 테스트에서 가능한 한 가상 시간에 의존하세요.

## 13. 성능 & 벤치마킹
변경 사항이 성능에 영향을 줄 수 있는 경우:
- 전후를 측정하세요 (마이크로 벤치마크 또는 간단한 타이밍 하니스).
- 조기 최적화하지 마세요; 핫스팟이 증명되지 않는 한 가독성을 선호하세요.
- 필요하지 않다면 핫 패스(예: 로깅 가드 내부)에서 할당을 피하세요.

## 14. 폐지(Deprecation) & 브레이킹 체인지
### 폐지 정책
- Kotlin에서 `@Deprecated(message = "Use X", ReplaceWith("X"))`로 표시하세요.
- 제거하기 전에 폐지된 API를 적어도 하나의 MINOR 사이클 동안 유지하세요.

### 브레이킹 체인지
브레이킹 체인지는 다음을 충족해야 합니다:
1. 정당화됨 (예: 보안, 정확성, 복구 불가능한 설계 결함)
2. `docs/RELEASE_NOTES.md`의 **Breaking Changes** 하에 문서화됨
3. 가능하다면 마이그레이션 노트와 전환 래퍼 제공

## 15. 릴리즈 & 버저닝 워크플로
1. `docs/RELEASE_NOTES.md`가 상단에 새 버전에 대한 완료된 섹션을 갖도록 하세요.
2. `build.gradle.kts`에서 `version` 범프 (시맨틱 버저닝).
3. 전체 빌드 실행: `./gradlew clean build`.
4. 태그: `git tag vX.Y.Z && git push origin vX.Y.Z`.
5. (선택사항) CI를 통해 아티팩트 퍼블리시 / JitPack 소비가 자동으로 태그된 빌드 해결.

## 16. 보안 & 책임감 있는 공개
의심되는 취약점에 대해 공개 이슈를 생성하지 마세요. 대신 관리자에게 직접 연락하세요 (README의 이메일). 다음을 제공하세요:
- 영향받는 버전
- 재현 단계
- 영향 평가

## 17. FAQ
| 질문 | 답변 |
|------|------|
| "새로운 로깅 백엔드를 추가할 수 있나요?" | 예—SLF4J 추상화가 유일한 컴파일 의존성으로 남도록 하세요. 중요한 경우 README에서 교체 문서화. |
| "새 모듈에 왜 Kotlin을 사용하나요?" | 적절한 어노테이션을 통해 Java 상호 운용성을 유지하면서 간결한 확장과 코루틴 지원을 제공합니다. |
| "새로운 시간대를 추가하는 방법은?" | `SupportedTimeZone` 열거형을 확장하세요; 사용자 구성 가능한 경우 문서 및 메타데이터 JSON 업데이트. |
| "내부 리팩터링에 대해 릴리즈 노트를 업데이트해야 하나요?" | 사용자에게 보이는 API/동작 변경이 없다면 필요 없습니다. |

---
## 빠른 참조
```bash
# 빌드 & 테스트
./gradlew clean build

# 테스트만 실행
./gradlew test

# 로컬 퍼블리시 (설정된 경우)
./gradlew publishToMavenLocal
```

Peanut-Butter에 기여해주셔서 감사합니다.