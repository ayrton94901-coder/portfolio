package com.example.demo.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {

	private final Path uploadDir;

	public StorageService(@Value("${app.upload-dir}") String uploadDir) {
		this.uploadDir = Path.of(uploadDir).toAbsolutePath().normalize();
	}

	public String store(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("画像を選択してください");
		}

		String original = StringUtils.cleanPath(file.getOriginalFilename());
		String ext = "";
		int dot = original.lastIndexOf('.');
		if (dot >= 0)
			ext = original.substring(dot);

		String filename = UUID.randomUUID() + ext;

		try {
			Files.createDirectories(uploadDir);
			file.transferTo(uploadDir.resolve(filename));
			return filename;
		} catch (IOException e) {
			throw new RuntimeException("画像の保存に失敗しました", e);
		}
	}

	public Path getUploadDir() {
		return uploadDir;
	}
}