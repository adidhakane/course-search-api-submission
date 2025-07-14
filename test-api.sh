#!/bin/bash

# Course Search API Test Script
# This script tests all endpoints and features of the Course Search API

BASE_URL="http://localhost:8080"
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== Course Search API Test Suite ===${NC}"
echo ""

# Function to make HTTP request and check response
test_endpoint() {
    local name="$1"
    local url="$2"
    local expected_status="$3"

    echo -e "${YELLOW}Testing: $name${NC}"
    echo "URL: $url"

    response=$(curl -s -w "HTTPSTATUS:%{http_code}" "$url")
    http_code=$(echo $response | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
    body=$(echo $response | sed -e 's/HTTPSTATUS\:.*//g')

    if [ "$http_code" -eq "$expected_status" ]; then
        echo -e "${GREEN}✓ PASS${NC} (HTTP $http_code)"
        if [ "$expected_status" -eq 200 ]; then
            echo "Response preview:"
            echo "$body" | jq '.' 2>/dev/null | head -20 || echo "$body" | head -5
        fi
    else
        echo -e "${RED}✗ FAIL${NC} (Expected HTTP $expected_status, got HTTP $http_code)"
        echo "Response: $body"
    fi
    echo ""
}

# Function to test with JSON output validation
test_search_endpoint() {
    local name="$1"
    local url="$2"

    echo -e "${YELLOW}Testing: $name${NC}"
    echo "URL: $url"

    response=$(curl -s "$url")

    # Check if response is valid JSON
    if echo "$response" | jq . >/dev/null 2>&1; then
        total=$(echo "$response" | jq '.total')
        courses_count=$(echo "$response" | jq '.courses | length')

        echo -e "${GREEN}✓ PASS${NC} - Valid JSON response"
        echo "Total results: $total"
        echo "Courses returned: $courses_count"

        # Show first course if available
        if [ "$courses_count" -gt 0 ]; then
            echo "First course preview:"
            echo "$response" | jq '.courses[0] | {id, title, category, price}' 2>/dev/null
        fi
    else
        echo -e "${RED}✗ FAIL${NC} - Invalid JSON response"
        echo "Response: $response"
    fi
    echo ""
}

echo -e "${YELLOW}1. Health Check${NC}"
test_endpoint "Health Check" "$BASE_URL/api/health" 200

echo -e "${YELLOW}2. Basic Search Tests${NC}"
test_search_endpoint "Search all courses" "$BASE_URL/api/search"
test_search_endpoint "Search for 'math'" "$BASE_URL/api/search?q=math"
test_search_endpoint "Search for 'science'" "$BASE_URL/api/search?q=science"
test_search_endpoint "Search for 'art'" "$BASE_URL/api/search?q=art"

echo -e "${YELLOW}3. Filter Tests${NC}"
test_search_endpoint "Filter by category (Science)" "$BASE_URL/api/search?category=Science"
test_search_endpoint "Filter by category (Technology)" "$BASE_URL/api/search?category=Technology"
test_search_endpoint "Filter by type (COURSE)" "$BASE_URL/api/search?type=COURSE"
test_search_endpoint "Filter by type (ONE_TIME)" "$BASE_URL/api/search?type=ONE_TIME"
test_search_endpoint "Filter by type (CLUB)" "$BASE_URL/api/search?type=CLUB"

echo -e "${YELLOW}4. Age Range Filter Tests${NC}"
test_search_endpoint "Age range 8-12" "$BASE_URL/api/search?minAge=8&maxAge=12"
test_search_endpoint "Min age 10" "$BASE_URL/api/search?minAge=10"
test_search_endpoint "Max age 12" "$BASE_URL/api/search?maxAge=12"

echo -e "${YELLOW}5. Price Filter Tests${NC}"
test_search_endpoint "Price under 100" "$BASE_URL/api/search?maxPrice=100"
test_search_endpoint "Price over 200" "$BASE_URL/api/search?minPrice=200"
test_search_endpoint "Price range 100-200" "$BASE_URL/api/search?minPrice=100&maxPrice=200"

echo -e "${YELLOW}6. Date Filter Tests${NC}"
test_search_endpoint "Courses after 2025-08-01" "$BASE_URL/api/search?startDate=2025-08-01T00:00:00"
test_search_endpoint "Courses after 2025-09-01" "$BASE_URL/api/search?startDate=2025-09-01T00:00:00"

echo -e "${YELLOW}7. Sorting Tests${NC}"
test_search_endpoint "Sort by upcoming (default)" "$BASE_URL/api/search?sort=upcoming&size=5"
test_search_endpoint "Sort by price ascending" "$BASE_URL/api/search?sort=priceAsc&size=5"
test_search_endpoint "Sort by price descending" "$BASE_URL/api/search?sort=priceDesc&size=5"

echo -e "${YELLOW}8. Pagination Tests${NC}"
test_search_endpoint "Page 0, size 3" "$BASE_URL/api/search?page=0&size=3"
test_search_endpoint "Page 1, size 3" "$BASE_URL/api/search?page=1&size=3"
test_search_endpoint "Page 0, size 20" "$BASE_URL/api/search?page=0&size=20"

echo -e "${YELLOW}9. Complex Query Tests${NC}"
test_search_endpoint "Complex query 1" "$BASE_URL/api/search?q=math&category=Math&minAge=8&maxAge=15&sort=priceAsc"
test_search_endpoint "Complex query 2" "$BASE_URL/api/search?q=science&type=COURSE&minPrice=100&maxPrice=250&sort=upcoming"
test_search_endpoint "Complex query 3" "$BASE_URL/api/search?category=Technology&type=COURSE&minAge=10&maxAge=16&sort=priceDesc&page=0&size=5"

echo -e "${YELLOW}10. Fuzzy Search Tests (Assignment B)${NC}"
test_search_endpoint "Fuzzy: 'mathmatics' (should find mathematics)" "$BASE_URL/api/search?q=mathmatics"
test_search_endpoint "Fuzzy: 'phisics' (should find physics)" "$BASE_URL/api/search?q=phisics"
test_search_endpoint "Fuzzy: 'robtics' (should find robotics)" "$BASE_URL/api/search?q=robtics"
test_search_endpoint "Fuzzy: 'dinors' (should find dinosaur)" "$BASE_URL/api/search?q=dinors"

echo -e "${YELLOW}11. Autocomplete Tests (Assignment B)${NC}"

test_autocomplete() {
    local query="$1"
    local url="$BASE_URL/api/search/suggest?q=$query"

    echo -e "${YELLOW}Testing autocomplete for: '$query'${NC}"
    echo "URL: $url"

    response=$(curl -s "$url")

    if echo "$response" | jq . >/dev/null 2>&1; then
        suggestions_count=$(echo "$response" | jq '. | length')
        echo -e "${GREEN}✓ PASS${NC} - $suggestions_count suggestions found"
        echo "Suggestions:"
        echo "$response" | jq -r '.[]' | head -10
    else
        echo -e "${RED}✗ FAIL${NC} - Invalid response"
        echo "Response: $response"
    fi
    echo ""
}

test_autocomplete "math"
test_autocomplete "sci"
test_autocomplete "art"
test_autocomplete "prog"
test_autocomplete "phy"

echo -e "${YELLOW}12. Edge Cases and Error Handling${NC}"
test_endpoint "Invalid parameter type" "$BASE_URL/api/search?minAge=invalid" 400
test_endpoint "Invalid date format" "$BASE_URL/api/search?startDate=invalid-date" 400
test_endpoint "Empty suggestion query" "$BASE_URL/api/search/suggest?q=" 200

echo -e "${YELLOW}13. Performance Tests${NC}"

performance_test() {
    local name="$1"
    local url="$2"

    echo -e "${YELLOW}Performance test: $name${NC}"
    echo "URL: $url"

    start_time=$(date +%s%N)
    response=$(curl -s "$url")
    end_time=$(date +%s%N)

    duration=$(( (end_time - start_time) / 1000000 ))

    if echo "$response" | jq . >/dev/null 2>&1; then
        total=$(echo "$response" | jq '.total')
        echo -e "${GREEN}✓ PASS${NC} - Response time: ${duration}ms, Results: $total"
    else
        echo -e "${RED}✗ FAIL${NC} - Invalid response, Time: ${duration}ms"
    fi
    echo ""
}

performance_test "Large result set" "$BASE_URL/api/search?size=50"
performance_test "Complex query performance" "$BASE_URL/api/search?q=course&minAge=5&maxAge=18&minPrice=50&maxPrice=300&sort=priceAsc&size=25"

echo -e "${YELLOW}=== Test Summary ===${NC}"
echo "All tests completed!"
echo ""
echo -e "${YELLOW}Manual verification suggestions:${NC}"
echo "1. Check that fuzzy search works with typos"
echo "2. Verify autocomplete provides relevant suggestions"
echo "3. Confirm filters work in combination"
echo "4. Test pagination with different page sizes"
echo "5. Verify sorting produces expected order"
echo ""
echo -e "${GREEN}Test script completed successfully!${NC}"
