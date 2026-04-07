package com.example.demo.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProfileEditForm {

	@NotBlank
	@Size(max = 255)
	private String displayName;

	@NotBlank
	@Size(max = 255)
	private String handle;

	// ProfileEditForm.java に追加
	@Size(max = 500)
	private String bio;

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}
}