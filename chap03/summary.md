# JUnit 단언 깊게 파기

이번 챕터에서는 햄크레스트(Hamcrest) 라이브러리를 활용하여 JUnit에서 다양한 방법으로 단언을 활용하는 방법을 배웁니다. 

## JUnit 단언

JUnit에서 단언은 테스트에 넣을 수 있는 정적메서드 호출입니다. 각 단언은 어떤 조건이 참인지 검증하는 방법입니다. 단언한 조건이 참이 아니면 테스트는 그 자리에서 멈추고 실패를 보고합니다. 

JUnit에서는 두가지 스타일의 단언 스타일을 제공합니다.
- 전통적인 스타일의 단언은 원래 버전에 포함되어 있으며,
- 새롭고 좀 더 표현력이 좋은 햄크레스트라고 알려진 단언도 있습니다.

두 가지 단언스타일은 각자 다른 환경에서 다른 방식으로 제공됩니다. 두 가지를 섞어서 사용할 수도 있지만 보통 둘 중 한 가지를 선택하면 좋습니다. 

전통적인 단언과 햄크레스트단언을 테스트 할 것인데, 이는 이 md파일이 아닌 직접 코드를 작성해 보고자 한다.


AssertTrue대신 AssertThat을 사용하여, 실패했을 경우 어떠한 값이 나왔으며 기댓값은 무엇이었는지 알 수 있도록 한다. 

JUnit 햄크레스트 매처를 이용하면 다음 일을 할 수 있다.
- 객체 타입을 검사합니다.
- 두 객체의 참조가 같은 인스턴스인지를 검사합니다.
- 다수의 매처를 결합하여 둘 다 혹은 둘 중에 어떤 것이든 성공하는지 검사합니다.
- 어떤 컬렉션이 요소를 포함하거나 조건에 부합하는지 검사합니다.
- 어떤 컬렉션이 아이템 몇 개를 모두 포함하는지 검사합니다.
- 어떤 컬렉션에 있는 모든 요소가 매처를 준수하는지 검사합니다. 


is(), equalTo()메서드 모두 일치하는지 검사하는 예처럼 비슷해 보이지만 equalsTo의 경우 배열, 컬렉션 경우를 비교할때 사용하고 is의 경우 반환값이 같은지 여부 정도 검사를 진행한다고 하지만, 배열 비교가 is에서도 잘 이뤄지니 혼동이 된다.

## 예외를 기대하는 세 가지 방법

코드의 행복한 경로를 보장하는 것과 더불어 기대하는 예외를 던지는지 확인하고 싶습니다. 어떤 클래스가 예외를 던지는 조건을 이해하면 그 클래스를 사용하는 클라이언트 개발자의 삶이 한결 편안해집니다. 

JUnit은 적어도 세 가지 다른 방식으로 기대한 예외를 던지는지 명시할 수 있습니다.
1. 단순한 방식: 애너테이션 이용
2. 옛 방식: try/catch와 fail 이용
3. 새로운 방식 : ExcpectedException 규칙  
JUnit은 커스텀 규칙을 정의하여 테스트가 실행되는 흐름 동안 발생하는 일에 대한 더 큰 통제권을 부여합니다. 한편으로 JUnit 규칙은 관점 지향 프로그래밍과 유사한 기능을 제공합니다. 자동으로 테스트 집단에 종단 관심사를 부착할 수 있습니다. 
JUnit은 별도로 코딩할 필요 없이 바로 사용할 수 있는 소수의 유용한 규칙들을 제공합니다. 특히 ExpectedException 규칙은 예외를 검사하는 데 있어 단순한 방식과 옛 방식의 좋은 점만 모았습니다. 

이 세가지 경우를 예제코드를 작성하면서 확인해 보겠다.

## 던져라

검증된 예외(checked exception)가 테스트코드에 발생하면 try/catch를 발생시키지 말고 그냥 던지도록 한다.