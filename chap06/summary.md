# Right-BICEP: 무엇을 테스트할 것인가?

Right-BICEP은 무엇을 테스트할지에 대해 쉽게 선별하게 합니다.

- Right : 결과가 올바른가?
- B : 경계 조건(boundary conditions)은 맞는가?
- I : 역 관계(inverse relationship)를 검사할 수 있는가?
- C : 다른 수단을 활용하여 교차 검사(cross-check)할 수 있는가?
- E : 오류 조건(error conditions)을 강제로 일어나게 할 수 있는가?
- P : 성능 조건(performance characteristics)은 기준에 부합하는가?

---
## [Right]-BICEP: 결과가 올바른가?

테스트 코드는 우선적으로 기대한 결과를 산출하는지 검증할 수 있어야 합니다. 하지만, 이러한 것은 행복경로 테스트라고 부르는데 이러한 질문을 던져봐야 합니다.

> 나는 코드가 정상적으로 동작한다면, 그것을 알 수 있을까?

다른, 관점으로 어떤 작은 부분의 코드에 대해 행복 경로 테스트를 할 수 없다면, 그 내용을 완전히 이해하지 못한 것입니다. 그리고 앞의 짊눙 대답할 수 있을 때까지 잠시 추가 개발은 보류하면 좋습니다.

우리가 작성하는 단위테스트는 선택을 문서화 하고, 어떤 변경이 발생하면 적어도 현재까지 코드가 어떻게 동작했는지 알게 됩니다.

---
## Rigth-[B]ICEP: 경계 조건은 맞는가?

코드에 있는 분명한 행복 경로는 입력 값의 양극단을 다루는 코드 시나리오의 경계 조건에 걸리지 않을 수도 있습니다. 우리가 마주치는 수많은 결함은 이러한 모서리 사례이므로 테스트로 이것들을 처리해야 합니다.

- 모호하고 일관성없는 입력 값. 예를 들어 특수문자(!@#$%^&*()) 가 포함된 파일 이름
- 잘못된 양식의 데이터, 예를 들어 최상위 도메인이 빠진 이메일 주소(fred@foobar)
- 수치적 오버플로를 일으키는 계산
- 비거나 빠진 값. 예를 들어 0, 0.0, "" 혹은 null
- 이성적인 기댓값을 훨씬 벗어나는 값. 예를 들어 150세의 나이
- 교실의 당번표처럼 중복을 허용해서는 안 되는 목록에 중복 값이 있는 경우
- 정렬이 안 된 정렬 리스트 혹은 그 반대. 정렬 알고리즘에 이미 정렬된 입력 값을 넣는 경우나 정렬 알고리즘에 역순 데이터를 넣는 경우
- 시간 순이 맞지 않는 경우. 예를 들어 HTTP 서버가 OPTIONS 메서드의 결과를 POST메서드보다 먼저 반환해야 하지만 그 후에 반환하는 경우

---
## 경계 조건에서는 CORRECT를 기억하라

CORRECT 약어는 잠재적인 경계 조건을 기억하는데 도움을 줍니다.

- [C]onformance(준수) : 값이 기대한 양식을 준수하고 있는가?
- [O]rdering(순서): 값의 집합이 적절하게 정렬되거나 정렬되지 않았나?
- [R]ange(범위): 이성적인 최솟값과 최댓값 안에 있는가?
- [R]eference(참조): 코드 자체에서 통제할 수 없는 외부 참조를 포함하고 있는가?
- [E]xistence(존재): 값이 존재하는가, 0이거나 집합에 존재하는가?
- [C]ardinality(기수): 정확히 충분한 값들이 있는가?
- [T]ime(절대적 혹은 상대적 시간): 모든 것이 순서대로 일어나는가? 정확한 시간에? 정시에?
---
## Right-B[I]CEP: 역관계를 검사할 수 있는가?

논리적인 역관계를 적용하여 행동을 검사할수 있다. 종종 수학에서 곱셈으로 나눗셈을 검증하고 뺄셈으로 덧셈을 검증하는것 처럼 말이다.


## Right-BI[C]EP: 다른 수단을 활용하여 교차 검사할 수 있는가?

흥미로운 문제에는 무수한 해법이 존재합니다. 그 중 성능이 좋거나 냄새가 좋기 떄문에 1등 해법을 선택합니다. 그러면 프로덕션 결과를 교차 검사하기 위해 '패배자' 해법이 남습니다. 아마도 프로덕션 시스템에 활용하기에는 너무 느리거나 유연하지 않겠지ㅏㅁㄴ, 그것들이 믿을 수 있고 참된 값을 보장한다면 1등 해법을 교차검사할 때 사용할 수 있습니다.

## Right-BIC[E]P: 오류 조건을 강제로 일어나게 할 수 있는가?

행복 경로가 있다는 것은 반대로 불행한 경로도 있다는 것을 의미한다. 테스트 코드로 이러한 모든 실전 문제를 우아하고 이성적인 방식으로 다루려면 테스트도 오류들을 강제로 발생시켜야 한다.

먼저 코드를 테스트하기 위해 도입할 수 있는 오류 혹은 환경적인 제약에 대해 생각해 본다.

- 메모리가 가득 찰 때
- 디스크 공간이 가득 찰 때
- 벽시계 시간에 관한 문제들
- 네트워크 가용성 및 오류들
- 시스템 로드
- 제한된 색상 팔레트
- 매우 높거나 낮은 비디오 해상도

좋은 단위 테스트는 단지 코드에 존재하는 로직 전체에 대한 커버리지를 달성하는게 아니라 때때로 뒷주머니에서 창의력을 꺼내는 노력이 필요하다. 가장 끔찍한 결함은 예상치 못한 곳에서 나온다.

## Right-BICE[P]: 성능 조건은 기준에 부합하는가? 

구글의 롭 파이크는 "병목 지점은 놀라운 곳에서 일어납니다. 따라서 실제로 병목이 어디인지 증명되기 전까지는 미리 짐작하여 코드를 난도질하지 마세요" 라고 말합니다. 정말 많은 프로그래머가 성능 문제가 어디에 있으며 최적의 해법이 무엇인지 추측합니다. 유일한 문제점은 이러한 추측이 종종 잘못되었다는 것입니다.

추측만으로 성능문제에 대응하기 보다는 단위테스트를 설계하여 진짜 문제가 어디에 있으며 예상한 변경 사항으로 어떤 차이가 생겼는지 파악해야 합니다.

- 전형적으로 코드 덩어리를 충분한 횟수만큼 실행하길 원할 것입니다. 이렇게 타임이과 CPU클록주기 관한 이슈를 제거합니다.
- 반복하는 코드 부분을 자바가 최적화하지 못하는지 확인해야 합니다.
- 최적화되지 않은 테스트는 한번에 수 밀리초가 걸리는 일반적인 테스트코드들보다 느립니다. 느린 테스트들은 빠른 것과 분리하세요. 성능 테스트는 야밤에 한번이면 충분합니다. 누군가 성능이 떨어지는 코드를 추가했을 때 전체 테스트 시간이 늘어나는 것을 원치는 않을 것입니다.
- 동일한 머신이라도 실행 시간은 시스템 로드처럼 잡다한 요소에 따라 달라질 수 있습니다.

거대한 서버에서 테스트를 실행하면 충분히 빠르겠지만 사양이 형편없는 데스크톱에서 시리행하면 그다지 빠르지 않을수 있습니다. 환경에 따라 실패하는 테스트를 다루는 것은 결코 즐겁지않으며 여러 환경에서 일관성 있는 동작을 보장하는 해법은 쉽지 않습니다. 유일한 해법은 가능한 프로덕션 환경과 유사한 머신에서 실행하는 것입니다.