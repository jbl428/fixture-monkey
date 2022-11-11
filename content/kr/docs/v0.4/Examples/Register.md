---
title: "기본 객체 빌더 정의"
weight: 9
---

{{< alert color="primary" title="Abstract">}}
정의한 기본 연산은 타입을 생성할 때 항상 적용합니다.
{{< /alert >}} 

## 0. 클래스

```java
public class GenerateString {
	String value;
}

public class GenerateInt {
	int value;
}
```

## 1. 단일 타입 추가

```java
LabMonkey labMonkey=LabMonkey.labMonkeyBuilder()
	.register(
        GenerateString.class,
        fixture -> fixture.giveMeBuilder(GenerateString.class)
            .set("value", Arbitraries.strings().alpha())
    )
	.build();
```

## 2. 복수 타입 추가
### register
```java
LabMonkey labMonkey = LabMonkey.labMonkeyBuilder()
	.register(
	    GenerateString.class,
	    fixture -> fixture.giveMeBuilder(GenerateString.class)
            .set("value", Arbitraries.strings().alpha())
    )
	.register(
        GenerateInt.class,
        fixture -> fixture.giveMeBuilder(GenerateInt.class)
            .set("value", Arbitraries.integers().between(1, 100))
	)
	.build();
```


### registerGroup
#### registerGroup 정의
```java
public class GenerateGroup {
	public ArbitraryBuilder<GenerateString> generateString(FixtureMonkey fixtureMonkey){
		return fixtureMonkey.giveMeBuilder(GenerateString.class)
			.set("value", Arbitraries.strings().numeric());
    }
	
	public ArbitraryBuilder<GenerateInt> generateInt(FixtureMonkey fixtureMonkey){
		return fixture.giveMeBuilder(GenerateInt.class)
			.set("value", Arbitraries.integers().between(1, 100));
    }
}
```

#### 옵션 추가
```java
LabMonkey labMonkey = LabMonkey.labMonkeyBuilder()
    .registerGroup(GenerateGroup.class)
	.build();
```

#### 예시
```java
GenerateString generateString = labMonkey.giveMeOne(GenerateString.class);
GenerateInt generateInt = labMonkey.giveMeOne(GenerateInt.class);

then(generateString.getValue()).containsOnlyDigits()();
then(generateInt.getValue()).isBetween(1, 100);
```