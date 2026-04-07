package com.example.demo.comment;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.post.Post;
import com.example.demo.post.PostRepository;
import com.example.demo.user.User;

import jakarta.validation.Valid;

@Controller
public class CommentController {

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;

	public CommentController(CommentRepository commentRepository, PostRepository postRepository) {
		this.commentRepository = commentRepository;
		this.postRepository = postRepository;
	}

	@PostMapping("/posts/{postId}/comments")
	public String create(@PathVariable Long postId, @Valid CommentForm commentForm, BindingResult result,
			@AuthenticationPrincipal User user) {

		if (user == null)
			return "redirect:/login";

		Post post = postRepository.findById(postId).orElse(null);
		if (post == null)
			return "redirect:/posts";

		if (result.hasErrors()) {
			// いったん簡単に：エラーでも詳細へ戻す（表示は次のステップで）
			return "redirect:/posts/" + postId;
		}

		Comment c = new Comment();
		c.setContent(commentForm.getContent());
		c.setPost(post);
		c.setUser(user);

		commentRepository.save(c);

		return "redirect:/posts/" + postId;
	}
}