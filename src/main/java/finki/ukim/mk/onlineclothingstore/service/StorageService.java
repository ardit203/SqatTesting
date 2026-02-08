package finki.ukim.mk.onlineclothingstore.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface StorageService {
    final Path root = Paths.get("uploads");
    String saveProductImage(Long productId, MultipartFile file) throws IOException;

    void deleteByPublicUrl(String publicUrl) throws IOException;

    String replaceProductImage(Long productId, MultipartFile newFile, String oldImageUrl) throws IOException;
}
