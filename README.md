```markdown
# IMDb Info Hub

## Run with Docker

```bash
docker build -t movieinfo-app .
docker run -d -p 8080:8080 --name movieinfo-container movieinfo-app
```

## API Documentation

```
http://localhost:8080/swagger-ui.html
```

## Services

| Service | Endpoint | Description |
|---------|----------|-------------|
| Genre | `/api/v1/genres/{genre}/best-per-year` | Best titles per year for a specific genre |
| Title | `/api/v1/titles/common-by-actors` | Common movies between two actors |
| Title | `/api/v1/titles/same-director-writer-alive` | Titles where director and writer are the same alive person |
| Stats | `/api/v1/stats/requests` | Total HTTP request count |

## Database

H2 Database - Data files are downloaded from IMDb website at runtime.
```
