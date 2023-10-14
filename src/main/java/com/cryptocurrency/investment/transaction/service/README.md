- 기존

첫번쨰 아이디어

PriorityBlockingQueue (시간, 가격) 를 사용홰서 Consumer / Producer 구조를 만들고 최신시간을 받아올 수 있도록 구성

가격데이터들을 받을떄마다 Object Pool 에 Job 을 저장해두고 실행시킨다.

Object Pool 을 만든 이유

성능테스트 당시 H2DB 를 사용해서 Mysql 과 얼마나 차이가 있는지를 몰랐다.