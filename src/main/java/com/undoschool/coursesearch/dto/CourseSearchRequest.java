package com.undoschool.coursesearch.dto;

import com.undoschool.coursesearch.document.CourseDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSearchRequest {
    private String q; // search keyword
    private Integer minAge;
    private Integer maxAge;
    private String category;
    private CourseDocument.CourseType type;
    private Double minPrice;
    private Double maxPrice;
    private LocalDateTime startDate;
    private String sort; // upcoming, priceAsc, priceDesc
    private Integer page;
    private Integer size;

    // Default values
    public Integer getPage() {
        return page != null ? page : 0;
    }

    public Integer getSize() {
        return size != null ? size : 10;
    }

    public String getSort() {
        return sort != null ? sort : "upcoming";
    }
}
