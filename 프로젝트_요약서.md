# GenAI Agents 프로젝트 요약서

## 프로젝트 개요

**프로젝트명**: Dexter AI Agent  
**목적**: AWS Bedrock Agent를 활용한 멀티 에이전트 시스템 구축  
**주요 기능**: 웹 검색, 콘텐츠 생성, 파일 저장, 이미지 생성을 수행하는 AI 에이전트 협업 시스템

## 아키텍처 구조

### 1. 백엔드 애플리케이션 (`app/backend`)

#### 기술 스택
- **프레임워크**: Spring Boot 3.4.2
- **언어**: Java 17
- **빌드 도구**: Maven
- **주요 의존성**:
  - AWS SDK for Bedrock Agent Runtime
  - AWS SDK for Bedrock Runtime
  - Spring Cloud AWS (SQS, S3)
  - JSoup (웹 파싱)

#### 핵심 컴포넌트

##### 1.1 AgentService
- **위치**: `com.ai.agent.backend.agent.service.AgentService`
- **역할**: Bedrock Agent와의 통신 관리
- **주요 기능**:
  - Agent 호출 및 응답 처리
  - Return of Control 이벤트 처리
  - 비동기 Agent 재호출
  - 세션 상태 관리

##### 1.2 AgentAction 시스템
- **기본 클래스**: `AgentAction` (추상 클래스)
- **팩토리 패턴**: `AgentActionFactory`를 통한 액션 생성
- **지원 액션 그룹**:
  - `WEB_SEARCH`: 구글 검색 및 웹 콘텐츠 파싱
  - `SAVE`: S3 파일 저장
  - `IMAGE_GENERATION`: Bedrock을 통한 이미지 생성

##### 1.3 웹 검색 기능
- **구현체**: `GoogleSearch`
- **기능**: 
  - 구글 검색 API 연동
  - JSoup을 활용한 웹 콘텐츠 추출
  - 검색 결과 파싱 및 반환

##### 1.4 API 엔드포인트
- **컨트롤러**: `AgentController`
- **엔드포인트**: `POST /api/agent`
- **기능**: 사용자 쿼리를 받아 Agent 시스템 호출

### 2. 인프라스트럭처 (`infra`)

#### 기술 스택
- **IaC 도구**: Terraform
- **클라우드 플랫폼**: AWS
- **리전**: ap-northeast-2 (서울)

#### 주요 AWS 리소스

##### 2.1 AI Agent 구성
- **Supervisor Agent**: 전체 워크플로우 조정
- **Web Search Agent**: 웹 검색 전담
- **Writer Agent**: 콘텐츠 작성 전담
- **Foundation Model**: Anthropic Claude 3.5 Sonnet

##### 2.2 Agent 협업 구조
```
Supervisor Agent (조정자)
├── Web Search Collaborator (웹 검색)
└── Writer Collaborator (콘텐츠 작성)
```

##### 2.3 네트워크 인프라
- **VPC**: 10.0.0.0/16 CIDR
- **가용 영역**: ap-northeast-2a, 2b, 2c
- **서브넷**:
  - Private: 10.0.1.0/24
  - Public: 10.0.101.0/24

##### 2.4 스토리지 및 권한
- **S3 버킷**: 파일 저장용
- **IAM 역할**: Bedrock Agent 실행 권한
- **리소스 그룹**: 프로젝트 리소스 관리

## 핵심 기능

### 1. Return of Control 메커니즘
- Bedrock Agent가 외부 API 호출이 필요할 때 백엔드로 제어권 반환
- 백엔드에서 실제 API 실행 후 결과를 Agent에게 다시 전달
- 비동기 처리를 통한 성능 최적화

### 2. 멀티 에이전트 협업
- Supervisor Agent가 작업을 분석하여 적절한 전문 Agent에게 위임
- 각 Agent는 특화된 기능 수행
- 대화 히스토리 공유를 통한 컨텍스트 유지

### 3. 동적 액션 실행
- OpenAPI 스키마 기반 파라미터 자동 매핑
- 리플렉션을 활용한 동적 메서드 호출
- 타입 안전성을 보장하는 파라미터 변환

## 설정 및 환경 변수

### 주요 설정값
```properties
# Bedrock Agent 설정
aws.bedrock.supervisor.agent.id=${AWS_BEDROCK_SUPERVISOR_AGENT_ID}
aws.bedrock.supervisor.agent.alias.id=${AWS_BEDROCK_SUPERVISOR_AGENT_ALIAS_ID}

# AWS 리소스 설정
aws.region=${AWS_REGION}
aws.s3.bucket.name=${AWS_S3_BUCKET_NAME}
```

### Terraform 변수
- `foundation_model`: 사용할 기반 모델
- `instruction_*`: 각 Agent별 지시사항
- `orchestration_prompt_configurations`: 프롬프트 설정
- `environment`: 배포 환경

## 배포 및 운영

### 백엔드 배포
- **컨테이너화**: Docker 지원
- **실행 스크립트**: 
  - `start-backend.sh`: 일반 실행
  - `debug-backend.sh`: 디버그 모드 실행

### 인프라 배포
1. Terraform 초기화: `terraform init`
2. 계획 확인: `terraform plan`
3. 배포 실행: `terraform apply`
4. 환경 변수 파일 자동 생성: `.aws/.env`

## 보안 및 권한

### IAM 정책
- Bedrock 모델 호출 권한
- S3 버킷 접근 권한
- CloudWatch 로그 권한
- 최소 권한 원칙 적용

### 네트워크 보안
- VPC 내 격리된 환경
- 보안 그룹을 통한 트래픽 제어
- 프라이빗 서브넷 활용

## 확장성 및 유지보수

### 모듈화 설계
- Terraform 모듈 기반 인프라 관리
- Spring Boot의 의존성 주입을 활용한 느슨한 결합
- 인터페이스 기반 구현으로 확장성 확보

### 모니터링 및 로깅
- CloudWatch를 통한 로그 수집
- 구조화된 로깅으로 디버깅 지원
- 리소스 그룹을 통한 통합 모니터링

## 향후 개선 방향

### 현재 주석 처리된 기능들
- ECR 기반 컨테이너 배포
- ECS Fargate 서비스 운영
- Lambda 기반 액션 그룹
- SQS 메시지 큐 연동

### 확장 가능한 영역
- 추가 AI 모델 지원
- 더 많은 액션 그룹 구현
- 실시간 스트리밍 응답
- 고급 모니터링 및 알림 시스템

이 프로젝트는 AWS Bedrock Agent의 Return of Control 기능을 활용하여 실제 외부 API와 연동되는 실용적인 AI 에이전트 시스템을 구현한 것으로, 확장 가능하고 유지보수가 용이한 아키텍처를 제공합니다. 