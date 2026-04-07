package com.example.demo.user;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// ここに email を入れる（ログインIDとして使う）
	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	// 表示用 @handle
	@Column(nullable = false, unique = true)
	private String handle;

	// 表示用（日本語OK）
	@Column(nullable = false)
	private String displayName;

	// プロフィール画像ファイル名（uploads配下に保存）
	@Column(name = "avatar_filename")
	private String avatarFilename;

	// User.java のフィールド追加例
	@Column(columnDefinition = "TEXT")
	private String bio;

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	// --- UserDetails ---
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}

	// Spring Securityが参照するログインID（email）
	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	// --- getter/setter ---
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// 画面で email として扱いたいなら、username をemailの別名として公開する
	public String getEmail() {
		return username;
	}

	public void setEmail(String email) {
		this.username = email;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getAvatarFilename() {
		return avatarFilename;
	}

	public void setAvatarFilename(String avatarFilename) {
		this.avatarFilename = avatarFilename;
	}
}