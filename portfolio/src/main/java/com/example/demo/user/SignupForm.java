package com.example.demo.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SignupForm {

	@NotBlank(message = "メールアドレスは必須です")
	@Email(message = "メールアドレスの形式が正しくありません")
	private String email;

	@NotBlank(message = "パスワードは必須です")
	@Size(min = 6, max = 100, message = "パスワードは6文字以上で入力してください")
	private String password;

	@NotBlank(message = "ユーザー名を入力してください")
	@Size(min = 3, max = 20, message = "ユーザー名は3〜20文字で入力してください")
	@Pattern(regexp = "^[\\p{IsHan}\\p{IsHiragana}\\p{IsKatakana}a-zA-Z0-9_]+$", message = "ユーザー名は日本語（ひらがな/カタカナ/漢字）・英数字・_のみ使えます")

	@NotBlank(message = "表示名を入力してください")
	@Size(min = 1, max = 30, message = "表示名は30文字以内で入力してください")
	private String displayName;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	private String handle;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}
}