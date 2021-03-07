# 더 큰 설계 문제

- Profile 클래스와 SRP
- 새로운 클래스 추출
- 명령-질의 분리
- 단위 테스트의 유지 보수 비용
- 다른 설계에 관한 생각들
- 마치며

이 장에서는 좀 더 큰 설계의 관점에서 이야기를 한다. 단일 책임 원칙에 초점을 맞추며, 이는 좀 더 작은 클래스를 만들어 무엇보다 유연성과 테스트 용이성을 높여줍니다. 그리고 명령-질의 분리도 알아보는데, 이것은 부작용을 만들고 동시에 값을 반환하여 사용자를 기만하는 메서드를 만들지 않도록 합니다. 

---
## Profile 클래스와 SRP

Profile 클래스는 회사 혹은 인물 정보를 추적하고 관리합니다. 예를 들어 이름과 질문에 대한 답변들의 컬렉션등을 포함합니다. Profile 크ㅏㄹ래스가 포착하는 이러한 정보 집합들은 시간이 지나면서 많이 바뀔 수 있습니다.

Profile 클래스의 두 번째 책임은 조건의 집합이 프로파일과 매칭되는지 여부 혹은 그정도를 알려주는 점수를 계산하는 것입니다. 

Profile 클래스는 객체지향 클래스 설계의 단일책임 원칙을 위반하고 있습니다. 이것은 클래스를 변경할 떄는 단 한가지 이유만 있어야 함을 의미합니다.

```
SOLID 클래스의 설계원칙
- 단일책임원칙 : 클래스는 변경할 떄 한가지 이유만 있어야 합니다. 클래스는 작고 단일 목적을 추구합니다.
- 개방폐쇄원칙 : 확장에는 열려있고 변경에는 닫혀있어야 합니다. 기존 클래스의 변경을 최소화 해야 합니다.
- 리스코프 치환 원칙 : 하위 타입은 반드시 상위 타입을 대체할 수 있어야 합니다. 클라이언트 입장에서 오버라이딩한 메서드가 기능성을 깨면 안됩니다.
- 인터페이스 분리 원칙 : 클라이언트는 필요하지 않는 메서드에 의존하면 안됩니다. 커다란 인터페이스를 다수의 작은 인터페이스로 분할합니다.
- 의존성 역전 원칙 : 고수준 모듈은 저수준 모듈을 의존해서는 안 됩니다. 둘 다 추상클래스에 의존해야 합니다. 추상 클래스는 구체 클래스에 의존해서는 안 됩니다. 구체 클래스는 추상 클래스에 의존해야 합니다.
```

## 새로운 클래스 추출

Profile 클래스는 책임 두개를 정의합니다.
- 프로파일에 대한 정보 추적하기
- 조건 집합이 프로파일에 매칭되는지 혹은 그 정도를 판단하기.

위 두개에 맞춰 클래스를 두개로 나눕니다.

## 명령-질의 분리

Profile 클래스에서 matches()메서드를 살펴보면 이상한점이 있습니다.
```
public boolean matches(Criteria criteria) {
    MatchSet matchSet = new MatchSet(answers, criteria);
    score = matchSet.getScore();
    return matchSet.matches();
}
```
계산된 점수를 Profile 필드에 저장하는 뭔가 어색한 감이 없잖아 있음.

이러할 경우 점수를 원할 경우 matches메서드를 호출해야한다는 것을 알아야 함.

어떤 값을 반호나하고 부작용을 발생(클래스, 엔티티의 상태 변경)시키는 메서드는 명령-질의 원칙을 위반합니다. 이 원칙에 따르면 어떤 메서드는 명령을 실행(부작용을 생성하는 어떤 작업을 함)하거나 질의에 대답(어떤 값 반환)할 수 있으며, 두 작업을 모두 해서는 안됩니다.


## 단위 테스트의 유지 보수 비용
Profile 인터페이스를 변경하여 ProfileTest 클래스의 메서드가 몇 개 깨졌습니다. 먼저 이것을 고치려고 노력해야 하며, 이러한 노력은 단위 테스트를 소유하는 비용에 해당합니다.

리팩토링은 코드 동작을 변경하지 않고 코드 구현을 바꾸는 활동입니다. 테스트는 그 동작을 반영합니다. 하지만 현실에서는 클래스 동작을 변경하고 있습니다. 적어도 클래스의 인터페이스를 통해 클래스 동작을 노출하는 관점에서 말입니다

### 자신을 보호하는 방법
코드 중복은 가장 큰 설계의 문제입니다. 여러 테스트에 걸친 코드 중복은 두 가지 문제가 있습니다.
1. 테스트를 따르기가 어려워 집니다. 
2. 작은 코드 조각들을 단일 메소드로 추출하면 그 코드 조각들을 변경해야 할 때 미치는 영향을 최소화할 수 있습니다.


### 깨진 테스트코드 고치기
기존 클래스가 둘로 나눠지면서 테스트코드에 버그가 발생하였는데, 이를 수정하기 위해 테스트코드도 둘로 나누며, 그에맞게 테스트코드를 추가합니다.

### 다른 설계에 관한 생각들
answersMap이라는 변수가 Profile클래스에 있는데, 이것을 저장해두는 곳을 데이터베이스로 바꿀 경우 많은 부분의 코드변경이 Profile에서 이뤄지게 되므로 이 부분역시 다음과 같이 나누는 것을 권장한다.

```
public class Profile {
    private AnswerCollection answers = new AnswerCollection();
    private String name;

    public Profile(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void add(Answer answer) {
        answers.add(answer);
    }

    public MatchSet getMatchSet(Criteria criteria) {
        return new MatchSet(answers, criteria);
    }
}
````

AnswerCollection 클래스
```
public class AnswerCollection {
    private Map<String, Answer> answers = new HashMap<>();

    public void add(Answer answer) {
        answers.put(answer.getQuestionText(), answer);
    }

    public Answer answerMatching(Criterion criterion) {
        return answers.get(criterion.getAnswer().getQuestionText());
    }

    public List<Answer> find(Predicate<Answer> pred) {
        return answers.values().stream()
                .filter(pred)
                .collect(Collectors.toList());
    }
}
````

## 마치며
이 장에서는 명령-질의 분리와 SRP에 맞게 리팩토링하는 예제를 살펴보았으며, 리팩토링에 따른 테스트코드의 재작성 예제에 대해 알아보았따.