package com.example.demo.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentForm {

    @NotBlank(message = "コメントを入力してください")
    @Size(max = 200, message = "コメントは200文字以内で入力してください")
    private String content;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}