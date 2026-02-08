package finki.ukim.mk.onlineclothingstore.service.impl;

import finki.ukim.mk.onlineclothingstore.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class StorageServiceImpl implements StorageService {


    public String saveProductImage(Long productId, MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new IllegalArgumentException("Empty file");

        String ct = file.getContentType();
        if (ct == null || !(ct.equals("image/png") || ct.equals("image/jpeg") || ct.equals("image/webp")))
            throw new IllegalArgumentException("Only png/jpg/webp allowed");

        Path dir = root.resolve("products");
        Files.createDirectories(dir);

        String ext = getExt(file.getOriginalFilename()); // ".png" etc
        String filename = productId.toString()  + ext;
        Path target = dir.resolve(filename).normalize();

        if (!target.startsWith(dir)) throw new SecurityException("Bad path");

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        // public URL you will store in DB
        return "/uploads/products/" + filename;
    }

    private String getExt(String name) {
        if (name == null) return ".png";
        int dot = name.lastIndexOf('.');
        return dot >= 0 ? name.substring(dot).toLowerCase() : ".png";
    }

    public void deleteByPublicUrl(String publicUrl) throws IOException {
        if (publicUrl == null || publicUrl.isBlank()) return;

        // Only allow deleting from our uploads folder
        String prefix = "/uploads/";
        if (!publicUrl.startsWith(prefix)) return;

        // Convert "/uploads/variants/12/redM.png" -> "variants/12/redM.png"
        String relative = publicUrl.substring(prefix.length());

        Path filePath = root.resolve(relative).normalize(); // root = Paths.get("uploads")

        // Prevent path traversal (must stay inside uploads/)
        if (!filePath.startsWith(root)) throw new SecurityException("Bad path");

        Files.deleteIfExists(filePath);
    }

    public String replaceProductImage(Long productId, MultipartFile newFile, String oldImageUrl) throws IOException {

        // If no new file uploaded, keep old
        if (newFile == null || newFile.isEmpty()) return oldImageUrl;

        // delete old first (if any)
        deleteByPublicUrl(oldImageUrl);

        // save new and return its url
        return saveProductImage(productId, newFile);
    }
}
