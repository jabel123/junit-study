# 깔끔한 코드로 리팩토링하기

낮은 중복과 높은 명확성이라는 두 가지 목표를 합리적인 비용과 놀라운 투자 수익률(ROI)로 달성할 수 있습니다. 좋은 소식은 단위 테스트를 만들면 이러한 목표에 도달할 수 있다는 것입니다.

## 작은 리팩토링
### 리팩토링의 기회
</br>

*리팩토링전 코드*
### 리팩토링의 기회
메소드는 특별히 길지 않고 표현식과 문장들을 합하여 10여줄정도 되는데, 상당히 빽빽한 코드를 담고 있다. 
```
public boolean matches(Criteria criteria) {
        score = 0;

        boolean kill = false;
        boolean anyMatches = false;
        for (Criterion criterion: criteria) {
            Answer answer = answers.get(
                    criterion.getAnswer().getQuestionText());
            boolean match =
                    criterion.getWeight() == Weight.DontCare ||
                            answer.match(criterion.getAnswer());

            if (!match && criterion.getWeight() == Weight.MustMatch) {
                kill = true;
            }
            if (match) {
                score += criterion.getWeight().getValue();
            }
            anyMatches |= match;
        }
        if (kill)
            return false;
        return anyMatches;
    }
```

### 메서드 추출 :: 두 번째로 중요한 리팩토링 친구
리팩토링의 가장 중요한 친구는 이름짓기(rename) 입니다. 대상은 클래스, 메서드, 모든 종류의 변수입니다. 명확성은 대개 코드 의도를 선언하는 것이고, 좋은 이름은 코드 의도를 전달하는 가장 좋은 수단입니다.

우리의 목표는 matches() 메서드의 복잡도를 줄여 코드가 무엇을 담당하는지 그 정책을 쉽게 이해하는 것입니다. 부분적으로 세부 로직을 추출하여 새로운 별도이 메소드로 이동합니다.

```
public boolean matches(Criteria criteria) {
        score = 0;

        boolean kill = false;
        boolean anyMatches = false;
        for (Criterion criterion: criteria) {
            Answer answer = answers.get(
                    criterion.getAnswer().getQuestionText());
            boolean match = matches(criterion, answer);

            if (!match && criterion.getWeight() == Weight.MustMatch) {
                kill = true;
            }
            if (match) {
                score += criterion.getWeight().getValue();
            }
            anyMatches |= match;
        }
        if (kill)
            return false;
        return anyMatches;
    }

    private boolean matches (Criterion criterion, Answer answer)
    {
        return criterion.getWeight() == Weight.DontCare ||
                answer.match(criterion.getAnswer());
    }
```

코드를 이리저리 옮기면 기존 기능들이 쉽게 깨집니다. 따라서 자신감을 가지고 코드를 변경할 수 있어야 하고, 지금까지 알려지지 않은 교활한 작은 결함들이 나오지 않으리라는 확신을 가져야 합니다.  

## 메서드를 위한 더 좋은 집 찾기
새롭게 추출된 matches 메서드는 Profile객체와 아무런 관계가 없으므로 matches메서드를 Criterion 클래스로 이동합니다. 

```
public class Criterion implements Scoreable {
    public boolean matches(Criterion criterion, Answer answer)
    {
        return criterion.getWeight() == Weight.DontCare ||
                answer.match(criterion.getAnswer());
    }
}
    
```
Profile 클래스
```
public boolean matches(Criteria criteria) {
        score = 0;

        boolean kill = false;
        boolean anyMatches = false;
        for (Criterion criterion: criteria) {
            Answer answer = answers.get(
                    criterion.getAnswer().getQuestionText());
            boolean match = criterion.matches(criterion, answer);

            if (!match && criterion.getWeight() == Weight.MustMatch) {
                kill = true;
            }
            if (match) {
                score += criterion.getWeight().getValue();
            }
            anyMatches |= match;
        }
        if (kill)
            return false;
        return anyMatches;
    }
```
Profile 클래스
```
Answer answer = answers.get(criterion.getAnswer().getQuestionText());
```
위의 코드는 디메테르의 법칙(다른 객체로 전파되는 연쇄적은 메서드 호출을 피해야함)을 위반하고 깔끔하지 않다. 이것을 개선하는 첫 번째 단계는 answer할당문의 우변을 새로운 메서드인 answerMatching()메서드로 추출하는 것이다.

Profile 클래스
```
 public boolean matches(Criteria criteria) {
        score = 0;

        boolean kill = false;
        boolean anyMatches = false;
        for (Criterion criterion: criteria) {
            Answer answer = answerMatching(criterion);
            boolean match = criterion.matches(criterion, answer);

            if (!match && criterion.getWeight() == Weight.MustMatch) {
                kill = true;
            }
            if (match) {
                score += criterion.getWeight().getValue();
            }
            anyMatches |= match;
        }
        if (kill)
            return false;
        return anyMatches;
    }

    private Answer answerMatching (Criterion criterion)
    {
        return answers.get(criterion.getAnswer().getQuestionText());
    }
```
임시 변수들은 쓰임새가 다양합니다. 임시 변수로 값비싼 비용의 계산 값을 캐시에 넣거나 메서드 몸체에서 변경되는 것들을 수집하는데 익숙할 것입니다. answer 임시변수는 어떤 경우에도 해당하지 않지만, 임시 변수의 또다른 용례는 코드 의도를 명확하게 하는 것입니다. 임시 변수가 한 번만 사용된다고 해도 유효한 선택입니다.

## 자동 및 수동 리팩토링
여기서 answer 지역변수는 코드의 명확성을 높이지 않고 한 번만 사용합니다. 변수를 제거하고 answerMatching(Criterion)표현을 인라인하겠습니다.

Profile 클래스
```
for (Criterion criterion: criteria) {
    boolean match = criterion.matches(answerMatching(criterion));

    if (!match && criterion.getWeight() == Weight.MustMatch) {
        kill = true;
    }
    if (match) {
        score += criterion.getWeight().getValue();
    }
    anyMatches |= match;
}
```

각 IDE의 Refactoring기능에서 inline 힌트를 얻는 것도 좋은 방법이다. 
matches() 메서드의 세부  사항을 제거했기 때문에 이제 고수준의 정책을 쉽게 이해할 수 있습니다. 메서드의 핵심 목표를 구별할 수 있습니다.
- 매칭되는 조건의 가중치를 합하여 점수를 계산합니다.
- 필수(must-match) 항목이 프로파일 답변과 매칭되지 않으면 false를 반환합니다.
- 그렇지 않고 매칭되는 것이 있으면 true를 반환하고, 매칭되는 것이 없으면 false를 반환합니다.

리팩토링 할 떄는 항상 테스트를 실행하도록 합니다.

## 과한 리팩토링? 

### 보상 : 명확하고 테스트 가능한 단위들
matches메서드는 이제 즉시 이해할 수 있을 정도로 전체 알고리즘이 깔끔하게 정리되었다. 현재 코드는 다음 순서의 알고리즘을 따릅니다.
- 주어진 조건에 따라 점수를 계산합니다.
- 프로파일이 어떤 필수 조건에 부합하지 않으면 false를 반환합니다.
- 그렇지 않으면 어떤 조건에 맞는지 여부를 반환합니다.

### 성능염려 : 그렇지 않아도 된다.
matches()메서드를 리팩토링한 결과 anyMatches(), calculateScore(), doesNotMeetAnyMustMatchCriterion()메서드 각각에 criterion조건에 대한 반복문을 갖게 되었습니다. 새로운 반복문 세개로 matches()메서드는 잠재적 실행시간이 네배가 되었습니다.

그러나, 성능이 문제가 되지 않는다면 어설픈 최적화 노력으로 시간을 낭비하기보다 코드를 깔끔하게 유지하세요. 최적화된 코드는 여러 방면에서 문제 소지가 있습니다. 일반적으로 코드 가독성이 낮고 유지 보수 비용이 증가하고 설계 또한 유연하지 않습니다.

*깔끔한 설계는 최적화를 위한 최선의 준비입니다.*


## 마치며