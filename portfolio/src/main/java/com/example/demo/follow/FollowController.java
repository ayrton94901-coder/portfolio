package com.example.demo.follow;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

@Controller
public class FollowController {

	private final FollowRepository followRepository;
	private final UserRepository userRepository;

	public FollowController(FollowRepository followRepository, UserRepository userRepository) {
		this.followRepository = followRepository;
		this.userRepository = userRepository;
	}

	@PostMapping("/users/{id}/follow")
	public String follow(@AuthenticationPrincipal User me, @PathVariable("id") Long targetId) {
		if (me == null)
			return "redirect:/login";
		if (me.getId().equals(targetId))
			return "redirect:/users/" + targetId;

		if (!followRepository.existsByFollowerIdAndFolloweeId(me.getId(), targetId)) {
			User target = userRepository.findById(targetId).orElse(null);
			if (target == null)
				return "redirect:/posts";

			Follow f = new Follow();
			f.setFollower(me);
			f.setFollowee(target);
			followRepository.save(f);
		}
		return "redirect:/users/" + targetId;
	}

	@Transactional
	@PostMapping("/users/{id}/unfollow")
	public String unfollow(@AuthenticationPrincipal User me, @PathVariable("id") Long targetId) {
		if (me == null)
			return "redirect:/login";
		followRepository.deleteByFollowerIdAndFolloweeId(me.getId(), targetId);
		return "redirect:/users/" + targetId;
	}

}