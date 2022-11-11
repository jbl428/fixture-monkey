
---
title: "Release Notes"
weight: 6
---
### 0.4.3
#### LabMonkey
* Javax.validation에서 숫자 타입을 생성할 때 min, max값을 타입에 맞게 제한합니다.
* generic이 없는 컨테이너를 생성할 수 있습니다.
* `JsonNode`를 생성할 수 있습니다.
* `@Pattern`과 `@NotBlank`를 설정한 경우 적용이 안되는 문제를 해결합니다.
* 코틀린 사용자를 위해 `fixture-monkey-starter-kotlin` 모듈을 추가합니다.
* Map과 Set에서 효율적으로 유일한 요소를 생성하게 수정합니다.
* 코틀린 플러그인을 추가했을 때 부모 객체의 필드를 못 받아오는 문제를 해결합니다.

#### FixtureMonkey
* generator를 변경해서 생성했을 때 generator에 해당하는 propertyNameResolver가 동작안하는 문제를 해결합니다.

### 0.4.2
* 생성자로 객체를 생성하는 ConstructorPropertiesArbitraryIntrospector 추가
* 팩토리 메서드로 객체를 생성하는 FactoryMethodIntrospector 추가
* nullableContainer 옵션이 적용안되는 문제 해결
* setInner, setLazy Exp 추가

### 0.4.1
* 자식 객체를 register한 경우 부모 객체에서 register한 setNull이 적용안되는 문제 해결
* BigDecimal, UUID와 같이 자바 기본 패키지에 존재하는 객체 생성 못하는 문제 해결
* KotlinPlugin을 추가하면 자바 객체를 생성 못하는 문제 해결

### 0.4.0
* Exp 추가
* 맵 연산 추가 `setInner`
* 옵션 변경