---
title: "Lombok을 사용하는 Java"
weight: 1
---

## @Value
### ConstructorPropertiesIntrospector
#### 0. 필요조건
둘 중 하나의 조건만 만족하면 됩니다.
* `lombok.config`에 `lombok.anyConstructor.addConstructorProperties=true` 옵션을 추가합니다.
* 생성자에 `@ConstructorProperties` 가 있습니다.

#### 1. 옵션 변경
`FixtureMonkeyBuilder`의 옵션 중 `objectIntrospector`를 변경합니다.

```java
FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
    .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
    .build();
```

### JacksonArbitraryIntrospector
{{< alert color="primary" title="Tip">}}
서드파티 라이브러리 [Jackson](https://github.com/FasterXML/jackson)에 의존성이 있어 모듈 추가가 필요합니다.
{{< /alert >}}

#### 1. 의존성 추가
```groovy
testImplementation("com.navercorp.fixturemonkey:fixture-monkey-jackson:{{< param version >}}")
```

```xml
<dependency>
  <groupId>com.navercorp.fixturemonkey</groupId>
  <artifactId>fixture-monkey-jackson</artifactId>
  <version>{{< param version >}}</version>
  <scope>test</scope>
</dependency>
```

#### 2. 옵션 변경
`FixtureMonkeyBuilder` 의 옵션 중 `objectIntrospector`를 변경합니다.

##### ObjectMapper를 정의한 경우
```java
FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
    .plugin(new JacksonPlugin(objectMapper))
    .build();
```

##### ObjectMapper를 정의하지 않은 경우
```java
FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
    .plugin(new JacksonPlugin())
    .build();
```

## @Builder
### 1. 옵션 변경
`FixtureMonkeyBuilder` 의 옵션 중 `objectIntrospector`를 변경합니다.
```java
FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
    .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
    .build();
```


## @NoArgsConstructor
### FieldReflectionArbitraryIntrospector
#### 1. 옵션 변경
`FixtureMonkeyBuilder` 의 옵션 중 `objectIntrospector`를 변경합니다.

```java
FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
    .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
    .build();
```

## @NoArgsConstructor + @Setter
### BeanArbitraryIntrospector
{{< alert color="primary" title="Tip">}}
기본으로 설정된 `objectIntrospector` 입니다.
{{< /alert >}}

### 1. 옵션 변경
`FixtureMonkeyBuilder` 의 옵션 중 `objectIntrospector`를 변경합니다.

```java
FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
	.objectIntrospector(BeanArbitraryIntrospector.INSTANCE)
	.build();
```