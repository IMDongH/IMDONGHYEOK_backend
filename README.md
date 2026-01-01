이 프로젝트는 송금 서비스 테스트 프로젝트입니다.

## 📂 프로젝트 구조 (Project Structure)

프로젝트는 크게 `core`, `storage`, `support` 세 가지 레이어로 구분됩니다.

```
transfer-practice
├── core
│   ├── core-api        # 클라이언트 요청을 처리하는 API 서버 (Presentation Layer)
│   └── core-enum       # 전역에서 사용되는 Enum 클래스 모음
├── storage
│   └── db-core         # 데이터베이스 접근 및 영속성 관리 (Persistence Layer)
└── support
    ├── logging         # 로깅 설정 및 유틸리티
    └── monitoring      # 모니터링(Actuator, Prometheus 등) 설정
```

## 🚀 실행 방법 (How to Run)

### 사전 요구사항 (Prerequisites)
- Docker & Docker Compose

### 애플리케이션 실행 (Run Application)
**도커 컨테이너 실행 (Run with Docker)**
프로젝트 루트에서 다음 명령어를 실행하면 **빌드와 실행이 자동으로 수행**됩니다.
```bash
docker-compose up -d --build
```

3. **접속 정보 (Access)**
   - **API Server**: http://localhost:8080
   - **MySQL**: localhost:3306 (user: root / password: root)
   - **swagger**: http://localhost:8080/swagger-ui/index.html
## 📚 문서 (Documents)
- [모듈 흐름 설명 및 코드 흐름 구조](docs/ARCHITECTURE.md): 멀티 모듈의 모듈 간 흐름 및 코드 흐름에 대한 상세 설명입니다.
- [ERD (Entity Relationship Diagram)](docs/ERD.md): 데이터베이스 스키마 구조 문서입니다.
