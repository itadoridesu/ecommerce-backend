package com.dmdm.ecommerce_backend.helpers;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class ImageStorageUtility {

    private final String uploadDir = "src/main/resources/static/images/";

    public String saveImage(MultipartFile file) throws IOException {
        // Get original filename (e.g. "banana.png")
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IOException("Invalid file: filename is missing");
        }

        if (!file.getContentType().startsWith("image/")) {
            throw new IOException("Only image files are allowed.");
        }

        // Extract extension (e.g. ".png")
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // Generate a unique filename like "a7d8e2d3-...png"
        String newFilename = UUID.randomUUID() + extension;

        // Build the full file path where the image will be saved
        Path filePath = Paths.get(uploadDir + newFilename);

        // Make sure the folder (static/images) exists — create if it doesn’t
        Files.createDirectories(filePath.getParent());

        // Write this uploaded file’s contents to the given location (filePath) on disk
        file.transferTo(filePath);

        // Return the path to be stored in the database (used later to access image)
        return "/images/" + newFilename;
    }

    public void deleteImageFile(String filename) {
        Path filePath = Paths.get("src/main/resources/static/images/" + filename);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + filename, e);
        }
    }


}
