# 테스트 리팩토링

테스트는 결함을 최소화하고 리팩토링으로 프로덕션 시스템을 깔끔하게 유지시켜 주지만, 이것은 지속적인 비용을 의미하기도 합니다. 시스템이 변경됨에 따라 테스트 코드도 다시 들여다보아야 합니다. 때때로 변경 사항들을 청소하고 그 결과로 깨진 수많은 테스트를 고쳐야 합니다.

이 장에서는 리팩토링 이해도를 최대화하고 테스트 유지보수 비용을 최소화 하는 방법을 알아봅니다.

---
## 이해 검색

테스트 이름이 어떤 유용한 정보도 제공하고 있지 않고, 주석이 몇 줄 되지 않아 이해에 어려움을 겪는 상황.

### 테스트 냄새: 불필요한 테스트 코드

먼저, try-catch문을 없애고 JUnit자체에서 잡도록 throws한다. 
그리고 불필요한 not-null assert문은 제거합니다.

### 테스트 냄새: 추상화 전략
잘 구성된 테스트는 시스템과 상호 작용을 '데이터 준비하기', '시스템과 동작하기', '결과 단언하기' 세가지 관점에서 보여줍니다.

```
좋은 테스트는 클라이언트가 시스템과 어떻게 상호작용하는지 추상화합니다.
```

뒤죽박죽된 테스트는 search.getMatches() 호출에서 반환된 매칭 목록에 대해 단언문 5줄을 포함합니다. 이 5줄을 각각 읽어야만 무슨 작업인지 이해할 수 있습니다.
*뒤죽박죽*
```
List<Match> matches = search.getMatches();
assertTrue(matches.size() >= 1);
Match match = matches.get(0);
assertThat(match.searchString, equalTo("practical joke"));
assertThat(match.surroundingContext, equalTo(
    "or a vast practical joke, though t"));
````

*수정후*
```
assertThat(search.getMatches(), containsMatches(new Match[] { 
         new Match("1", "practical joke", 
                   "or a vast practical joke, though t") }));
```
수정후 코드를 보면 사용자 정의 매처는 "matches 변수는 Match객체를 한 개 포함하고 있는데, 그 객체는 특정 검색 문자열과 주변 맥락을 포함하고 있는지 단언할 거야"라는걸 알 수 있습니다.

*사용자 정의 매처*
```
public class ContainsMatches extends TypeSafeMatcher<List<Match>> {
   private Match[] expected;

   public ContainsMatches(Match[] expected) {
      this.expected = expected;
   }

   @Override
   public void describeTo(Description description) {
      description.appendText("<" + expected.toString() + ">");
   }

   private boolean equals(Match expected, Match actual) {
      return expected.searchString.equals(actual.searchString)
         && expected.surroundingContext.equals(actual.surroundingContext);
   }

   @Override
   protected boolean matchesSafely(List<Match> actual) {
      if (actual.size() != expected.length)
         return false;
      for (int i = 0; i < expected.length; i++)
         if (!equals(expected[i], actual.get(i)))
            return false;
      return true;
   }

   @Factory
   public static <T> Matcher<List<Match>> containsMatches(Match[] expected) {
      return new ContainsMatches(expected);
   }
}
```

다음 코드는 결과가 0인지 비교하는 코드입니다.
```
assertThat(search.getMatches().size(), equalTo(0));
```

*수정후*
```
assertTrue(search.getMatches().isEmpty());
```
이와 같이 바꾸면 크기 비교를 이해하는 불필요한 정신적 노력을 줄일 수 있습니다.

## 테스트 냄새: 부적절한 정보
잘 추상화된 테스트는 코드를 이해하는데 중요한 것을 부각시켜 주고 그렇지 않은 것은 보이지 않게 해 줍니다. 테스트에 사용된 데이터는 스토리를 말하는데 도움을 주어야 합니다.

때때로 테스트에는 부적절하지만, 당장 컴파일되어야 하기 때문에 데이터를 넣기도 합니다. 예를 들어 메서드가 테스트에는 어떤 영향도 없는 부가적인 인수를 취하기도 합니다.

*수정 전*
```
Search search = new Search(stream, "practical joke", 1);
... 
assertThat(search.get Matches(), containsMatches(new Match[]{new Match("1", "practical joke", "or a vast practical joke, though t")}))
```

문자열 1이 무엇을 의미하는지 알 수 없습니다. 1은 실제로는 검색 제목으을 의미합니다.

더 나은 해결책은 의미 있는 이름을 가진 상수를 도입하여 즉시 파악할 수 있도록 하는 것입니다.

*수정 후*
```
public class SearchTest {
    private static final String A_TITLE = "1";

    @Test
    public test() {
        Search search = new Search(stream, "practical joke", A_TITLE);
        ...
        assertThat(search.get Matches(), containsMatches(new Match[]{new Match(A_TITLE, "practical joke", "or a vast practical joke, though t")}))
        ...
    }
}
```
## 테스트 냄새: 부푼 생성
이제 Search객체의 생성자에 InputStream 객체를 넘겨야 합니다. 테스트코드는 InputStream 객체를 두군데서 만듭니다. 첫번째 생성은 세 문장으로 구성됩니다.

```
String pageContent = "There are certain queer times and occasions "
    + "in this strange mixed affair we call life when a man "
    + "takes this whole universe for a vast practical joke, "
    + "though the wit thereof he but dimly discerns, and more "
    + "than suspects that the joke is at nobody's expense but "
    + "his own.";
byte[] bytes = pageContent.getBytes();
ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
```
앞의 예제에서는 사용자 정의 단어를 사용하여 매칭 결과를 비교했는데 11.3절 테스트에 나와있는 나머지 구현 세부사항이 누락된 추상화를 의미합니다. 해결책은 주어진 적절한 텍스트에 대해 InputStream객체를 생성하는 도우미 메서드를 만드는 것입니다.

```
    public void test() {
        InputStream stream =
        streamOn("There are certain queer times and occasions "
        + "in this strange mixed affair we call life when a man "
        + "takes this whole universe for a vast practical joke, "
        + "though the wit thereof he but dimly discerns, and more "
        + "than suspects that the joke is at nobody's expense but "
        + "his own.");
        ....
    }
    
    ...
    private InputStream streamOn(String pageContent) {
      return new ByteArrayInputStream(pageContent.getBytes());
   }
```
## 테스트 냄새: 다수의 단언

테스트마다 단언 한개로 가는 방향은 좋은 생각입니다. 때때로 단일 테스트에 다수의 사후 조건에 대한 단언이 필요하기는 하지만, 그 보다 더 자주 여러개의 단언이 있다는 것은 테스트 케이스를 두 개 포함하고 있다는 증거입니다.

## 테스트 냄새: 테스트와 무관한 세부 사항들
테스트를 실행할 때는 로그를 끄지만 그렇게 하는 코드는 테스트의 정수를 이해하는 데 방해물이 될 수 있습니다. 그리고 좋은 코딩 시민으로서 항상 스트림을 사용한 후에는 닫아야 하지만 그것 또한 테스트에서는 군더더기가 될 수 있습니다.

이러한 군더더기들은 @Before와 @After 메서드로 이동시킵니다.

## 테스트 냄새: 잘못된 조직
테스트에서 어느 부분들이 준비(Arrange), 실행(Act), 단언(Assert) 부분인지 아는 것은 테스트를 빠르게 인지할 수 있습니다.

각 부분마다 빈줄을 삽입해 줍니다.


## 테스트 냄새: 암시적 의미 
각 테스트가 분명하게 대답해야 할 가장 큰 질문은 "왜 그러한 결과를 기대하는가?" 입니다. 독자는 테스트 준비와 단언 부분을 상호 연관 지을 수 있어야 합니다. 단언이 기대하는 이유가 분명하지 않다면 코드를 읽는 사람들은 그 해답을 얻기 위해 다른 코드를 뒤져 가며 시간을 낭비해야 하기 때문입니다. (좀 더 읽기 쉽게 만든다.)

## 새로운 테스트 추가
테스트를 깔끔하게 두개로 분할하면서 상대적으로 새로운 몇갤의 테스트를 추가하기 쉬워졋습니다.

## 마치며
독자는 테스트 이름을 읽고서 어떤 케이스인지 이해할 수 있습니다. 먼저 테스트의 실행 부분에 집중하여 코드가 무엇을 실행하는지 압니다. 그들은 준비 부분에서 어떤 맥락으로 테스트가 실행되는지 이해하고, 단언 한 개로 기대하는 결과가 무엇인지 파악합니다. 이렇게 이해가 잘 되는 행위들은 전보다 훨씬 더 빠르게 실행합니다.

프로덕션 코드를 깔끔하고 간결하게 리팩토링하고, 프로덕션 코드를 설계할 때 더 많은 유연성을 제공하도록 리팩토링하고, 시스템의 의존성 도전 과제에 대해 목을 지원하고, 유지보수 비용을 최소화하고, 이해도를 최대화하도록 테스트를 리팩토링하는 것입니다.