package com.example.demo.post;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.comment.CommentForm;
import com.example.demo.comment.CommentRepository;
import com.example.demo.like.PostLike;
import com.example.demo.like.PostLikeRepository;
import com.example.demo.storage.StorageService;
import com.example.demo.tag.Tag;
import com.example.demo.tag.TagRepository;
import com.example.demo.user.User;

import jakarta.validation.Valid;

@Controller
public class PostController {

	private final PostRepository postRepository;
	private final StorageService storageService;
	private final CommentRepository commentRepository;
	private final PostLikeRepository likeRepository;
	private final TagRepository tagRepository;

	public PostController(PostRepository postRepository, StorageService storageService,
			CommentRepository commentRepository, PostLikeRepository likeRepository, TagRepository tagRepository) {
		this.postRepository = postRepository;
		this.storageService = storageService;
		this.commentRepository = commentRepository;
		this.likeRepository = likeRepository;
		this.tagRepository = tagRepository;
	}

	@GetMapping("/")
	public String root() {
		return "redirect:/posts";
	}

	@GetMapping("/posts")
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "tag", required = false) String tag, @AuthenticationPrincipal User user, Model model) {

		List<Post> posts;
		if (tag != null && !tag.isBlank()) {
			posts = postRepository.findByTag(tag.trim());
		} else if (keyword != null && !keyword.isBlank()) {
			posts = postRepository.search(keyword.trim());
		} else {
			posts = postRepository.findAllWithUserOrderByCreatedAtDesc();
		}

		model.addAttribute("posts", posts);
		System.out.println("posts.userNullCount=" + posts.stream().filter(p -> p.getUser() == null).count());
		System.out.println("posts.size=" + posts.size());
		System.out.println("posts.nullCount=" + posts.stream().filter(x -> x == null).count());
		System.out.println("tag=" + tag + ", keyword=" + keyword);
		model.addAttribute("keyword", keyword);
		model.addAttribute("tag", tag);

		List<Long> postIds = posts.stream().map(Post::getId).toList();

		// Like件数
		Map<Long, Long> likeCountMap = new HashMap<>();
		if (!postIds.isEmpty()) {
			for (PostLike pl : likeRepository.findByPostIdIn(postIds)) {
				Long pid = pl.getPost().getId();
				likeCountMap.put(pid, likeCountMap.getOrDefault(pid, 0L) + 1L);
			}
		}

		// 自分がLike済みか
		Set<Long> likedPostIds = new HashSet<>();
		if (user != null && !postIds.isEmpty()) {
			for (PostLike pl : likeRepository.findByPostIdInAndUserId(postIds, user.getId())) {
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

		model.addAttribute("likeCountMap", likeCountMap);
		model.addAttribute("likedPostIds", likedPostIds);
		model.addAttribute("commentCountMap", commentCountMap);

		return "posts/index";
	}

	@GetMapping("/posts/new")
	public String newPost(Model model) {
		model.addAttribute("postForm", new PostForm());
		return "posts/new";
	}

	@PostMapping("/posts")
	public String create(@Valid PostForm postForm, BindingResult result, @AuthenticationPrincipal User user) {

		if (result.hasErrors())
			return "posts/new";

		if (postForm.getImage() == null || postForm.getImage().isEmpty()) {
			result.rejectValue("image", "image.empty", "画像を選択してください");
			return "posts/new";
		}

		if (user == null)
			return "redirect:/login";

		String filename = storageService.store(postForm.getImage());

		Post post = new Post();
		post.setUser(user);
		post.setImageFilename(filename);

		// 映画情報
		post.setMovieTitle(postForm.getMovieTitle());
		post.setDirector(postForm.getDirector());
		post.setReleaseYear(postForm.getReleaseYear());
		post.setRating(postForm.getRating());

		// 感想
		post.setCaption(postForm.getCaption());

		// タグ（複数）
		Set<Tag> tagEntities = new LinkedHashSet<>();
		String rawTags = postForm.getTags();
		if (rawTags != null && !rawTags.isBlank()) {
			for (String part : rawTags.split("[,、\\s]+")) {
				String trimmed = part.trim();
				if (trimmed.isEmpty())
					continue;

				String normalized = trimmed.startsWith("#") ? trimmed.substring(1) : trimmed;
				normalized = normalized.trim();
				if (normalized.isEmpty())
					continue;

				final String tagName = normalized; // ← effectively final

				Tag tag = tagRepository.findByName(tagName).orElseGet(() -> {
					Tag t = new Tag();
					t.setName(tagName);
					return tagRepository.save(t);
				});

				tagEntities.add(tag);
			}
		}
		post.setTags(tagEntities);

		postRepository.save(post);
		return "redirect:/posts";
	}

	@GetMapping("/posts/{id}")
	public String show(@PathVariable Long id, @AuthenticationPrincipal User user, Model model) {

		Post post = postRepository.findByIdWithUserAndTags(id).orElse(null);
		if (post == null)
			return "redirect:/posts";

		model.addAttribute("post", post);
		model.addAttribute("comments", commentRepository.findByPostIdWithUserOrderByCreatedAtAsc(id));
		model.addAttribute("commentForm", new CommentForm());

		long likeCount = likeRepository.countByPostId(id);
		boolean likedByMe = (user != null) && likeRepository.existsByPostIdAndUserId(id, user.getId());

		model.addAttribute("likeCount", likeCount);
		model.addAttribute("likedByMe", likedByMe);

		return "posts/show";
	}
}