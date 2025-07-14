package com.undoschool.coursesearch.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undoschool.coursesearch.document.CourseDocument;
import com.undoschool.coursesearch.dto.CourseSearchResponse;
import com.undoschool.coursesearch.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Testcontainers
public class CourseSearchIntegrationTest {

    @Container
    static ElasticsearchContainer elasticsearch = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.11.0")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("xpack.security.enrollment.enabled", "false");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Clear existing data
        courseRepository.deleteAll();

        // Create test data
        List<CourseDocument> testCourses = Arrays.asList(
                CourseDocument.builder()
                        .id("test-001")
                        .title("Advanced Mathematics")
                        .description("Learn advanced math concepts")
                        .category("Math")
                        .type(CourseDocument.CourseType.COURSE)
                        .gradeRange("4th-6th")
                        .minAge(9)
                        .maxAge(12)
                        .price(150.0)
                        .nextSessionDate(Instant.parse("2025-08-15T10:00:00Z"))
                        .titleSuggest("Advanced Mathematics")
                        .build(),
                CourseDocument.builder()
                        .id("test-002")
                        .title("Basic Physics")
                        .description("Introduction to physics principles")
                        .category("Science")
                        .type(CourseDocument.CourseType.COURSE)
                        .gradeRange("7th-9th")
                        .minAge(12)
                        .maxAge(15)
                        .price(200.0)
                        .nextSessionDate(Instant.parse("2025-08-20T14:00:00Z"))
                        .titleSuggest("Basic Physics")
                        .build(),
                CourseDocument.builder()
                        .id("test-003")
                        .title("Art Workshop")
                        .description("Creative art activities")
                        .category("Art")
                        .type(CourseDocument.CourseType.ONE_TIME)
                        .gradeRange("1st-3rd")
                        .minAge(6)
                        .maxAge(9)
                        .price(75.0)
                        .nextSessionDate(Instant.parse("2025-08-10T11:00:00Z"))
                        .titleSuggest("Art Workshop")
                        .build()
        );

        courseRepository.saveAll(testCourses);

        // Wait for indexing
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testBasicSearch() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/search")
                        .param("q", "math")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        CourseSearchResponse response = objectMapper.readValue(responseBody, CourseSearchResponse.class);

        assertThat(response.getTotal()).isGreaterThan(0);
        assertThat(response.getCourses()).isNotEmpty();
        assertThat(response.getCourses().get(0).getTitle()).containsIgnoringCase("math");
    }

    @Test
    void testFilterByCategory() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/search")
                        .param("category", "Science")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        CourseSearchResponse response = objectMapper.readValue(responseBody, CourseSearchResponse.class);

        assertThat(response.getTotal()).isGreaterThan(0);
        assertThat(response.getCourses()).allMatch(course -> "Science".equals(course.getCategory()));
    }

    @Test
    void testFilterByAgeRange() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/search")
                        .param("minAge", "10")
                        .param("maxAge", "13")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        CourseSearchResponse response = objectMapper.readValue(responseBody, CourseSearchResponse.class);

        assertThat(response.getTotal()).isGreaterThan(0);
        assertThat(response.getCourses()).allMatch(course ->
                course.getMinAge() <= 13 && course.getMaxAge() >= 10);
    }

    @Test
    void testSortByPrice() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/search")
                        .param("sort", "priceAsc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        CourseSearchResponse response = objectMapper.readValue(responseBody, CourseSearchResponse.class);

        assertThat(response.getTotal()).isGreaterThan(0);

        // Verify ascending price order
        List<CourseDocument> courses = response.getCourses();
        for (int i = 1; i < courses.size(); i++) {
            assertThat(courses.get(i).getPrice()).isGreaterThanOrEqualTo(courses.get(i-1).getPrice());
        }
    }

    @Test
    void testPagination() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/search")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        CourseSearchResponse response = objectMapper.readValue(responseBody, CourseSearchResponse.class);

        assertThat(response.getCourses()).hasSizeLessThanOrEqualTo(2);
    }

    @Test
    void testAutocompleteSuggestions() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/search/suggest")
                        .param("q", "math")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        List<String> suggestions = objectMapper.readValue(responseBody,
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));

        assertThat(suggestions).isNotEmpty();
        assertThat(suggestions).anyMatch(suggestion -> suggestion.toLowerCase().contains("math"));
    }
}
