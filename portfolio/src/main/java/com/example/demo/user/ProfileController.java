package com.example.demo.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.comment.CommentRepository;
import com.example.demo.follow.FollowRepository;
import com.example.demo.like.PostLike;
import com.example.demo.like.PostLikeRepository;
import com.example.demo.post.Post;
import com.example.demo.post.PostRepository;
import com.example.demo.storage.StorageService;

import jakarta.validation.Valid;

@Controller
public class ProfileController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final CommentRepository commentRepository;
    private final PostLikeRepository likeRepository;
    private final FollowRepository followRepository;

    public ProfileController(PostRepository postRepository,
            UserRepository userRepository,
            StorageService storageService,
            CommentRepository commentRepository,
            PostLikeRepository likeRepository,
            FollowRepository followRepository) {
this.postRepository = postRepository;
this.userRepository = userRepository;
this.storageService = storageService;
this.commentRepository = commentRepository;
this.likeRepository = likeRepository;
this.followRepository = followRepository;
}

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal User user, Model model) {
        if (user == null) return "redirect:/login";

        User me = userRepository.findById(user.getId()).orElse(null);
        if (me == null) return "redirect:/login";

        List<Post> myPosts = postRepository.findByUserIdWithUserAndTagsOrderByCreatedAtDesc(me.getId());

        List<Long> postIds = myPosts.stream().map(Post::getId).toList();

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
        if (!postIds.isEmpty()) {
            for (PostLike pl : likeRepository.findByPostIdInAndUserId(postIds, me.getId())) {
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

        model.addAttribute("user", me);
        model.addAttribute("myPosts", myPosts);
        model.addAttribute("isMe", true);
        
        long followerCount = followRepository.countByFolloweeId(me.getId());
        long followingCount = followRepository.countByFollowerId(me.getId());

        model.addAttribute("followerCount", followerCount);
        model.addAttribute("followingCount", followingCount);
        model.addAttribute("isFollowing", false); // 自分ページなので固定でOK

        // フラグメント（postCard.html）が参照する変数
        model.addAttribute("likeCountMap", likeCountMap);
        model.addAttribute("likedPostIds", likedPostIds);
        model.addAttribute("commentCountMap", commentCountMap);

        return "profile/show";
    }

    @GetMapping("/profile/edit")
    public String edit(@AuthenticationPrincipal User user, Model model) {
        if (user == null) return "redirect:/login";

        User me = userRepository.findById(user.getId()).orElse(null);
        if (me == null) return "redirect:/login";

        ProfileEditForm form = new ProfileEditForm();
        form.setDisplayName(me.getDisplayName());
        form.setHandle(me.getHandle());
        form.setBio(me.getBio());

        model.addAttribute("profileEditForm", form);
        model.addAttribute("user", me);
        return "profile/edit";
    }

    @PostMapping("/profile/edit")
    public String update(@AuthenticationPrincipal User user,
                         @Valid ProfileEditForm profileEditForm,
                         BindingResult result,
                         Model model) {
        if (user == null) return "redirect:/login";

        if (result.hasErrors()) {
            model.addAttribute("profileEditForm", profileEditForm);
            
            return "profile/edit";
        }

        // DBのユーザーを更新
        User me = userRepository.findById(user.getId()).orElse(null);
        if (me == null) return "redirect:/login";

        me.setDisplayName(profileEditForm.getDisplayName());
        me.setHandle(profileEditForm.getHandle());
        me.setBio(profileEditForm.getBio());
        userRepository.save(me);

        return "redirect:/profile";
    }
    
    @PostMapping("/profile/avatar")
    public String updateAvatar(@AuthenticationPrincipal User user,
                               @RequestParam("avatar") MultipartFile avatarFile) {
        if (user == null) return "redirect:/login";
        if (avatarFile == null || avatarFile.isEmpty()) return "redirect:/profile";

        // 画像を保存（postsの画像保存と同じ仕組みを流用）
        String filename = storageService.store(avatarFile);

        // DB更新
        User me = userRepository.findById(user.getId()).orElse(null);
        if (me == null) return "redirect:/login";

        me.setAvatarFilename(filename);
        userRepository.save(me);

        return "redirect:/profile/edit";
    }
}