package com.undoschool.coursesearch.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.undoschool.coursesearch.document.CourseDocument;
import com.undoschool.coursesearch.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataLoaderService implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        loadSampleData();
    }

    private void loadSampleData() {
        try {
            log.info("Loading sample course data...");

            // Check if data already exists
            long count = courseRepository.count();
            if (count > 0) {
                log.info("Sample data already loaded. Found {} courses in index.", count);
                return;
            }

            // Configure ObjectMapper for LocalDateTime
            objectMapper.registerModule(new JavaTimeModule());

            // Load sample data from JSON file
            ClassPathResource resource = new ClassPathResource("sample-courses.json");
            InputStream inputStream = resource.getInputStream();

            List<CourseDocument> courses = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<CourseDocument>>() {}
            );

            // Set titleSuggest field for autocomplete
            courses.forEach(course -> course.setTitleSuggest(course.getTitle()));

            // Bulk save to Elasticsearch
            courseRepository.saveAll(courses);

            log.info("Successfully loaded {} courses into Elasticsearch", courses.size());

        } catch (IOException e) {
            log.error("Error loading sample data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load sample data", e);
        }
    }
}
