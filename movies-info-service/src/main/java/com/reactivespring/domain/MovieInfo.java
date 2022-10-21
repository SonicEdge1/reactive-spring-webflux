package com.reactivespring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class MovieInfo {

    @Id
    private String movieInfoId;
    @NotBlank(message = "Movie Title Must Be Present")
    private String title;
    @NotNull
    @Positive(message = "Year must be a positive value")
    private Integer year;
    @NotNull(message = "At least one cast member must be present")
    private List<@NotBlank(message = "At least one cast member must be present") String> cast;
    private LocalDate release_date;

}
