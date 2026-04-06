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
