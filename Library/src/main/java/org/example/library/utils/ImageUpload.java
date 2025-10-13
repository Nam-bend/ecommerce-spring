package org.example.library.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
@Component
public class ImageUpload {
    private final String UPLOAD_FOLDER =
            "C:\\img";

    public boolean uploadFile(MultipartFile file, String filename) {
        try {
            File directory = new File(UPLOAD_FOLDER);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Lưu file với tên mới (uniqueFilename)
            Files.copy(file.getInputStream(),
                    Paths.get(UPLOAD_FOLDER + File.separator + filename),
                    StandardCopyOption.REPLACE_EXISTING);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getFilePath(String filename) {
        return UPLOAD_FOLDER + File.separator + filename;
    }
}
