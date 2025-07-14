# Deployment Guide

## Local Development Setup

### Prerequisites
- Java 17+
- Docker & Docker Compose
- Maven 3.6+

### Step-by-Step Setup

1. **Clone Repository**
   ```bash
   git clone <repository-url>
   cd course-search-api
   ```
2. **Start Elasticsearch**
```
docker-compose up -d
```
3. **Verify Elasticsearch**
```
curl http://localhost:9200
# Should return cluster information
```
4. **Build Application**
```
mvn clean compile
```

5. **Run Application**
```
mvn spring-boot:run
```
6. **Verify Application**
```
curl http://localhost:8080/api/health
# Should return: "Course Search API is running!"
```
7. **Test API**

```
./quick-test.sh
# Or run comprehensive tests:
./test-api.sh
```

## Production Deployment
Docker Deployment
1. Create Dockerfile
```
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/course-search-api-*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```
2. Build Docker Image
```
mvn clean package
docker build -t course-search-api .
```
3. Run with Docker Compose
```
version: '3.8'
services:
app:
image: course-search-api
ports:
- "8080:8080"
depends_on:
- elasticsearch
environment:
- SPRING_ELASTICSEARCH_URIS=http://elasticsearch:9200

elasticsearch:
image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
environment:
- discovery.type=single-node
- xpack.security.enabled=false
ports:
- "9200:9200"
```
4. Environment Variables
```
# Elasticsearch Configuration
SPRING_ELASTICSEARCH_URIS=http://elasticsearch:9200
SPRING_ELASTICSEARCH_CONNECTION_TIMEOUT=10s
SPRING_ELASTICSEARCH_SOCKET_TIMEOUT=30s

# Application Configuration
SERVER_PORT=8080
LOGGING_LEVEL_COM_UNDOSCHOOL_COURSESEARCH=INFO
```
## Troubleshooting
Common Issues
Elasticsearch Connection Failed

```
# Check if Elasticsearch is running
docker ps | grep elasticsearch

# Check Elasticsearch logs
docker-compose logs elasticsearch

# Restart Elasticsearch
docker-compose restart elasticsearch
```
Data Not Loading

```
# Check if index exists
curl http://localhost:9200/_cat/indices

# Check document count
curl http://localhost:9200/courses/_count

# Restart application to reload data
mvn spring-boot:run
```
Search Not Working

```
# Check application logs
tail -f logs/application.log

# Test Elasticsearch directly
curl "http://localhost:9200/courses/_search?q=math"
```
## Performance Tuning
Elasticsearch JVM Settings
```
elasticsearch:
environment:
- "ES_JAVA_OPTS=-Xms1g -Xmx1g"
```
Application JVM Settings

```
java -Xms512m -Xmx1g -jar course-search-api.jar
```

Connection Pool Settings

```
spring.elasticsearch.connection-timeout=30s
spring.elasticsearch.socket-timeout=60s
```
## Monitoring
Health Checks

# Application health
curl http://localhost:8080/api/health

# Elasticsearch health
curl http://localhost:9200/_cluster/health
Metrics
Copy
# Document count
curl http://localhost:9200/courses/_count

# Index statistics
curl http://localhost:9200/courses/_stats