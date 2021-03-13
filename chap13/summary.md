# 까다로운 테스트

- 멀티스레드 코드 테스트
- 데이터베이스 테스트
- 마치며

모든 것이 단위 테스트를 하기에 쉬운 것은 아닙니다. 어떤 코드는 테스트하기가 너무 까다롭습니다. 이 장에서는 좀 더 도전적인 상황을 테스트 하는 방법에 대한 예제 몇 개와 함꼐합니다. 특히 스레드와 영속성에 연관된 코드를 테스트할 것입니다. 

---
## 멀티스레드 코드 테스트

동시성 코드에 대한 단위테스트 하는 것은 어려우며, 기술적으로 단위테스트의 영역이 아니기 때문에 통합 테스트로 분류하는 것이 낫습니다. 이때 애플리케이션 고유의 로직 중 일부는 동시적으로 실행될 수 있음을 고려하여 통합적으로 검증해야 합니다. 

스레드를 사용하는 코드에 대한 테스트는 느린 경향이 있습니다. 우리는 단위 테스트를 할 떄 느린 테스트는 원하지 않는데 말입니다. 동시성 문제가 없다는 것을 보장하면서 실행 시간의 범위를 확장해야 하기 때문입니다. 스레드에 관한 결함은 때때로 슬쩍하고 오랫동안 잠재해 있다가 없을 것 같다고 확신한 한참 후에 등장하기도 합니다.

### 단순하고 똑똑하게 유지

멀티스레드 코드를 테스트할 때는 다음 주요 주제를 따릅니다.
- **스레드 통제와 애플리케이션 코드 사이의 중첩을 최소화 합니다.** : 스레드 없이 다량의 애플리케이션 코드를 단위 테스트할 수 있도록 설계를 변경합니다. 남은 작은 코드에 대해 스레드에 집중적인 테스트를 작성합니다.
- **다른사람의 작업을 믿습니다.** : 자바5에는 더그 리아의 훌ㄹㅇ한 동시성 유틸리티 클래스 (java.util.concurrent 패키지가 있음)가 들어 있고, 그것은 이미 2004년에 나온 자바 5 이래로 오랜 시간 충분히 검증받았습니다. 예를 들어 생사낮/ 소비자 문제를 직접 코딩하지 말고 똑똑한 다른 사람들이 직접 써보며 유용성을 입증한 BlockingQueue 클래스를 사용합니다.

### 모든 매칭 찾기

```
public class ProfileMatcher {
   private Map<String, Profile> profiles = new HashMap<>(); 
   private static final int DEFAULT_POOL_SIZE = 4;

   public void add(Profile profile) {
      profiles.put(profile.getId(), profile);
   }


    
   public void findMatchingProfiles(
         Criteria criteria, MatchListener listener) {
      ExecutorService executor = 
            Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);

      List<MatchSet> matchSets = profiles.values().stream()
            .map(profile -> profile.getMatchSet(criteria)) 
            .collect(Collectors.toList());
      for (MatchSet set: matchSets) {
         Runnable runnable = () -> {
            if (set.matches())
               listener.foundMatch(profiles.get(set.getProfileId()), set);
         };
         executor.execute(runnable);
      }
      executor.shutdown();
   }
}
```

위의 코드에서 우리는 애플리케이션을 빠르게 반응하도록 만들고 싶습니다. 따라서 findMatchingProfiles() 메서드를 각각 별도의 스레드 맥락에서 매칭을 계산하도록 설계했습니다. 더욱이 모든 처리가 완료될 때까지 때까지 클라이언트가 블록되는 것이 아니라 findMatchingProfiles() 메서드에 MatchListener 인수를 넣도록 했습니다. 매칭되는 각 프로파일은 MatchListener 인터페이스의 foundMatch() 메서드로 반환됩니다.


### 애프릴케이션 로직 추출
findMatchingProfiles() 메서드는 꽤 짧은 편이지만 여전히 좋은 테스트 과제를 그럭저럭 수행합니다. 이 메서드는 애플리케이션 로직과 스레드 로직을 둘 다 사용합니다. 첫 번쨰 과제는 둘을 불리하는 것입니다.

MatchSet 인터페이스를 모으는 로직을 같은 클래스의 collectMatchSets() 메서드로 추출합니다.

```
public void findMatchingProfiles(
         Criteria criteria, MatchListener listener) {
      ExecutorService executor = 
            Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);
      for (MatchSet set: collectMatchSets(criteria)) {
         Runnable runnable = () -> {
            if (set.matches())
               listener.foundMatch(profiles.get(set.getProfileId()), set);
         };
         executor.execute(runnable);
      }
      executor.shutdown();
   }

   List<MatchSet> collectMatchSets(Criteria criteria) {
      List<MatchSet> matchSets = profiles.values().stream()
            .map(profile -> profile.getMatchSet(criteria))
            .collect(Collectors.toList());
      return matchSets;
   }
```

유사하게 매칭된 프로파일 정보를 리스너로 넘기는 애플리케이션 로직도 추출합니다.

```
public void findMatchingProfiles(
         Criteria criteria, MatchListener listener) {
      ExecutorService executor = 
            Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);

      for (MatchSet set: collectMatchSets(criteria)) {
         Runnable runnable = () -> process(listener, set);
         executor.execute(runnable);
      }
      executor.shutdown();
   }

   void process(MatchListener listener, MatchSet set) {
      if (set.matches())
         listener.foundMatch(profiles.get(set.getProfileId()), set);
   } 

   List<MatchSet> collectMatchSets(Criteria criteria) {
      List<MatchSet> matchSets = profiles.values().stream()
            .map(profile -> profile.getMatchSet(criteria))
            .collect(Collectors.toList());
      return matchSets;
   }
```

### 스레드 로직의 테스트 지원을 위해 재설계
테스트 코드에서 ExecutorService 인스턴스에 접근할 필요가 있습니다. 따라서 그것의 초기화를 필드 수준으로 추출하고 ExecutorService 참조를 반환하는 패키지-접근-수준의 메서드를 제공합니다.

이미 process()메서드를 테스트했기 때문에 그 메서드는 잘 동작한다고 안전하게 가정할 수 있으며, findMatchingProfiles() 메서드를 테스트할 때는 그 로직을 무시합니다. process() 메서드의 동작을 스텁처리하려고 findMatchingProfiles() 메서드를 오버라이딩합니다. 남아있는 구현에 processFunction 인수를 추가합니다. 그것은 각 스레드에서 실행되는 함수를 나타냅니다. processFunction 함수 참조를 사용하여 각 MatchSet을 처리하는 적절한 로직을 호출합니다.

원래의 원형을 갖는 findMatchingProfiels() 메서드를 다시 추가합니다. 내부적으로는 아서 만든 메서드에 동작을 위임합니다. 함수 인수에 대해서는 this::process를 넘깁니다. 그것은 ProfileMatcher 클래스의 이미 동작이 검증된 process 메서드의 참조입니다.
```
public class ProfileMatcher {
   private Map<String, Profile> profiles = new HashMap<>();
   private static final int DEFAULT_POOL_SIZE = 4;

   public void add(Profile profile) {
      profiles.put(profile.getId(), profile);
   }

   private ExecutorService executor = 
         Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);
   
   ExecutorService getExecutor() {
      return executor;
   }
   
   public void findMatchingProfiles( 
         Criteria criteria, 
         MatchListener listener, 
         List<MatchSet> matchSets,
         BiConsumer<MatchListener, MatchSet> processFunction) {
      for (MatchSet set: matchSets) {
         Runnable runnable = () -> processFunction.accept(listener, set); 
         executor.execute(runnable);
      }
      executor.shutdown();
   }
 
   public void findMatchingProfiles( 
         Criteria criteria, MatchListener listener) { 
      findMatchingProfiles(
            criteria, listener, collectMatchSets(criteria), this::process);
   }

   void process(MatchListener listener, MatchSet set) {
      if (set.matches())
         listener.foundMatch(profiles.get(set.getProfileId()), set);
   }

   List<MatchSet> collectMatchSets(Criteria criteria) {
      List<MatchSet> matchSets = profiles.values().stream()
            .map(profile -> profile.getMatchSet(criteria))
            .collect(Collectors.toList());
      return matchSets;
   }
}
```
## 데이터베이스 테스트

5.2 절에서 StatCompiler 코드를 처음 보앗습니다. 이 클래스를 QuestionController 이 클래스를 QuestionController 인스턴스와 직접 상호 작용하지 않도록 리팩토링했고, 결과적으로 나머지 다수의 로직에 대한 빠른 속도의 테스트를 작성할 수 있었습니다. 이제 QuestionController와 상호작용하는 questionText() 메서드만 남았고, 테스트 코드를 작성하려고 합니다.


```
public Map<Integer,String> questionText(List<BooleanAnswer> answers) {
    Map<Integer,String> questions = new HashMap<>();
    answers.stream().forEach(answer -> {
        if (!questions.containsKey(answer.getQuestionId()))
        questions.put(answer.getQuestionId(), 
            controller.find(answer.getQuestionId()).getText()); });
    return questions;
}
```

### 고마워 Controller

questionTest() 메서드의 테스트를 작성하기 어려운 이유는 자바 영속성 API를 사용하는 포스트그레 DB와 통신하는 controller 변수 때문입니다. 첫 번쨰 질문은 QuestionController 클래스에 관한 것입니다. 그것은 믿을 수 있으며 어떻게 동작하는지 이해하고 있나요? 테스트 작성으로 확인해보고자 합니다. 먼저 대상 클래스입ㄴ디ㅏ.

```
public class QuestionController {
   private Clock clock = Clock.systemUTC();
   // ...

   private static EntityManagerFactory getEntityManagerFactory() {
      return Persistence.createEntityManagerFactory("postgres-ds");
   }
   public Question find(Integer id) {
      return em().find(Question.class, id);
   }
   
   public List<Question> getAll() {
      return em()
         .createQuery("select q from Question q", Question.class)
         .getResultList();
   }
   
   public List<Question> findWithMatchingText(String text) {
      String query = 
         "select q from Question q where q.text like '%" + text + "%'";
      return em().createQuery(query, Question.class) .getResultList();
   }
   
   public int addPercentileQuestion(String text, String[] answerChoices) {
      return persist(new PercentileQuestion(text, answerChoices));
   }

   public int addBooleanQuestion(String text) {
      return persist(new BooleanQuestion(text));
   }

   void setClock(Clock clock) {
      this.clock = clock;
   }
   // ...

   void deleteAll() {
      executeInTransaction(
         (em) -> em.createNativeQuery("delete from Question")
                   .executeUpdate());
   }
   
   private void executeInTransaction(Consumer<EntityManager> func) {
      EntityManager em = em();

      EntityTransaction transaction = em.getTransaction();
      try {
         transaction.begin();
         func.accept(em);
         transaction.commit();
      } catch (Throwable t) {
         t.printStackTrace();
         transaction.rollback();
      }
      finally {
        em.close();
      }
   }
   
   private int persist(Persistable object) {
      object.setCreateTimestamp(clock.instant());
      executeInTransaction((em) -> em.persist(object));
      return object.getId();
   }
   
   private EntityManager em() {
      return getEntityManagerFactory().createEntityManager();
   }
}

```

QuestionController ㅋㄹ래스에 있는 대부분의 로직은 JPA 인터페이스를 구현하는 코드에 대한 단순한 위임입니다. 그다지 흥미로운 로직들은 아닙니다. 하지만 JPA에 대한 의존성을 고립시켰기 때문에 좋은 설계이나, 테스트 관점에서는 의문이 듭니다. QuestionController 클래스에 대한 단위 테스트를 작성하는 것이 의미가 있을까요? jPA 관련 인터페이스를 모두 스텁으로 만들어 단위 테스트할 수도 있지만 노력이 많이들고 테스트도 어려울 것입니다.

그대신 진짜 db와 성공적으로 상호 작용하는 Question이 올바르게 연결되었음을 증명할 것입니다. 결함들은 jAP를 다룰때 꽤 흔한 것입니다. 세 가지의 서로 다른 조각들이 함꼐 동작하고 있기 때문입니다. 자바 코드, 매핑 설정, 데이터베이스 자체입니다. 

### 데이터 문제 
JUnit 테스트의 대다수는 속도가 빠르길 원합니다. 영속적인 모든 상호 작용을 시스템의 한 곳으로 고립시킬 수 있다면 통합 테스트의 대상은 결국 상당히 소규모로 줄어들 것입니다. 

(테스트 목적에서 H2 같은 인메모리 db로 프로덕션 데이터베이스를 모사하고 싶을수도 있습니다. 속도야 빠르겠지만 그 밖의 좋은 운도 필요합니다 .이 경우 인메모리 DB와 프로덕션 db의 미묘한 차이 때문에 버러질 문제점이 있습니다.)

진짜 db와 상호작용하는 통합 테스트를 작성할 때 데이터베이스의 데이터와 그것을 어떻게 가져올지는 매우 중요한 고려사항입니다. 데이터베이스가 기대한 질의 결과가 나온다고 증명하려면 먼저 적절한 데이터를 넣거나 이미 이러한 데이터가 db에 있다고 가정해야 합니다.

데이터가 이미 db에 있다고 가정하는 것은 고통스러운 방법입니다. 시간이 지나면서 데이터는 우리도 모르게 변질될 것이고 테스트도 망가집니다. 테스트 코드와 데이터를 분리시키면 특정 테스트가 왜 통과하거나 실패하는지 그 이유를 이해하기가 어려워집니다. 테스트 고나ㅓㅈㅁ에서 데이터의미는 그것을 모두 데이터베이스에 부어 넣는 순간 사라집니다. 테스트안에서 데이터를 생성하고 관리하도록 합니다. 

다음 질문에 답해봅니다. 어떤 db인가요? 머신에 있는 데이터베이스라면 가장 간단한 경로는 테스트마다 깨끗한 데이터베이스로 시작하는 것입니다(혹은 적절한 참조 데이터를 포함한 기존에 생성된 데이터베이스 인스턴스도 좋습니다.). 매 테스트는 그 다음 자기가 쓸 데이터를 추가하거나 그것을 ㅗ작업합니다. 이렇게 하면 테스트간 의존성 문제를 최소화할 수 있습니다. 테스트간 의존성 문제는 다른 테스트에서 남아 있던 데이터 떄문에 어떤 테스트가 망가지는 것을 의미합니다.

우리의 db가 아니라면, 즉 테스트를 위해 공유된 db에만 접근할 수 있다면 더 비침습적인 해법이 필요합니다. 한 가지 선택 사항은 다음과 같습니다. db가 트랜잭션을 지원한다면 테스트마다 트랜잭션을 초기화하고, 테스트가 끝나면 롤백합니다.(보통 @Before와 @After 메서드에 위임합니다.)
마지막으로 통합 테스트는 작성과 유지보수가 어렵습니다. 자주 망가지고, 그들이 깨졌을 때 문제를 디버깅하는 것도 상당히 오래 걸립니다. 하지만 여전히 테스트 전략의 필수적인 부분입니다.

```
통합테스트는 필수적이지만 설계하고 유지보수하기가 까다롭습니다. 단위 테스트에서 검증하는 로직을 최대화 하는 방향으로 통합 테스트 개수와 복잡도를 최소화합니다.
```

### 클린 룸 db 테스트

controller를 위한 테스트는 매 테스트 메서드의 실행 전후에 db를 비웁니다.

### controller 를 목처리

지금까지는 직접적인 데이터베이스와 모든 상호 작용을 QuestionController 클래스로 고립시키고 테스트했습니다. 이제 StatCompiler 클래스의 questionText() 메서드를 테스트할 차례입니다. QuestionController 클래스는 믿을수 있으므로 그것의 find()를 안전하게 스텁으로 만듭니다.

목에 대해 어떤 가정을 세운다고 생각해봅니다. 목으로 처리한 것은 무엇이고, 그것이 질의에 대해 어떻게 반응하고 어떤 부작용을 발생시키는지 충분히 알고 있어야 합니다. 이러한 지식이 없다면 테스트에 잘못된 가정이 포함된 것입니다. 

## 마치며

두 가지 공통 도전과제인 멀티스레드와 데이터베이스 상호 작요은 그 자체로 험난한 주제입니다. 많은 결함이 이 영역에서 출몰합니다.

일반적으로 이러한 더 어려운 시나리오들에 대해 다음 전략을 따르길 원합니다.

- 관심사를 분리하세요. 애플리케이션 로직은 스레드, 데이터베이스 혹은 문제를 일으킬 수 있는 다른 의존성과 분리하세요. 의존적인 코드는 고립시켜서 코드 베이스에 만연하지 않도록 하세요.
- 느리거나 휘발적인 코드를 목으로 대체하여 단위테스트의 의존성을 끊으세요.
- 필요한 경우에는 통합 테스트를 작성하되, 단순하고 집중적으로 만드세요.

