# 목 객체 사용

- 테스트 도전과제
- 번거로운 동작을 스텁으로 대체
- 테스트를 지원하기 위해 설계 변경
- 스텁에 지능 더하기: 인자 검증
- 목 도구를 사용하여 테스트 자동화
- 마지막 하나의 단순화: 주입 도구 소개
- 목을 올바르게 사용할 때 중요한 것
- 마치며

팻은 "단위테스트를 시스템이 생기자마자 지원했다면 좋겠지만, 그것은 현실에 맞지 않는다." 라고 말합니다.

이 장에서는 목 객체를 도입하여 고통을 주는 협력자에 대한 의존성을 끊는 방법과 항상 존재하는 장애물을 넘을 수 있게 도와주는 도구 활용법을 배울 것입니다. 목(mock)과 함꼐 단위테스트의 터널 끝에서 한줄기 빛을 볼 수 있을 것이다.

--- 
## 테스트 도전과제

테스트할 코드는 아래와 같다.
```
public class AddressRetriever {
   public Address retrieve(double latitude, double longitude)
         throws IOException, ParseException {
      String parms = String.format("lat=%.6flon=%.6f", latitude, longitude);
      String response = new HttpImpl().get(
        "http://open.mapquestapi.com/nominatim/v1/reverse?format=json&"
        + parms);

      JSONObject obj = (JSONObject)new JSONParser().parse(response);

      JSONObject address = (JSONObject)obj.get("address");
      String country = (String)address.get("country_code");
      if (!country.equals("us"))
         throw new UnsupportedOperationException(
            "cannot support non-US addresses at this time");

      String houseNumber = (String)address.get("house_number");
      String road = (String)address.get("road");
      String city = (String)address.get("city");
      String state = (String)address.get("state");
      String zip = (String)address.get("postcode");
      return new Address(houseNumber, road, city, state, zip);
   }
}
```
살짝 보면 이 메서드에 대한 테스트를 작성하는 것이 쉬워보입니다. 수십줄의 길이에 문장 몇 개와 단일 조건문만 포함하기 떄문입니다. 따라서 이 코드가 HTTP GET 요청을 만든다는 것을 인지할 수 있습니다.

HttpImpl 클래스는 아파치의 HttpComponents 클라이언트와 상호 작용하여 REST호출을 실행합니다.

HttpImpl 클래스
```
public class HttpImpl implements Http {
   public String get(String url) throws IOException {
      CloseableHttpClient client = HttpClients.createDefault();
      HttpGet request = new HttpGet(url);
      CloseableHttpResponse response = client.execute(request);
      try {
         HttpEntity entity = response.getEntity();
         return EntityUtils.toString(entity);
      } finally {
         response.close();
      }
   }
}
```

Http 인터페이스
```
public interface Http {
   String get(String url) throws IOException;
}

``` 

HTTP호출이 테스트상에서 일어날 경우 다음의 문제가 생길수 있따.
- 실제 호출에 대한 테스트는 나머지 대다수의 빠른 테스트들에 비해 속도가 느릴 것이다.
- Nominatim HTTP API가 항상 가용한지 보장할 수 없습니다. 통제 밖입니다.

## 번거로운 동작을 스텁으로 대체
먼저 HTTP 호출에서 반환되는 JSON 응답을 이용하여 Address 객체를 생성하는 로직을 검증하는 데 집중합니다. 그렇게 하기 위해 HttpImpl 클래스의 get()메서드 동작을 변경할 필요가 있습니다. 단지 테스트를 작성하는 용도로 하드코딩한 JSON 문자열을 반환하도록 합니다. 테스트 용도로 하드 코딩한 값을 스텁(stub)이라고 합니다.

```
Http http = (String url) -> 
    "{\"address\":{"
    + "\"house_number\":\"324\","
    + "\"road\":\"North Tejon Street\","
    + "\"city\":\"Colorado Springs\","
    + "\"state\":\"Colorado\","
    + "\"postcode\":\"80903\","
    + "\"country_code\":\"us\"}"
    + "}";
```

HttpImpl 클래스에 있는 프로덕션 구현 대신에 스텁을 사용하는 방법을 AddressRetriever 인스턴스로 전달하거나 그것을 주입하는 것을 의미합니다. 지금은 AddressRetriever 클래스의 생성자를 이용하여 스텁을 주입하는 방법을 선택합니다.

AddressRetriever클래스
```
public class AddressRetriever {
   private Http http;

   public AddressRetriever(Http http) {
      this.http = http;
   }

   public Address retrieve(double latitude, double longitude)
         throws IOException, ParseException {
      String parms = String.format("lat=%.6flon=%.6f", latitude, longitude);
      String response = http.get(
         "http://open.mapquestapi.com/nominatim/v1/reverse?format=json&"
         + parms);

      JSONObject obj = (JSONObject)new JSONParser().parse(response);
      // ...

      JSONObject address = (JSONObject)obj.get("address");
      // ...
   }
}
```

테스트를 실행하면 다음일들이 벌어집니다.

- 테스트는 Http의 스텁 인스턴스를 생성합니다. 스텁은 get(String url)단일 메서드가 있으며 하드 코딩된 JSON 문자열을 반환합니다.
- 테스트는 AddressRetriever 객체를 생성하고 생성자에 스텁을 전달합니다.
- AddressRetriever 객체는 스텁을 저장합니다.
- 실행될 떄 retrieve() 메서드는 먼저 넘어온 파라미터의 포멧을 정합니다. 그 다음 스텁이 저장된 http 필드에 get() 메서드를 호출합니다. retrieve() 메서드는 http 필드가 스텁을 참조하는지 프로덕션 구현을 참조하는지 관여하지 않습니다. 메서드가 아는 것은 get() 메서드를 구현한 객체와 상호 작용하고 있다는 점입니다.
- 스텁은 테스트에 하드 코딩된 JSON 문자열을 반환합니다.
- 나머지 retrieve()메서드는 하드 코딩된 JSON 문자열을 파싱하고 그에 따라 Address 객체를 구성합니다.
- 테스트는 반환된 Address 객체의 요소를 검증합니다.

## 테스트를 지원하기 위한 설계 변경

굳이 스텁을 사용할떄 생성자 주입만을 고집할 필요는 없으며, setter등을 이용하여 의존관계를 주입할 수 있습니다.

또 팩토리 메서드를 오버라이드 할 수 도 있습니다. 추상 팩토리를 도입할 수도 있고, 스프링처럼 주입을 수행하는 도구를 사용할 수도 있습니다.

## 스텁에 지능 더하기: 인자 검증
인자를 검증하는 코드를 테스트 코드에 추가한다.

## 목 도구를 사용하여 테스트 단순화
- 테스트에 어떤 인자를 기대하는지 명시하기(스텁 자체에 있는 것과 반대)
- get() 메서드에 넘겨진 인자들을 잡아서 저장하기
- get() 메서드에 저장된 인자들이 기대하는 인자들인지 테스트가 완료될 때 검증하는 능력 지원하기

이 단계들을 수행하는 목을 생성하는 것은 과도합니다. 하지만 동일한 목을 사용하는 두 번쨰 혹은 세번쨰 테슽를 작성한다면 각각을 위해 필요한 코드의 양은 줄어들 것입니다.

그리고 다른 번거로운 의존성들에 대해 더 많은 목을 구현한다면 이들 사이의 중복을 제거하는 방법을 찾을 수도 있습니다. 그것은 목을 채용하는 테스트들을 빠르게 만들 수 있도록 하는 범용 도구를 도입하는 것입니다. 

바퀴를 재발명하기보다 다른 누군가 범용 목적의 목 도구를 설계해 놓은 작품의 과실을 찾습니다. 대표적으로 모키토가 이러한 과일입니다.
```
public class AddressRetrieverTest {
   @Test
   public void answersAppropriateAddressForValidCoordinates() 
         throws IOException, ParseException {
      Http http = mock(Http.class);
      when(http.get(contains("lat=38.000000&lon=-104.000000"))).thenReturn(
            "{\"address\":{"
            + "\"house_number\":\"324\","
           // ...
            + "\"road\":\"North Tejon Street\","
            + "\"city\":\"Colorado Springs\","
            + "\"state\":\"Colorado\","
            + "\"postcode\":\"80903\","
            + "\"country_code\":\"us\"}"
            + "}");
      AddressRetriever retriever = new AddressRetriever(http);

      Address address = retriever.retrieve(38.0,-104.0);
      
      assertThat(address.houseNumber, equalTo("324"));
      // ...
      assertThat(address.road, equalTo("North Tejon Street"));
      assertThat(address.city, equalTo("Colorado Springs"));
      assertThat(address.state, equalTo("Colorado"));
      assertThat(address.zip, equalTo("80903"));
   }
}
```

## 마지막 하나의 단순화: 주입 도구 소개
생성자를 사용하여 목을 대상 클래스에 넘기는 것은 일종의 기법입니다. 프로덕션 코드에서 인터페이스를 변경하고 내부 사항을 다른 클래스에 노출하게 됩니다. 

의존성 주입 도구를 사용하면 훨씬 더 잘할수 있는데, 스프링 DI와 구글 주스같은 더 많은 도구를 찾을 수 있습니다.

하지만 우리는 모키토를 사용하고 있기 떄문에 모키토의 내장 DI 기능들을 활용할 것입니다. 모키토의 DI 기능성은 다른 도구처럼 세련되지는 않지만 대부분의 경우 이것 이상이 필요하지는 않을 것입니다.

1. @Mock 애너테이션을 사용하여 목 인스턴스를 생성합니다.
2. @InjectMocks 애너테이션을 붙인 대상 인스턴스 변수를 선언합니다.
3. 대상 인스턴스를 인스턴스화한 후 MockitoAnnotations.initMocks(this)를 호출합니다.

AddressRetrieverTest
```
public class AddressRetrieverTest {
   @Mock private Http http;
   @InjectMocks private AddressRetriever retriever;
   
   @Before
   public void createRetriever() {
      retriever = new AddressRetriever();
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void answersAppropriateAddressForValidCoordinates() 
         throws IOException, ParseException {
      when(http.get(contains("lat=38.000000&lon=-104.000000")))
         .thenReturn("{\"address\":{"
                        + "\"house_number\":\"324\","
         // ...
                        + "\"road\":\"North Tejon Street\","
                        + "\"city\":\"Colorado Springs\","
                        + "\"state\":\"Colorado\","
                        + "\"postcode\":\"80903\","
                        + "\"country_code\":\"us\"}"
                        + "}");

      Address address = retriever.retrieve(38.0,-104.0);
      
      assertThat(address.houseNumber, equalTo("324"));
      assertThat(address.road, equalTo("North Tejon Street"));
      assertThat(address.city, equalTo("Colorado Springs"));
      assertThat(address.state, equalTo("Colorado"));
      assertThat(address.zip, equalTo("80903"));
   }
}
```
- http 필드를 선언하고 @Mock 애너테이션을 붙입니다. 이 애너테이션은 목을 합성하고자 하는 곳을 의미합니다.
- retriever 필드를 선언하고 @InjectMocks 애너테이션을 붙입니다. 이 애너테이션은 목을 주입하고자 하는 대상을 의미합니다.
- @Before 메서등서 AddressRetriever 클래스의 인스턴스를 생성합니다.
- MockitoAnnotations.initMocks(this)를 호출합니다. this 인수는 테스트 클래스 자체를 참조합니다. 모키토는 테스트 클래스에서 어떤 @Mock 애너테이션이 붙은 필드를 가져와서 각각에 대해 목 인스턴스를 합성합니다. 그 다음 어떤 @InjectMocks 애너테이션이 붙은 필드를 가져와서 목 객체들을 거기에 주입합니다.

목 객체를 주입하려고 모키토는 먼저 사용할 적절한 생성자를 탐색합니다. 아무것도 없으면 적절한 세터 메서드를 탐색합니다. 마지막으로 적절한 필드를 찾습니다.

## 목을 올바르게 사용할 때 중요한 것
최선의 경우 모키토의 when...then(...) 구조물을 사용하여 테스트의 기대 사항을 1줄로 표시할 수 있습니다. 1줄로 동작하고 1줄로 단언합니다. 그러면 빠르게 읽고 이해할 수 있습니다.

목을 사용한 테스트는 진행하길 원하는 내용을 분명하게 기술해야 합니다. 이렇게 하는 한 가지 방법은 연관성입니다. answerAppropriateAddressForValidCoordinates 메서드는 기대하는 인자 문자열인 "lat=38.000000&lon=-104.000000"이 38.0과 -104.0의 인수와 연관됩니다. 모든 일이 항상 이렇게 쉽지는 않지만, 테스트 독자가 코드를 깊이 파지 않아도 이러한 관련성을 쉽게 파악할수록 코드는 더 좋아집니다.

목이 실제 동작을 대신하고 있단걸 잊지 않고, 그것들을 안전하게 사용하고 있는지 질문이 필요합니다.

- 목이 프로덕션 코드의 동작을 올바르게 묘사하고 있는가?
- 프로덕션 코드는 생각하지 못한 다른 형식으로 반환하는가?
- 프로덕션 코드는 예외를 던지는가?
- null을 반환하는가

목은 단위 테스트 커버리지의 구멍을 만드는데, 통합 테스트를 작성하여 이 구멍을 막도록 합니다.
