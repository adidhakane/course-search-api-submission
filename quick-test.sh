#!/bin/bash

# Quick Test Script for Course Search API
BASE_URL="http://localhost:8080"

echo "=== Quick API Test ==="

# Health check
echo "1. Health Check:"
curl -s "$BASE_URL/api/health"
echo -e "\n"

# Basic search
echo "2. Basic Search (first 3 results):"
curl -s "$BASE_URL/api/search?size=3" | jq '{total: .total, count: (.courses | length), courses: [.courses[] | {id, title, category, price}]}'
echo -e "\n"

# Search with keyword
echo "3. Search for 'math':"
curl -s "$BASE_URL/api/search?q=math&size=3" | jq '{total: .total, courses: [.courses[] | {title, category, price}]}'
echo -e "\n"

# Filter test
echo "4. Science courses under $200:"
curl -s "$BASE_URL/api/search?category=Science&maxPrice=200&size=3" | jq '{total: .total, courses: [.courses[] | {title, price}]}'
echo -e "\n"

# Autocomplete test
echo "5. Autocomplete for 'phy':"
curl -s "$BASE_URL/api/search/suggest?q=phy" | jq '.'
echo -e "\n"

# Fuzzy search test
echo "6. Fuzzy search for 'mathmatics' (typo):"
curl -s "$BASE_URL/api/search?q=mathmatics&size=2" | jq '{total: .total, courses: [.courses[] | {title}]}'
echo -e "\n"

echo "Quick test completed!"
