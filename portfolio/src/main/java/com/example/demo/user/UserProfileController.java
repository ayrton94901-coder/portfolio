package com.example.demo.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.comment.CommentRepository;
import com.example.demo.follow.FollowRepository;
import com.example.demo.like.PostLike;
import com.example.demo.like.PostLikeRepository;
import com.example.demo.post.Post;
import com.example.demo.post.PostRepository;

@Controller
public class UserProfileController {

	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final FollowRepository followRepository;
	private final CommentRepository commentRepository;
	private final PostLikeRepository likeRepository;

	public UserProfileController(UserRepository userRepository, PostRepository postRepository,
			FollowRepository followRepository, CommentRepository commentRepository, PostLikeRepository likeRepository) {
		this.userRepository = userRepository;
		this.postRepository = postRepository;
		this.followRepository = followRepository;
		this.commentRepository = commentRepository;
		this.likeRepository = likeRepository;
	}

	@GetMapping("/users/{id}")
	public String showUser(@PathVariable Long id, @AuthenticationPrincipal User loginUser, Model model) {

		User u = userRepository.findById(id).orElseThrow();
		List<Post> posts = postRepository.findByUserIdWithUserAndTagsOrderByCreatedAtDesc(id);

		boolean isMe = (loginUser != null && loginUser.getId().equals(u.getId()));
		// おすすめ：自分の /users/{id} は /profile に寄せるなら下を有効化
		// if (isMe) return "redirect:/profile";

		boolean isFollowing = false;
		if (!isMe && loginUser != null) {
			isFollowing = followRepository.existsByFollowerIdAndFolloweeId(loginUser.getId(), u.getId());
		}

		long followerCount = followRepository.countByFolloweeId(u.getId());
		long followingCount = followRepository.countByFollowerId(u.getId());

		// ===== ここから：postCard用の集計（プロフィールでも数字を出す） =====
		List<Long> postIds = posts.stream().map(Post::getId).toList();

		// Like件数
		Map<Long, Long> likeCountMap = new HashMap<>();
		if (!postIds.isEmpty()) {
			for (PostLike pl : likeRepository.findByPostIdIn(postIds)) {
				Long pid = pl.getPost().getId();
				likeCountMap.put(pid, likeCountMap.getOrDefault(pid, 0L) + 1L);
			}
		}

		// 自分がLike済みか（ログインしてる時だけ）
		Set<Long> likedPostIds = new HashSet<>();
		if (loginUser != null && !postIds.isEmpty()) {
			for (PostLike pl : likeRepository.findByPostIdInAndUserId(postIds, loginUser.getId())) {
				likedPostIds.add(pl.getPost().getId());
			}
		}

		// コメント件数
		Map<Long, Long> commentCountMap = new HashMap<>();
		if (!postIds.isEmpty()) {
			for (Object[] row : commentRepository.countByPostIds(postIds)) {
				Long pid = (Long) row[0];
				Long cnt = (Long) row[1];
				commentCountMap.put(pid, cnt);
			}
		}
		// ===== ここまで =====

		model.addAttribute("user", u);
		model.addAttribute("myPosts", posts);
		model.addAttribute("isMe", isMe);

		model.addAttribute("isFollowing", isFollowing);
		model.addAttribute("followerCount", followerCount);
		model.addAttribute("followingCount", followingCount);

		// postCard.html が参照する変数
		model.addAttribute("likeCountMap", likeCountMap);
		model.addAttribute("likedPostIds", likedPostIds);
		model.addAttribute("commentCountMap", commentCountMap);

		return "profile/show";
	}
}