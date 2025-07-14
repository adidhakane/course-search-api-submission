# Course Search API - Demo Queries

This document contains example queries that demonstrate all features of the Course Search API.

## Basic Setup Verification

```bash
# Check if Elasticsearch is running
curl http://localhost:9200

# Check if application is running
curl http://localhost:8080/api/health

# Verify data is loaded
curl "http://localhost:9200/courses/_count"
```
## Assignment A - Required Features
### 1. Full-Text Search
```bash
# Search for mathematics courses
curl "http://localhost:8080/api/search?q=mathematics"

# Search for science-related content
curl "http://localhost:8080/api/search?q=science"

# Search in both title and description
curl "http://localhost:8080/api/search?q=programming"
```
### 2. Category Filtering
```bash
# Find all Math courses
curl "http://localhost:8080/api/search?category=Math"

# Find all Technology courses
curl "http://localhost:8080/api/search?category=Technology"

# Find all Art courses
curl "http://localhost:8080/api/search?category=Art"
```
### 3. Course Type Filtering
```
# Find regular courses
curl "http://localhost:8080/api/search?type=COURSE"

# Find one-time workshops
curl "http://localhost:8080/api/search?type=ONE_TIME"

# Find clubs
curl "http://localhost:8080/api/search?type=CLUB"
```
### 4. Age Range Filtering
```bash
# Courses suitable for 8-12 year olds
curl "http://localhost:8080/api/search?minAge=8&maxAge=12"

# Courses for teenagers (13+)
curl "http://localhost:8080/api/search?minAge=13"

# Courses for younger kids (under 10)
curl "http://localhost:8080/api/search?maxAge=10"
```
### 5. Price Filtering
```bash
# Affordable courses under $100
curl "http://localhost:8080/api/search?maxPrice=100"

# Premium courses over $200
curl "http://localhost:8080/api/search?minPrice=200"

# Mid-range courses ($100-$200)
curl "http://localhost:8080/api/search?minPrice=100&maxPrice=200"
```
### 6. Date Filtering
```bash
# Courses starting in August 2025
curl "http://localhost:8080/api/search?startDate=2025-08-01T00:00:00Z"

# Courses starting in September 2025
curl "http://localhost:8080/api/search?startDate=2025-09-01T00:00:00Z"
```
### 7. Sorting Options
```bash
# Sort by upcoming sessions (default)
curl "http://localhost:8080/api/search?sort=upcoming&size=5"

# Sort by price (low to high)
curl "http://localhost:8080/api/search?sort=priceAsc&size=5"

# Sort by price (high to low)
curl "http://localhost:8080/api/search?sort=priceDesc&size=5"
```
### 8. Pagination
```bash
# First page (3 results)
curl "http://localhost:8080/api/search?page=0&size=3"

# Second page (3 results)
curl "http://localhost:8080/api/search?page=1&size=3"

# Large page size
curl "http://localhost:8080/api/search?page=0&size=20"
```
### 9. Complex Combined Queries
```bash
# Find affordable math courses for middle schoolers
curl "http://localhost:8080/api/search?q=math&category=Math&minAge=10&maxAge=14&maxPrice=200&sort=priceAsc"

# Find upcoming technology courses for teens
curl "http://localhost:8080/api/search?category=Technology&type=COURSE&minAge=13&startDate=2025-08-01T00:00:00Z&sort=upcoming"

# Find art workshops under $100
curl "http://localhost:8080/api/search?q=art&category=Art&type=ONE_TIME&maxPrice=100&sort=priceAsc"
```

## Assignment B - Bonus Features
1. Autocomplete Suggestions
```
# Math-related suggestions
curl "http://localhost:8080/api/search/suggest?q=math"

# Science-related suggestions
curl "http://localhost:8080/api/search/suggest?q=sci"

# Art-related suggestions
curl "http://localhost:8080/api/search/suggest?q=art"

# Programming-related suggestions
curl "http://localhost:8080/api/search/suggest?q=prog"

# Partial word suggestions
curl "http://localhost:8080/api/search/suggest?q=phy"
```
2. Fuzzy Search (Typo Tolerance)
```
# Typo: "mathmatics" should find "mathematics"
curl "http://localhost:8080/api/search?q=mathmatics"

# Typo: "phisics" should find "physics"
curl "http://localhost:8080/api/search?q=phisics"

# Typo: "robtics" should find "robotics"
curl "http://localhost:8080/api/search?q=robtics"

# Typo: "dinors" should find "dinosaur"
curl "http://localhost:8080/api/search?q=dinors"

# Typo: "photgraphy" should find "photography"
curl "http://localhost:8080/api/search?q=photgraphy"
```
## Real-World Usage Scenarios
Scenario 1: Parent looking for math help for 10-year-old
```
curl "http://localhost:8080/api/search?q=math&minAge=9&maxAge=11&sort=priceAsc"
```
Scenario 2: Finding summer camps (clubs) for teenagers
```
curl "http://localhost:8080/api/search?type=CLUB&minAge=13&maxAge=16&sort=upcoming"
```
Scenario 3: Budget-conscious parent looking for workshops under $80
```
curl "http://localhost:8080/api/search?type=ONE_TIME&maxPrice=80&sort=priceAsc"
```
Scenario 4: Looking for STEM courses starting next month
```
curl "http://localhost:8080/api/search?q=science OR technology OR math&startDate=2025-08-01T00:00:00Z&sort=upcoming"
```
## Performance Testing Queries
```
# Large result set
curl "http://localhost:8080/api/search?size=50"

# Complex query with multiple filters
curl "http://localhost:8080/api/search?q=course&minAge=5&maxAge=18&minPrice=50&maxPrice=300&sort=priceAsc&size=25"

# Search across all text fields
curl "http://localhost:8080/api/search?q=learn"
```
## Data Verification Queries
```
# Count courses by category
curl "http://localhost:9200/courses/_search?size=0" -H "Content-Type: application/json" -d '{
"aggs": {
"categories": {
"terms": {
"field": "category"
}
}
}
}'

# Count courses by type
curl "http://localhost:9200/courses/_search?size=0" -H "Content-Type: application/json" -d '{
"aggs": {
"types": {
"terms": {
"field": "type"
}
}
}
}'

# Price statistics
curl "http://localhost:9200/courses/_search?size=0" -H "Content-Type: application/json" -d '{
"aggs": {
"price_stats": {
"stats": {
"field": "price"
}
}
}
}'
```

### 15.2 Create Postman Collection Export

Create `Course-Search-API.postman_collection.json`:

```json
{
  "info": {
    "name": "Course Search API",
    "description": "Complete test collection for UndoSchool Course Search API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Health Check",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{baseUrl}}/api/health",
          "host": ["{{baseUrl}}"],
          "path": ["api", "health"]
        }
      }
    },
    {
      "name": "Basic Search",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{baseUrl}}/api/search",
          "host": ["{{baseUrl}}"],
          "path": ["api", "search"]
        }
      }
    },
    {
      "name": "Search with Keyword",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{baseUrl}}/api/search?q=mathematics",
          "host": ["{{baseUrl}}"],
          "path": ["api", "search"],
          "query": [
            {
              "key": "q",
              "value": "mathematics"
            }
          ]
        }
      }
    },
    {
      "name": "Filter by Category",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{baseUrl}}/api/search?category=Science",
          "host": ["{{baseUrl}}"],
          "path": ["api", "search"],
          "query": [
            {
              "key": "category",
              "value": "Science"
            }
          ]
        }
      }
    },
    {
      "name": "Complex Query",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{baseUrl}}/api/search?q=programming&category=Technology&minAge=10&maxAge=16&minPrice=100&maxPrice=300&sort=priceAsc&page=0&size=5",
          "host": ["{{baseUrl}}"],
          "path": ["api", "search"],
          "query": [
            {
              "key": "q",
              "value": "programming"
            },
            {
              "key": "category",
              "value": "Technology"
            },
            {
              "key": "minAge",
              "value": "10"
            },
            {
              "key": "maxAge",
              "value": "16"
            },
            {
              "key": "minPrice",
              "value": "100"
            },
            {
              "key": "maxPrice",
              "value": "300"
            },
            {
              "key": "sort",
              "value": "priceAsc"
            },
            {
              "key": "page",
              "value": "0"
            },
            {
              "key": "size",
              "value": "5"
            }
          ]
        }
      }
    },
    {
      "name": "Autocomplete Suggestions",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{baseUrl}}/api/search/suggest?q=math",
          "host": ["{{baseUrl}}"],
          "path": ["api", "search", "suggest"],
          "query": [
            {
              "key": "q",
              "value": "math"
            }
          ]
        }
      }
    },
    {
      "name": "Fuzzy Search",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{baseUrl}}/api/search?q=mathmatics",
          "host": ["{{baseUrl}}"],
          "path": ["api", "search"],
          "query": [
            {
              "key": "q",
              "value": "mathmatics"
            }
          ]
        }
      }
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080"
    }
  ]
}
```