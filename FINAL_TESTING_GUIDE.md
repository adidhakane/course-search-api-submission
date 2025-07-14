# Final Testing Guide

## Quick Verification (5 minutes)

1. Setup Verification
```
# Check Elasticsearch
curl http://localhost:9200
# Expected: Cluster info with version 8.11.0

# Check Application
curl http://localhost:8080/api/health
# Expected: "Course Search API is running!"

# Check Data Loading
curl http://localhost:9200/courses/_count
# Expected: {"count":52,"_shards":{"total":1,"successful":1,"skipped":0,"failed":0}}
```
2. Core Features Test
```
# Run quick test script
./quick-test.sh
```
Comprehensive Testing (15 minutes)
1. Assignment A - Required Features
Basic Search:

```
curl "http://localhost:8080/api/search?q=mathematics&size=3" | jq '.'
```
✅ Should return courses with "mathematics" in title/description

Category Filter:

```
curl "http://localhost:8080/api/search?category=Science&size=5" | jq '.courses[].category'
```
✅ All results should be "Science"

Age Filter:

```
curl "http://localhost:8080/api/search?minAge=10&maxAge=12&size=5" | jq '.courses[] | {title, minAge, maxAge}'
```
✅ All courses should have overlapping age ranges with 10-12

Price Filter:

```
curl "http://localhost:8080/api/search?minPrice=100&maxPrice=200&sort=priceAsc&size=5" | jq '.courses[] | {title, price}'
```
✅ All prices should be between 100-200, sorted ascending

Date Filter:

```
curl "http://localhost:8080/api/search?startDate=2025-08-15T00:00:00Z&sort=upcoming&size=5" | jq '.courses[] | {title, nextSessionDate}'
```
✅ All dates should be >= 2025-08-15

Sorting:

```
# Price ascending
curl "http://localhost:8080/api/search?sort=priceAsc&size=5" | jq '.courses[].price'

# Price descending  
curl "http://localhost:8080/api/search?sort=priceDesc&size=5" | jq '.courses[].price'

# Upcoming (default)
curl "http://localhost:8080/api/search?sort=upcoming&size=5" | jq '.courses[].nextSessionDate'
```
✅ Results should be properly sorted

Pagination:

```
# Page 0
curl "http://localhost:8080/api/search?page=0&size=3" | jq '.courses | length'

# Page 1
curl "http://localhost:8080/api/search?page=1&size=3" | jq '.courses | length'
```
✅ Should return 3 results each, different courses

2. Assignment B - Bonus Features
Fuzzy Search:

```
# Test various typos
curl "http://localhost:8080/api/search?q=mathmatics" | jq '.total'  # Should find "mathematics"
curl "http://localhost:8080/api/search?q=phisics" | jq '.total'     # Should find "physics"  
curl "http://localhost:8080/api/search?q=robtics" | jq '.total'     # Should find "robotics"
curl "http://localhost:8080/api/search?q=dinors" | jq '.total'      # Should find "dinosaur"
```
✅ All should return > 0 results despite typos

Autocomplete:

```
curl "http://localhost:8080/api/search/suggest?q=math" | jq '. | length'
curl "http://localhost:8080/api/search/suggest?q=sci" | jq '. | length'  
curl "http://localhost:8080/api/search/suggest?q=art" | jq '. | length'
```
✅ Should return relevant suggestions (> 0 results)

3. Complex Queries
Multi-filter Query:

```
curl "http://localhost:8080/api/search?q=science&category=Science&type=COURSE&minAge=10&maxAge=15&minPrice=100&maxPrice=250&sort=priceAsc&page=0&size=10" | jq '{total: .total, courses: [.courses[] | {title, category, type, price, minAge, maxAge}]}'
```
✅ Should apply all filters correctly

Full Test Suite (30 minutes)
```
# Run comprehensive test script
./test-api.sh
```
This script tests:

- ✅ Health check
- ✅ Basic search variations
- ✅ All filter types individually
- ✅ All sorting options
- ✅ Pagination scenarios
- ✅ Complex combined queries
- ✅ Fuzzy search with multiple typos
- ✅ Autocomplete with various inputs
- ✅ Error handling
- ✅ Performance scenarios

## Expected Results Summary
Assignment A Verification
- Search Endpoint: ✅ Returns structured JSON with total and courses array
- Filters Work: ✅ Category, type, age, price, date filters all functional
- Sorting Works: ✅ Three sorting options produce correct order
- Pagination Works: ✅ Page and size parameters control results
- Data Quality: ✅ 52 courses with diverse, realistic data

Assignment B Verification
- Fuzzy Search: ✅ Handles common typos and misspellings
- Autocomplete: ✅ Provides relevant title suggestions
- Performance: ✅ Fast response times even with complex queries

Error Handling
-Invalid Parameters: ✅ Returns 400 with helpful error messages
- Server Errors: ✅ Returns 500 with proper error structure
- Empty Results: ✅ Returns valid response with empty courses array

- Troubleshooting
If any test fails:

- Check Elasticsearch: curl http://localhost:9200/_cluster/health
- Check Data: curl http://localhost:9200/courses/_count
- Check Logs: tail -f logs/application.log
- Restart Services: docker-compose restart && mvn spring-boot:run
- Performance Benchmarks

Expected response times (on standard development machine):

- Simple queries: < 100ms
- Complex queries: < 500ms
- Autocomplete: < 50ms
- Large result sets: < 1000ms
- Final Checklist
- Elasticsearch running on port 9200
- Application running on port 8080
- 52 courses loaded in index
- All search filters working
- Sorting options working
- Pagination working
- Fuzzy search working
- Autocomplete working
- Error handling working
- Performance acceptable
Status: Ready for Submission ✅