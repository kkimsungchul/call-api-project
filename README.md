# API 호출 모듈

## 목차
- [개발 이유](#개발-이유)
- [개발 환경](#개발-환경)
- [사용방법](#사용방법)
- [클래스 설명](#클래스-설명)

---
## 개발 이유
postman 사용시 API를 반복적으로 작업해야 할 때 너무 불편해서 만들었음.<br>
POST를 사용하여 데이터를 여러개 입력하거나<br>
GET을 사용하여 데이터를 조회하거나<br>
Header에 포함된 값을 알고 싶을때 (이건 그냥 postman 써도됨..)<br>
위와같은 상황에서 사용하려고 만듬


## 개발 환경
### 기본 환경
- Windows 10
- eclipse 2021-12 (4.22.0) / IntelliJ IDEA 2023.1 (Ultimate Edition)

### 서버 환경
- JDK1.8 / JDK17 
- maven


## 사용방법
main메소드 참고


## 클래스 설명

### CallApi.java
- API를 호출하는 클래스

#### callRestAPI 메소드
    받아온 json 데이터를 ObjectMapper로 파싱하여서 map으로 리턴중

- data 

```text
- method : Http method (Get / Post / Header - 개발중)
- url : 호출할 URL
- jsonData  : 보낼 데이터 (Post 방식에 사용)
- keyMap 헤더에 포함할 API 인증 key 값 (Map<String,String>)
```

- result data

``` Map
{
	key = data,
	key = data
}
```

- 메소드 상단 파라메터 순서

```java
public Map<String,Object> callRestAPI(String ToolType ,String method ,String url , String jsonData) {
	...중략...
}
```

- Exception 

```text
MismatchedInputException : 받아온 데이터가 없을 때 ObjectMapper 에서 데이터를 파싱 시도 시 발생
```

### ReadFile.java
- 파일을 읽어오는 클래스
- 프로젝트 내에 있는 dataFolder 폴더에 데이터파일(txt) 를 넣은 후 사용하면 됨
- 한줄씩 읽어오며, 파싱은 직접해야함
- json 데이터 양식일 경우 ObjectMapper 클래스를 사용하여 파싱하면 됨
- 경로는 최상단부터 시작이므로, 절대경로로 사용할 때는 경로를 c:\\ 부터 입력

###  JsonUtils.java 
- ObjectMapper사용하여 Json 데이터와 Map데이터를 변환
#### convertMapToJson 메소드 
    Map 을 Json 타입으로 변환
#### convertJsonToMap 메소드
    Json 데이터를 map으로 변환
