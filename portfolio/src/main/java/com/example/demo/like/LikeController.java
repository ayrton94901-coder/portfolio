package com.example.demo.like;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.post.Post;
import com.example.demo.post.PostRepository;
import com.example.demo.user.User;

@Controller
public class LikeController {

	private final PostLikeRepository likeRepo;
	private final PostRepository postRepo;

	public LikeController(PostLikeRepository likeRepo, PostRepository postRepo) {
		this.likeRepo = likeRepo;
		this.postRepo = postRepo;
	}

	@PostMapping("/posts/{postId}/like")
	public String toggle(@PathVariable Long postId, @AuthenticationPrincipal User user) {

		if (user == null)
			return "redirect:/login";

		Post post = postRepo.findById(postId).orElse(null);
		if (post == null)
			return "redirect:/posts";

		// toggle() の中だけ抜粋（該当部分を置換）
		var existing = likeRepo.findByPostIdAndUserId(postId, user.getId());
		if (existing.isPresent()) {
			likeRepo.delete(existing.get());
		} else {
			PostLike pl = new PostLike();
			pl.setPost(post);
			pl.setUser(user);
			likeRepo.save(pl);
		}

		return "redirect:/posts";
	}
}