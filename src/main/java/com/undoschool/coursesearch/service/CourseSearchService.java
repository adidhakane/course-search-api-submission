package com.undoschool.coursesearch.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.NumberRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.DateRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.undoschool.coursesearch.document.CourseDocument;
import com.undoschool.coursesearch.dto.CourseSearchRequest;
import com.undoschool.coursesearch.dto.CourseSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public CourseSearchResponse searchCourses(CourseSearchRequest request) {
        log.info("Searching courses with request: {}", request);

        // Build the query
        NativeQuery searchQuery = buildSearchQuery(request);

        // Execute search
        SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(searchQuery, CourseDocument.class);

        // Extract results
        List<CourseDocument> courses = searchHits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return CourseSearchResponse.builder()
                .total(searchHits.getTotalHits())
                .courses(courses)
                .build();
    }

    private NativeQuery buildSearchQuery(CourseSearchRequest request) {
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        // Full-text search on title and description
        if (request.getQ() != null && !request.getQ().trim().isEmpty()) {
            Query multiMatchQuery = Query.of(q -> q
                    .multiMatch(m -> m
                            .query(request.getQ())
                            .fields("title^2", "description") // Boost title field
                            .fuzziness("AUTO") // Enable fuzzy matching for Assignment B
                    )
            );
            boolQueryBuilder.must(multiMatchQuery);
        }

        // Age range filters using NumberRangeQuery
        if (request.getMinAge() != null) {
            NumberRangeQuery minAgeRangeQuery = NumberRangeQuery.of(r -> r
                    .field("maxAge")
                    .gte(request.getMinAge().doubleValue())
            );
            Query minAgeQuery = Query.of(q -> q.range(r -> r.number(minAgeRangeQuery)));
            boolQueryBuilder.filter(minAgeQuery);
        }

        if (request.getMaxAge() != null) {
            NumberRangeQuery maxAgeRangeQuery = NumberRangeQuery.of(r -> r
                    .field("minAge")
                    .lte(request.getMaxAge().doubleValue())
            );
            Query maxAgeQuery = Query.of(q -> q.range(r -> r.number(maxAgeRangeQuery)));
            boolQueryBuilder.filter(maxAgeQuery);
        }

        // Category filter
        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            Query categoryQuery = Query.of(q -> q
                    .term(t -> t
                            .field("category")
                            .value(request.getCategory())
                    )
            );
            boolQueryBuilder.filter(categoryQuery);
        }

        // Type filter
        if (request.getType() != null) {
            Query typeQuery = Query.of(q -> q
                    .term(t -> t
                            .field("type")
                            .value(request.getType().name())
                    )
            );
            boolQueryBuilder.filter(typeQuery);
        }

        // Price range filters using NumberRangeQuery
        if (request.getMinPrice() != null || request.getMaxPrice() != null) {
            NumberRangeQuery.Builder priceRangeBuilder = new NumberRangeQuery.Builder()
                    .field("price");
            
            if (request.getMinPrice() != null) {
                priceRangeBuilder.gte(request.getMinPrice());
            }
            if (request.getMaxPrice() != null) {
                priceRangeBuilder.lte(request.getMaxPrice());
            }

            NumberRangeQuery priceRangeQuery = priceRangeBuilder.build();
            Query priceQuery = Query.of(q -> q.range(r -> r.number(priceRangeQuery)));
            boolQueryBuilder.filter(priceQuery);
        }

        // Date filter using DateRangeQuery
        if (request.getStartDate() != null) {
            String dateString = request.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z";
            DateRangeQuery dateRangeQuery = DateRangeQuery.of(r -> r
                    .field("nextSessionDate")
                    .gte(dateString)
            );
            Query dateQuery = Query.of(q -> q.range(r -> r.date(dateRangeQuery)));
            boolQueryBuilder.filter(dateQuery);
        }

        // Build the final query
        Query finalQuery = Query.of(q -> q.bool(boolQueryBuilder.build()));

        // Create pageable
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        // Build NativeQuery with sorting and pagination
        return NativeQuery.builder()
                .withQuery(finalQuery)
                .withPageable(pageable)
                .withSort(getSortOptions(request.getSort()))
                .build();
    }

    private Sort getSortOptions(String sortType) {
        switch (sortType.toLowerCase()) {
            case "priceasc":
                return Sort.by(Sort.Direction.ASC, "price");
            case "pricedesc":
                return Sort.by(Sort.Direction.DESC, "price");
            case "upcoming":
            default:
                return Sort.by(Sort.Direction.ASC, "nextSessionDate");
        }
    }

    // Assignment B: Autocomplete suggestions
    public List<String> getSuggestions(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        Query suggestionQuery = Query.of(q -> q
                .match(m -> m
                        .field("titleSuggest")
                        .query(query)
                )
        );

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(suggestionQuery)
                .withMaxResults(10)
                .build();

        SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(searchQuery, CourseDocument.class);

        return searchHits.getSearchHits()
                .stream()
                .map(hit -> hit.getContent().getTitle())
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }
}
