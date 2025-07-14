package com.undoschool.coursesearch.dto;

import com.undoschool.coursesearch.document.CourseDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSearchResponse {
    private long total;
    private List<CourseDocument> courses;
}
