package com.example.demo.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.user.SignupForm;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

import jakarta.validation.Valid;


@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@Valid SignupForm signupForm, BindingResult result, Model model) {
        if (userRepository.existsByUsername(signupForm.getEmail())) {
            result.rejectValue("email", "email.duplicate", "そのメールアドレスは既に登録されています");
        }
        if (userRepository.existsByHandle(signupForm.getHandle())) {
            result.rejectValue("handle", "handle.duplicate", "そのユーザー名は既に使われています");
        }

        if (result.hasErrors()) {
            return "auth/signup";
        }

        User user = new User();
        user.setEmail(signupForm.getEmail());
        user.setHandle(signupForm.getHandle()); // ★追加
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setDisplayName(signupForm.getDisplayName());

        userRepository.save(user);
        return "redirect:/login";
    }
}