# 프로젝트에서 테스트

- 빠른 도입
- 팀과 같은편 되기
- 지속적 통합으로 수렴
- 코드 커버리지
- 마치며

보통 일을하게 되면 다른 팀원들과 프로젝트를 진행하고 있을 것인데, 아마도 단위 테스트에 대해 팀원들과 같은 생각을 하길 원할 것입니다. 이 장에서는 팀원들과 끝없는 언쟁과 코드 충돌로 시간 낭비를 피할 수 있는 표준을 배울 것입니다.

---
## 빠른 도입 

단위 테스트와 같은 실천법을 배우는 것은 끊임없는 경계를 요구합니다. 단위 테스트 작성을 즐기고 새로운 코드에 잘 적용할 수 있다고 해도 보통은 힘든 싸움에 직면합니다.

아마 팀 동료는 그다지 조심스럽지 않게 테스트 코드보다 훨씬 빨리 코드를 만들 것입니다. 혹은 중요한 마감 시간이 다가올 떄 팀에서 마감 시간을 맞추는 유일한 방법은 모든 보호 장치를 거두는 것이라고 주장할 수도 있습니다.

불행하게도 우리가 아무리 개발에 뛰어나다고 해도 불가피한 마지막 순간의 진퇴양난에서 얻을 수 있는 것은 많지 않습니다. 할 수 있는 것은 협상뿐입니다.

하지만 다행하게도 첫날부터 품질을 통제하며 개발하길 주장하면 이러한 벽에 부딪히는 횟수를 줄일 수 있습니다.

단위테스트는 이러한 품질 통제의 일부가 됩니다. 어떻게 단위 테스트가 팀 문화의 습관적인 일부가 될 수 있을지 토론해 보세요.

## 팀과 같은편 되기

개발자들이 단위 테스트에 접근하는 방식은 개인별로 매우 다릅니다. 어떤 개발자들은 TDD를 주장합니다. 다른 사람들은 그들이 필요하다고 느낀 테스트에서만 단위 테스트를 주장할 것입니다. 일부 개발자들은 다수의 케이스를 단일 테스트 메서드로 몰아넣기를 좋아합니다. 또는 느린 통합 테스트를 선호하기도 합니다. 분명한 것은 책에서 읽은 권장 사항을 모든 사람이 동의하지는 않는다는 점입니다. 

팀이 같은편이 되는 것은 중요합니다. 오랜 언쟁은 다른 사람의 시간을 뺴앗는 일이 됩니다. 모든 것에 동의할 수 없을지라도 적어도 어떤 것에는 동의하고 합의점을 늘려 가는 방향으로 시작해야 합니다.

### 단위 테스트 표준 만들기

초창기에 표준화해야하는 목록은 다음과 같습니다.
- 코드를 체크인하기 전에 어떤 테스트를 실행할지 여부
- 테스트 클래스와 메서드의 이름 짓는 방식
- 햄크레스트 혹은 전통적인 단언 사용 여부
- AAA 사용 여부
- 선호하는 목 도구 선택
- 체크인 테스트를 실행할 때 콘솔에 출력을 허용할지 여부
- 단위 테스트 스위트에서 느린 테스트를 분명하게 식별하고 막을 방법

### 리뷰로 표준 준수 높이기

팀원들의 리뷰를 통해 테스트의 표준을 준수하도록 한다.


### 짝 프로그래밍을 이용한 리뷰

소프트웨어 개발 세상에서 페어 프로그래밍만큼 많은 논란을 일으킨 주제는 없습니다. 이것은 두 프로그래머가 함께 나란히 앉아서 소프트웨어를 개발하는 것입니다. 잘되면 짞 프로그래밍은 그중 한 명이 했을 때보다 설계가 좋고, 한 명보다는 두 명이 함꼐하는 것이 낫다는 해법을 만듭니다. 짝 프로그래밍 옹호자는 그것이 적극적인 형태의 리뷰라고 주장합니다.

사후 리뷰에는 몇 가지 도전 과제가 있습니다. 보통 리뷰어들은 리뷰를 받는 코드의 정통한 세부 내용에 익숙하지 않습니다. 최상의 ㄹ비ㅠ는 코드를 깊이 이해한 사람에게서 나옵니다. 하지만 현실적으로 많은 회사에 시간적 여유가 없습니다. 결과적으로 리뷰는 바라는 것보다 더 적은 결함을 찾게 됩니다. 교정된 결함의 종류도 일반적으로는 표면 수준입니다. 사후 리뷰는 가치가 있지만 들이는 노력만큼 그 가치가 높지는 않습니다.

게다가 사후 리뷰는 심각한 문제를 고치는 데는 너무 늦습니다. 코드가 만들어지고 배포될 준비를 한 후, 팀은 이미 동작한다고 알려진 코드를 되돌리거나 심각하게 재작업하는 것을 부담스러워 합니다. 개발자들은 동료나 관리자, 자기 자신에게서 앞으로 나아가야 한다는 압력을 받습니다.

한편 짞 프로그래밍은 두 번째 사람의 눈으로 시작부터 품질이 좋은 코드를 만들 수 있다는 희망을 줄 수 있습니다. 이것이 일어나는 한 가지 방향은 더 많고 좋은 단위 테스트를 지속하는 것입니다. 

## 지속적 통합으로 수렴

단위 테스트는 이러한 모든 문제를 해결해 주지 않지만 일종의 표준입니다. 코드에 대한 어떤  변경이 집합적인 테스트를 망가트릴 수는 없습니다. 그냥 표준들이 위반된 것이죠..

단위 테스트를 팀 차원의 표준으로 바라보려면 공유 저장소가 필요합니다. 개발자들은 저장소에서 코드를 체크아웃하고, 변경점을 만들어 로컬에서 테스트하고 그다음 코드를 다시 공유 저장소로 체크합니다.

옛 사고방식의 최전선은 공유된 코드에 대해 야간 빌드를 수행하는 것입니다. 모두 잘 되어 있으면 통합된 코드도 잘 동작할 것입니다. 적어도 이론상으로는 그렇습니다.

이러한 야간 빌드에 단이와 다른 자동화 테스트를 추가하면 그 가치를 극적으로 향상시킬 수 있습니다. 최근의 변경점들을 모두 통합했을 때 소프트웨어가 여러분의 것이 아닌 다른 머신에서도 테스트를 모두 통고했다는 것을 알면 배포에 자신감도 생길 것입니다.

올바른 방향으로 큰 진전이기는 하지만 야간 빌드는 예스럽고 부적절해 보입니다. 개발 팀은 하루에도 시스템에 코드 수백 줄을 추가합니다. 다른 개발자들이 더 많은 코드를 추가할수록 코드를 합쳤을 때 동작하지 않을 가능성이 늘어납니다. 야간 빌드로 통합된 소프트웨어를 테스트한다면 충돌을 발견하기까지 거의 만 하루가 걸릴 것입니다. 합친 코드가 어떻게 동작하는지 확인하고 문제를 찾는 데는 또 하루가 걸릴 것입니다. 충돌이 발생한 코드 영역을 병합하는데 또 하루가 소요될 수 있습니다.

지속적인 통합(CI)의 개념으로 들어오세요. 만 하루를 기다리는 것은 어리석게 보입니다. 여러분은 좀 더빠른 피드백을 원합니다. CI는 코드를 더 자주 통합하고 그 결과를 매번 검증하는 것을 의미합니다. 코드를 변경점과 합쳤을 때 동작하지 않는다는 것을 빨리 알수록 팀 성과는 더 좋아집니다.

CI의 실천은 지속적 통합 서버라는 도구의 지원을 받아야 합니다. CI서버는 소스 저장소를 모니터링합니다. 새로운 코드가 체크인 되면 CI서버는 소스 저장소에서 코드를 가져와 빌드를 초기화합니다. 빌드에 문제가 있따면 CI서버는 개발 팀에 통지합니다.

CI 서버가 어떤 가치를 제공하려면 빌드가 단위 테스트를 함꼐 수행해야 합니다. CI 서버 빌드 절차가 소스 저장소의 코드 기록을 바탕으로 하기 떄문에 시스템의 전반적인 건강도 함께 볼 수 있습니다. "내 머신에서 내 변경점이 동작함" 혹은 "당신의 머신에서 당신의 변경점이 동작함"이 아니라 "우리의 코드가 우리의 황금 서버중 하나에서 동작함"이 맞습니다.

CI서버는 나쁜 코드를 용납하지 않도록 건강한 동료 압박을 지원합니다. 개발자들은 습관적으로 스스로에게 체크인하기 전에 단위 테스트를 먼저 돌려보게 됩니다. 어느 누구도 CI 빌드 절차가 실패하여 다른 티원들의 시간을 낭비하고 싶지 않을 것입니다.

전형적인 CI 서버를 설치하고 설정하는 데 하루 혹은 이틀이 필요하지만, 그만큼 투자할 가치가 있습니다. 요즘 CI서버의 사용은 기본입니다.

```
CI서버는 현대 개발 팀을 구성하는 최소 요건입니다.
```

## 코드 커버리지

코드 커버리지라는 개념(얼마나 많은 코드가 단위테스트되었나)은 전형적인 고나리자의 숫자 강박을 자극하지만, 궁극적으로 교육 목적이 아닌 곳에서 사용했을 때는 씁쓸한 기분을 남길 수 있습니다. 

좀 더 구체적으로 코드 커버리지는 단위 테스트가 실행한 코드의 전체 퍼센트를 측정하는 것입니다. 귀찮은 측정 작업을 해 주는 도구들을  찾을 수 있습니다. 엠마와 코버투라는 코드 커버리지 도구의 예입니다.

### 커버리지는 어느 정도여야 하는가?

0%는 충분하지 않고 100%는 훌륭해 보이지만 과연 현실적인가??

커버리지 개념은 오로지 속임수를 써야만 100%에 도달할 수 있다는 제한이 내재되어 있습니다. 인자 없는 생성자를 제공하는 하이버네이트 같은 프레임워크를 쓴다고 상상해봅니다. 한편 테스트 코드와 클라이언트 코드는 인자를 한 개 갖는 오버로드된 생성자를 사용합니다. 인자가 없는 생성자는 테스트 코드에서 직접 호출하지 않기 떄문에 코드 커버리지에 포함되지 않습니다. 그러면 안되지만 100%맞추기 위해 단지 클래스만 인스턴스화 시키는 단위 테스트를 작성할 수도 있습니다.

대부분의 사람은 70%이하의 커버리지는 불충분하다고 말합니다. 많은 개발자가 단위 테스트에 더 많은 시간을 투자하는 것은 '한계 효용 체감의 법칙'이 적용된다고 말합니다. 

코드를 작성하고 습관적으로 단위 테스트를 작성하는 팀들은 비교적 쉽게 70%의 커버리지를 달성합니다. 커버리지 밖의 코드 1/3 정도는 테스트 되지 않은 상태인데, 보통 나쁜 의존성 때문에 그 코드가 어렵거나 테스트하기 어렵기 떄문입니다. 정말 이상한 것은 코드 결함의 30%는 이러한 테스트되지 않은 코드에 있고, 실제로 그 숫자는 더 높을 것입니다. 어려운 코드는 많은 결함을 숨기기 마련입니다.

**제프의 코드 커버리지 이론** : 낮은 커버리지의 영역에서 나쁜 코드의 양도 증가합니다.

### 100% 커버리지는 진짜 좋은가??

TDD를 수행하는 개발자들은 일반적으로 정의상 90%를 초과 달성 합니다. 그들은 작성하려는 코드를 설명하기 위해 단위 테스트를 항상 먼저 작성합니다. TDD는 테스트를 자기 충족적인 예언으로 만듭니다.

커버리지 테스트 자체는 오도될 수 있습니다. 테스트 몇 개에 대량의 코드 커버리지를 달성하지만 단언은 거의 없는 형태가 되기 쉽습니다. 커버리지 도구는 단일 단언을 사용했는지 여부를 신경쓰지 않습니다. 또 이해하고 유지보수하기 어려우며 가치 있는 단언을 포함하지 않은 나쁜 테스트를 작성했을 수도 있습니다. 많은 팀이 높은 커버리지만 달성하고 가치는 별로 없는 단위 테스트를 작성하느라 시간 낭비하는 것을 보았습니다.


### 코드 커버리지의 가치
특히 단위 테스트 여행을 시작하면서 테스트가 어느 코드를 커버하고 그렇지 않은지 알고 싶을 것입니다. 엠마 같은 도구의 아름다운 측면은 커버리지에서 누락하고 있는 부분을 가시적으로 보여준다는 것입니다.

테스트 작성을 완료했다고 생각할 떄 커버리지 도구를 실행하세요. 아직 커버되지 않은 영역을 보세요. 커버하지 않은 코드 영역을 염려한다면 더 많은 테스트를 작성하세요. 커버리지 도구를 주기적으로 바라보면 지속적으로 단위 테스트에 솔직해질 수 있습니다.

코드 커버리지 숫자는 그 자체로 의미가 없습니다. 하지만 코드 커버리지의 추세는 중요합니다. 팀은 시간이 지나면서 커버리지 퍼센트가 높아져야하고 적어도 아래 방향으로 내려가면 안됩니다.
``` 
코드 커버리지 도구는 코드가 어디서 커버리지가 부족한지, 팀이 어디에서 아래 방향으로 내려가고 있는지 이해하려고 할 떄만 사용하세요
```

## 마치며

지금까지 우리는 함꼐 단위 테스트를 배우고 실천했습니다. 우리는 모니터만 보았지만 주변에 팀원들이 있었습니다. 반응드을 보며 팀에 단위 테스트를 도입할 때 주의해야 할 고려 사항들을 알아보았습니다.