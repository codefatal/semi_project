# KH CoinComunity(KCC)
개인 프로젝트 : 유수현
#
## Contents
1. 프로젝트 소개
2. 설계의 주안점
3. 개발환경
4. 프로젝트 기능구현
5. 주요기능
6. Document
#
## 프로젝트 소개
- 암호화폐에 대해 잘 모르거나 실전에 들어가기전에 연습을 먼저 해보고 싶은 사람들을 위한
모의투자 플랫폼 구성
- 암호화폐 같은 경우 주식과 달리 모의투자를 하는 사이트가 거의 존재하지 않거나 기간제로만 존재하여
이 사이트를 기획
#
## 설계의 주안점
- 실용성 : 배포 시 실제로 사용가능하고 연습에 도움이 될 수 있도록 구성
- API 사용 경험 : 실제로 배포되고 있는 API를 사용함으로서 API연동에 대한 이해도를 높힘
#
## 개발환경
|Category|Detail|
|--------|------|
|FrontEnd|HTML5, CSS, JavaScript, JQuery, Bootstrap|
|BackEnd|Java(JDK 11), SpringBoot(2.7.14)|
|OS|Windows 10|
|IDE|SpringToolSuite4, VSCode|
|Server|Tomcat 9.0|
|CI|Github|
|DateBase|SQL Developer, Oracle(21c)|
|API|Bithumb API, TradingView Widget|
#
## 프로젝트 기능구현
- UI설계 / 템플릿을 이용한 뼈대 구성 / 헤더 각 메뉴 구현 - 홈 / 코인시세정보 / 코인 등락폭 정보 / 모의투자(로그인시) / 모의투자랭킹(로그인시) / 로그인 / 회원가입 / 로그아웃(로그인시) / 마이페이지(로그인시)
- 뉴스 메뉴에서 TradingView 뉴스 흐름 페이지 크롤링 진행 / 코인시세정보, 코인 등락폭 정보, 메인페이지 TradingView Widget 설정
- 로그인 - 로그인 시 DB의 유저 정보와 비교 / 회원가입 - 유효성 검사 및 DB에 회원정보 저장 / 모의투자 - Bithumb API 활용 / 모의투자 랭킹 - 랭킹 조회, 수익금액에 따른 내림차순
- 거래내역 - 매수, 매도 진행 시의 정보를 테이블에 저장하여 거래내역 페이지에서 전체/매수/매도 버튼을 통해 조회 가능
#
## 주요기능
[발표자료](https://docs.google.com/presentation/d/1_bz8sFVTaePa7R9fB2huYsMGf9IVvGGfLQd0O7OMOJQ/edit#slide=id.g25ded1f5d71_0_83, "발표자료")



