# 산타 마니또 - Santa Manitto

<img src="https://user-images.githubusercontent.com/57310034/111138328-258c4a00-85c3-11eb-813e-f66dfff116c1.png"/>

[![Release](https://img.shields.io/endpoint?color=green&logo=google-play&logoColor=green&url=https%3A%2F%2Fplayshields.herokuapp.com%2Fplay%3Fi%3Dorg.sopt.santamanitto%26l%3DPlayStore%26m%3D%24version)](https://play.google.com/store/apps/details?id=org.sopt.santamanitto)

아이스브레이킹을 위한 안드로이드 마니또 어플리케이션  

단말기의 `ANDROID_ID`를 키 값으로 간단한 회원가입을 한 후 마니또 방을 만들고 참여 코드를 배포해 참여자를 모으거나 참여코드를 통해 마니또 방에 입장할 수 있습니다. 마니또 랜덤 매칭을 시작하면 참여자들의 산타-마니또 관계를 맺어주고 방장이 작성해둔 미션들을 랜덤으로 부여해줍니다. 정해진 기간 동안 마니또 미션을 수행한 후, 전체 산타-마니또 관계를 확인할 수 있습니다. 특별한 기능을 가지고 반복되는 뷰 요소가 많아 커스텀 뷰를 많이 활용했습니다. 또한 서버와의 데이터 통신이 잦아 적절한 캐싱 작업 등 앱 퍼포먼스를 증가시키기 위해 많은 고민을 했던 프로젝트입니다. 태스크별로 액티비티를 나누고 `NavigationComponent`를 활용해 세부 화면면을 프래그먼트로 나누었습니다. MVVM 패턴으로 설계되어 하나의 태스크마다 하나의 뷰모델을 공유하도록 했고 뷰모델에서 필요한 모듈들은 `Hilt` 를 사용해 주입해주었습니다. 테스트 환경을 구성하기 위해 `BuildVariants` `Flavors`를 mock과 prod로 나누어 실제 서버와 통신하는 모듈, 임의의 더미 데이터를 반환하는 Fake 모듈을 형상에 따라 주입하도록 설계했습니다.

## 태스크  

### 회원가입  

<img height="300" src="https://user-images.githubusercontent.com/57310034/111139958-1e663b80-85c5-11eb-8c40-1cac30fde6c4.png"/>  

앱 실행 시 스플래시 화면에서 `ANDROID_ID`를 통해 회원가입 여부를 파악합니다. 가입되지 않은 단말기라면 회원가입 태스크를 실행합니다. 이름을 입력하고 약관에 모두 동의를 하면 회원가입 과정을 모두 마치고 메인 태스크를 실행한 후, 회원가입 태스크를 종료합니다. 약관을 터치하면 `WebView`가 포함된 프래그먼트로 전환되어 이용 약관과 개인정부 수집 방침을 보여주는 url과 연결됩니다.  

<br>

### 메인

<img height="300" src="https://user-images.githubusercontent.com/57310034/111141123-80737080-85c6-11eb-9c7c-79fcbc2a7f56.png"/>  

회원가입을 완료했거나 `ANDROID_ID`가 서버에 등록되어 있다면 메인 태스크를 실행합니다. "방 입장하기" 버튼을 눌러 참여 코드를 입력할 수 있는 프래그먼트로 전환할 수 있습니다. 메인 프래그먼트 아래에는 참여한 마니또 방의 목록을 서버에서 받아와 보여줍니다. 응답을 받기 전에는 로딩 프로그래스바가 노출되며, 목록을 불러오면 각 아이템 뷰홀더 안에서 세부 정보를 다시 서버에 요청합니다. 마니또 방의 상태(매칭 전, 매칭 후, 결과 발표 후 등)이 실시간으로 변영되지 않기 때문에 바로 위 우측 끝의 새로고침 버튼으로 목록과 상태를 갱신할 수 있도록 했습니다.

<br>

### 방 생성

<img height="300" src="https://user-images.githubusercontent.com/57310034/111142327-e7455980-85c7-11eb-9d7c-69df01e76ec4.png"/>    
<img height="260" src="https://user-images.githubusercontent.com/57310034/111142727-5ae76680-85c8-11eb-8715-735c73cea55b.png"/>

메인 프래그먼트에서 "방 만들기" 버튼을 눌러 방 생성 태스크를 새롭게 실행합니다. "산타 마니또 공개일 설정"과 "산타 마니또 공개 시간"은 마니또가 공개되는 시점에 영향을 주는 설정 요소입니다. 설정한 마니또 공개 시점은 하단에 붉은 글씨로 바로바로 갱신해 보여줍니다. 이를 위해 마니또 공개 시점을 `LiveData`를 상속한 클래스로 만들어두고 날짜와 시간 등을 설정하는 메서드와 최종 날짜 및 시간 등을 읽어오는 메서드들을 포함시켜 하나라도 변경된다면 `postValue(this)` 하도록 했습니다. 따라서 공개일과 공개 시간을 각각 설정하더라도 아래 붉은 글씨의 최종 공개 시점은 실시간으로 갱신될 수 있습니다.  

공개일을 설정하는 "n일 후" 뷰와 공개 시간의 AM/PM을 설정하는 뷰는 커스텀 뷰로 작성되었습니다. 그리고 공개 시간을 선택하면 시간을 설정할 수 있는 휠을 포함한 다이얼로그를 볼 수 있는데, 이 시간 선택 휠은 `RecyclerView`를 상속하여 만든 커스텀 뷰입니다. `SnapHelper`를 적용해 선택된 요소가 정중앙에 배치되고 선택되지 않은 요소는 선택된 요소와 멀어질 수록 폰트 사이즈가 작아지도록 구현했습니다.  

각 단계별로 입력된 정보들은 하나의 뷰모델에 저장되고, 최종적으로 방 만들기 버튼을 누르면 입력한 데이터를 토대로 서버에 방 생성 요청을 보냅니다. 그리고 참여코드를 응답으로 받아 이를 복사할 수 있는 다이얼로그를 띄운 다음, 방 생성 태스크를 종료하고 마니또 방 태스크를 실행합니다.

<br>  

### 마니또 방  

<img height="650" src="https://user-images.githubusercontent.com/57310034/111144047-e31a3b80-85c9-11eb-85c2-801b5e63ed58.png"/>  

메인 프래그먼트 하단에 위치한 "나의 산타 마니또" 목록 중 하나를 누르거나 방을 생성한 후에는 마니또 방 태스크가 실행됩니다. 메인 프래그먼트에서 진입한 경우, 터치한 마니또 방의 상태를 `Intent`로 전달 받습니다. 그리고 각 상태에 따라 `waitingRoomFragment`에 머물거나 `matchingFragment`나 `finishFragment`로 바로 전환됩니다. 기본 값은 `waitingRoomFragment`인데, 방을 막 생성하여 진입한 경우에는 상태를 전달하지 않아 `waitingRoomFramgent`에 머물게 됩니다. 참여코드를 통해 참여자가 모이면 목록에 반영되고, 방장이 "마니또 랜덤 매칭하기" 버튼을 누르면 룰렛이 돌아가는 매칭 프래그먼트를 거쳐 자신이 챙겨줘야 할 마니또의 이름과 수행해야할 미션을 보여주는 프래그먼트로 전환됩니다. 룰렛은 최소 2초간 돌아가고, 네트워크 응답이 지연되면 2초 이상 유지될 수 있습니다.  

메인 화면에서 마니또가 공개된 상태의 방을 누르면 `Intent`로 전달된 상태 값으로 인해 곧바로 `finishFragment`로 진입합니다. 자신을 챙겨줬던 산타의 이름과 산타가 수행했어야 할 미션을 보여줍니다. 전체 결과 보기 버튼을 누르면 마니또 방에 참가한 모든 사람들의 산타-마니또 관계를 한 눈에 볼 수 있습니다. 전체 결과 요청 시, 사용자 id만 반환하기 때문에 뷰 홀더에서 또 한번 사용자 id에 맞는 사용자 이름을 각각 요청합니다.  

각 프래그먼트는 한 번 전환된 후에는 이전 화면으로 돌아갈 수 없도록 기획되었기 때문에 프래그먼트 전환과 동시에 백스택을 모두 비웁니다.  

<br>

## 캐싱  

```kotlin
class UserCachedDataSource(
    @Named("remote") private val userRemoteDataSource: UserDataSource,
    private val accessTokenContainer: AccessTokenContainer
): UserDataSource {

    //로그인 유저 정보
    private var _cachedLoginUser: LoginUser? = null
    val cachedLoginUser: LoginUser?
        get() = _cachedLoginUser

    //참가한 마니또 방 목록
    private var _cachedJoinedRooms: List<JoinedRoom>? = null
    val cachedJoinedRooms: List<JoinedRoom>?
        get() = _cachedJoinedRooms

    var isJoinedRoomDirty = false

    //다른 사용자 정보
    val cachedUsers = HashMap<Int, User>()

    ...
}
```  

### 로그인 유저 정보

앱을 사용하고 있는 사용자의 정보는 최초 로그인 이후 변하지 않는 데이터입니다. 따라서 최초 로그인 시 응답으로 받은 id와 이름 등을 `_cachedLoginUser`에 캐싱해두고 이후 자신에 대한 데이터를 요청할 때는 캐싱된 데이터를 반환합니다.  

### 참가한 마니또 방 목록 

사용자가 참가한 마니또 방의 목록은 갱신되어야 할 조건이 명확합니다. 

1. 방을 생성했을 때 
2. 방에 참가했을 때 
3. 새로고침 버튼을 눌렀을 때  

위 조건을 만족하는 상황에 진입하면 `isJoindRoomDirty`의 값을 `true`로 변경합니다. 메인 프래그먼트의 "나의 산타 마니또" 목록은 `onResume`에서 매번 갱신하지만 `isJoindRoomDirty`가 `true`일 때에만 서버에 데이터를 요청하고 `false`일 때는 캐싱된 데이터를 반환합니다. 이런 방법으로 불필요한 네트워크 요청을 최소화하였습니다.  

### 다른 사용자 정보  

같은 마니또 방에 참여한 사용자에 대한 정보도 한 번 서버에 요청한 이후로 변하지 않습니다. `HashMap`을 사용해 사용자 id와 사용자 이름을 키-값 쌍으로 캐싱합니다. 여기에 저장되지 않은 사용자 id는 서버에 요청합니다.  

<br>

## BuildVariants  

```gradle
productFlavors {
    mock {
        dimension "default"
        applicationIdSuffix = ".mock"
    }
    prod {
        dimension "default"
    }
}

android.variantFilter { variant ->
    if (variant.buildType.name == 'release'
        && variant.getFlavors().get(0).name == 'mock') {
            variant.setIgnore(true)
    }
}
```  

<img width="400" src="https://user-images.githubusercontent.com/57310034/111147872-4c9c4900-85ce-11eb-9adb-1dca6c1ff367.png"/>  

mock, prod Flavors를 추가해 위와 같이 빌드 형상을 나누어 개발했습니다. mockDebug 형상에서는 네트워크 요청 모듈 대신 더미 데이터를 반환하는 Fake 객체를 주입했고 prodDebug, prodRelease 형상에서는 실제 네트워크 요청을 수행하는 모듈을 주입해 원하는 테스트 환경을 쉽게 구성할 수 있도록 하였습니다.  

## 사용 라이브러리  

- [firebase-analytics-ktx](https://firebase.google.com/docs/analytics?authuser=4) : 앱을 사용하는 유저들의 현황을 파악하기 위해 사용했습니다.
- [firebase-crashlytics-ktx](https://firebase.google.com/docs/crashlytics?authuser=4) : 미처 대응하지 못한 비정상 종료의 원인을 파악하고 수정하기 위해 사용했습니다.
- [firebase-config-ktx](https://firebase.google.com/docs/remote-config) : 서버 점검 시 별도의 업데이트 없이 간편하게 점검 팝업을 띄우기 위해 사용했습니다.
- [hilt-android](https://github.com/google/dagger) : DI를 간편하게 적용하기 위해 사용하였습니다.  
- [retrofit](https://github.com/square/retrofit) : 서버와의 HTTP 통신을 위해 사용했습니다.
- 그 외 : `Room`, `navigation-fragment-ktx`, `lifecycle-extensions` 등  

<br>

## 개발 환경  

- IDE : AndroidStudio Iguana
- Platform : Android
- SdkVersion
    - compile : 34
    - target : 34
    - min : 23
- Language : Kotlin

<br>

## 개발자  

- [김성규](https://github.com/SEONGGYU96)
- [김상호](https://github.com/Marchbreeze)
- [이태희](https://github.com/haeti-dev)
