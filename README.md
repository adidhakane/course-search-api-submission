# Course Search API

A Spring Boot application that provides course search functionality using Elasticsearch.

## 🎯 Project Overview

This application indexes course documents into Elasticsearch and exposes REST endpoints for:
- Searching courses with multiple filters, pagination, and sorting
- Autocomplete suggestions for course titles (Bonus)
- Fuzzy matching for course titles (Bonus)

## 🛠️ Technology Stack

- **Java**: 21
- **Spring Boot**: 3.1.5
- **Elasticsearch**: 8.11.0
- **Docker & Docker Compose**: For Elasticsearch setup
- **Maven**: Build tool

## 📋 Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Maven 3.6+
- Git

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/adidhakane/course-search-api-submission
cd course-search-api
```
### 2. Start Docker Desktop & then Elasticsearch
```bash
docker-compose up -d
```
### 3. Wait for sometime (1 or 2 minutes) & then is Verify Elasticsearch is Running
```bash
   curl http://localhost:9200
```
Expected response:
```json
{
  "name" : "elasticsearch",
  "cluster_name" : "course-search-cluster",
  "cluster_uuid" : "...",
  "version" : {
    "number" : "8.11.0"
  },
  "tagline" : "You Know, for Search"
}
```
### 4. Check Cluster Health
```bash
curl http://localhost:9200/_cluster/health
```
### 5. Build and Run the Application
```bash
   mvn clean install
   mvn spring-boot:run
```
### 6. Verify Application is Running
```bash
curl http://localhost:8080/api/health
Expected response: "Course Search API is running!"
```
## 📊 Sample Data
The application includes 50+ sample course documents with the following fields:

- id: Unique identifier
- title: Course title
- description: Detailed description
- category: Course category (Math, Science, Art, etc.)
- type: Course type (ONE_TIME, COURSE, CLUB)
- gradeRange: Target grade range
- minAge & maxAge: Age requirements
- price: Course price
- nextSessionDate: Next session date (ISO-8601 format)

## Data Verification:

```bash
# Check if data is loaded
curl "http://localhost:9200/courses/_count"

# View sample documents
curl "http://localhost:9200/courses/_search?size=3&pretty"
```

## 🔍 API Endpoints
### Search Courses
```bash
GET /api/search
```

#### Query Parameters:

- q: Search keyword (searches title and description)
- minAge: Minimum age filter
- maxAge: Maximum age filter
- category: Course category filter
- type: Course type filter (ONE_TIME, COURSE, CLUB)
- minPrice: Minimum price filter
- maxPrice: Maximum price filter
- startDate: Show courses on or after this date (ISO-8601)
- sort: Sorting option (upcoming, priceAsc, priceDesc)
- page: Page number (default: 0)
- size: Page size (default: 10)

#### Example Requests:

```bash
# Basic search
curl "http://localhost:8080/api/search?q=math"

# Search with filters
curl "http://localhost:8080/api/search?q=science&minAge=8&maxAge=12&category=Science&sort=priceAsc&page=0&size=5"

# Filter by date and type
curl "http://localhost:8080/api/search?type=COURSE&startDate=2025-06-01T00:00:00Z&sort=upcoming"
```
#### Response Format:

```json
{
"total": 25,
"courses": [
{
"id": "course-001",
"title": "Advanced Mathematics",
"category": "Math",
"price": 150.00,
"nextSessionDate": "2025-06-15T10:00:00Z"
}
]
}
```
## 🎯 Bonus Features
### Autocomplete Suggestions
```http
GET /api/search/suggest?q={partialTitle}
```

#### Example Requests:
```bash
# Get suggestions for partial title
curl "http://localhost:8080/api/search/suggest?q=art"
```
#### Response Format:
```json
[
  "Creative Art Workshop","Digital Art and Design"
]
```

### Fuzzy Search
The main search endpoint supports fuzzy matching for handling typos in search queries.

#### Example Requests:
```bash
# Get suggestions for partial title
curl "http://localhost:8080/api/search?q=methemetics"
```
#### Response Format:
```json
{"total":2,"courses":[{"id":"course-001","title":"Advanced Mathematics for Young Minds","description":"Explore complex mathematical concepts through fun activities and problem-solving exercises designed for curious young mathematicians.","category":"Math","type":"COURSE","gradeRange":"4th-6th","minAge":9,"maxAge":12,"price":150.0,"nextSessionDate":"2025-07-20T10:00:00Z","titleSuggest":"Advanced Mathematics for Young Minds"},{"id":"course-049","title":"Calculus Introduction","description":"Early introduction to calculus concepts for advanced mathematics students, focusing on limits and derivatives.","category":"Math","type":"COURSE","gradeRange":"9th-12th","minAge":14,"maxAge":18,"price":250.0,"nextSessionDate":"2025-09-04T15:00:00Z","titleSuggest":"Calculus Introduction"}]}
```

## 🧪 Testing
### Manual Testing Examples

#### Test Fuzzy Search:
```bash
# Search with typo - should still find "Dinosaur Discovery"
curl "http://localhost:8080/api/search?q=dinors"

# Another typo test - should find "Robotics"
curl "http://localhost:8080/api/search?q=robtics"

# Math typo - should find "Mathematics"
curl "http://localhost:8080/api/search?q=mathmatics"

# Physics typo - should find "Physics"
curl "http://localhost:8080/api/search?q=phisics"
```
#### Test Filters:
```bash
# Find courses for 8-10 year olds
curl "http://localhost:8080/api/search?minAge=8&maxAge=10"

# Find affordable courses under $100
curl "http://localhost:8080/api/search?maxPrice=100&sort=priceAsc"

# Find upcoming Science courses
curl "http://localhost:8080/api/search?category=Science&startDate=2025-07-15T00:00:00Z&sort=upcoming"
```
#### Test Sorting:
```bash
# Cheapest courses first
curl "http://localhost:8080/api/search?sort=priceAsc&size=5"

# Most expensive courses first  
curl "http://localhost:8080/api/search?sort=priceDesc&size=5"

# Upcoming courses (default)
curl "http://localhost:8080/api/search?sort=upcoming&size=5"
```

### Automated Testing
Run the run tests script in a new terminal with root directory:
```bash
chmod +x quick-test.sh #or relavent windows command
./run-tests.sh 
```

### Integration Tests
```bash
# Run all tests
mvn test

# Run only integration tests
mvn test -Dtest=*IntegrationTest
```

## 📁 Project Structure
```bash
src/main/java/com/undoschool/coursesearch/
├── config/           # Elasticsearch configuration
├── controller/       # REST controllers
├── document/         # Elasticsearch document entities
├── repository/       # Data access layer
└── service/          # Business logic layer
```
## 🔧 Configuration
### Elasticsearch Configuration
- Host: localhost
- Port: 9200
- Index: courses

### Application Properties
```properties
spring.elasticsearch.uris=http://localhost:9200
spring.elasticsearch.connection-timeout=10s
spring.elasticsearch.socket-timeout=30s
logging.level.com.undoschool.coursesearch=INFO
```
## 📝 Sample Data Categories
The application includes courses in the following categories:

- Math: Advanced Mathematics, Algebra Basics, Geometry Fun
- Science: Physics Introduction, Chemistry Lab, Biology Exploration
- Technology: Programming Basics, Robotics Workshop, Web Development
- Art: Digital Art Creation, Painting Techniques, Creative Writing
- Sports: Soccer Skills, Basketball Training, Swimming Lessons
- Language Arts: Creative Writing, Public Speaking, Literature Analysis
- Music: Piano Lessons, Guitar Basics, Music Theory
- History: Ancient Civilizations, World History, Local History
- Life Skills: Cooking Basics, Financial Literacy, Time Management

## 📈 Performance Considerations
- Uses Elasticsearch filters (cached) rather than queries where possible
- Implements proper pagination to handle large result sets
- Bulk indexing for efficient data loading
- Appropriate field types for optimal search performance
- Custom analyzers for autocomplete functionality

## 🤝 Contributing
This is an assignment project for UndoSchool backend developer internship.

## 📄 License
This project is created for educational and assignment purposes.
