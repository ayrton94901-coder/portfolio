package com.example.demo.post;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PostForm {

    @NotBlank(message = "映画タイトルは必須です")
    @Size(max = 100, message = "映画タイトルは100文字以内で入力してください")
    private String movieTitle;

    @Size(max = 100, message = "監督名は100文字以内で入力してください")
    private String director;

    @NotNull(message = "公開年は必須です")
    @Min(value = 1888, message = "公開年が不正です")
    @Max(value = 2100, message = "公開年が不正です")
    private Integer releaseYear;

    @NotNull(message = "評価は必須です")
    @Min(value = 1, message = "評価は1〜5で入力してください")
    @Max(value = 5, message = "評価は1〜5で入力してください")
    private Integer rating;

    @NotBlank(message = "感想は必須です")
    @Size(max = 500, message = "感想は500文字以内で入力してください")
    private String caption; // 感想本文

    @Size(max = 200, message = "タグは200文字以内で入力してください")
    private String tags;    // 例: "SF,名作,おすすめ"

    private MultipartFile image;

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public MultipartFile getImage() { return image; }
    public void setImage(MultipartFile image) { this.image = image; }
}