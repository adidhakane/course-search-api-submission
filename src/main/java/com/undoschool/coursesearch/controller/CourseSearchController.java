package com.undoschool.coursesearch.controller;

import com.undoschool.coursesearch.document.CourseDocument;
import com.undoschool.coursesearch.dto.CourseSearchRequest;
import com.undoschool.coursesearch.dto.CourseSearchResponse;
import com.undoschool.coursesearch.service.CourseSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // For potential frontend integration
public class CourseSearchController {

    private final CourseSearchService courseSearchService;

    @GetMapping("/search")
    public ResponseEntity<CourseSearchResponse> searchCourses(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) CourseDocument.CourseType type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(defaultValue = "upcoming") String sort,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        log.info("Received search request - q: {}, minAge: {}, maxAge: {}, category: {}, type: {}, minPrice: {}, maxPrice: {}, startDate: {}, sort: {}, page: {}, size: {}",
                q, minAge, maxAge, category, type, minPrice, maxPrice, startDate, sort, page, size);

        CourseSearchRequest request = CourseSearchRequest.builder()
                .q(q)
                .minAge(minAge)
                .maxAge(maxAge)
                .category(category)
                .type(type)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .startDate(startDate)
                .sort(sort)
                .page(page)
                .size(size)
                .build();

        CourseSearchResponse response = courseSearchService.searchCourses(request);

        log.info("Search completed - found {} total results, returning {} courses",
                response.getTotal(), response.getCourses().size());

        return ResponseEntity.ok(response);
    }

    // Assignment B: Autocomplete endpoint
    
    @GetMapping("/search/suggest")
    public ResponseEntity<List<String>> getSuggestions(@RequestParam String q) {
        log.info("Received suggestion request for query: {}", q);

        List<String> suggestions = courseSearchService.getSuggestions(q);

        log.info("Found {} suggestions for query: {}", suggestions.size(), q);

        return ResponseEntity.ok(suggestions);
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Course Search API is running!");
    }
}
