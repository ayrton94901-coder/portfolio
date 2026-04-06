# Portfolio (Spring Boot)

## Requirements
- Java 21

## Run (port 8081)
```bash
git clone https://github.com/ayrton94901-coder/portfolio.git
cd portfolio/portfolio
./mvnw spring-boot:run
```

Open: http://localhost:8081

## Notes
If you see "Port 8081 was already in use", stop the process using the port:

```bash
lsof -nP -iTCP:8081 -sTCP:LISTEN
kill <PID>
```
1.ログイン画面
2.新規登録画面
3.投稿一覧画面
4.プロフィール画面
5.詳細画面
6.プロフィール編集画面
7.投稿画面
