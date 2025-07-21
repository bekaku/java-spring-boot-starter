package com.bekaku.api.spring.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.exif.ExifIFD0Directory;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class FileUtil {


    public static String generateFileName(String orginalName) {
        if (ObjectUtils.isEmpty(orginalName)) {
            return null;
        }
        Optional<String> extension = getExtensionByStringHandling(orginalName);
        return extension.map(s -> DateUtil.getCurrentMilliTimeStamp() + ConstantData.UNDER_SCORE +
                UuidUtils.generateUUID().toString().replace(ConstantData.MIDDLE_SCORE, "") +
                ConstantData.DOT + s).orElse(null);
    }

    public static String generateFileName(String prefixName, String orginalName) {
        if (ObjectUtils.isEmpty(orginalName)) {
            return null;
        }
        Optional<String> extension = getExtensionByStringHandling(orginalName);
        return extension.map(s -> (prefixName != null ? prefixName + ConstantData.UNDER_SCORE : "") + DateUtil.getCurrentMilliTimeStamp() + ConstantData.UNDER_SCORE +
                UuidUtils.generateUUID().toString().replace(ConstantData.MIDDLE_SCORE, "") +
                ConstantData.DOT + s).orElse(null);
    }

    public static String generateFileNameByMimeType(String prefixName, String mimeTypeString) {
        String extension = FileUtil.getFileExtensionByMimeType(mimeTypeString);
        if (extension == null) {
            return null;
        }
        return (prefixName != null ? prefixName + ConstantData.UNDER_SCORE : "") + DateUtil.getCurrentMilliTimeStamp() + ConstantData.UNDER_SCORE +
                UuidUtils.generateUUID().toString().replace(ConstantData.MIDDLE_SCORE, "") + extension;
    }

    public static String generateJpgFileNameByMemeType(String prefixName, String mimeTypeString) {
        String fileName = generateFileNameByMimeType(prefixName, mimeTypeString);
        if (fileName != null) {
            String[] splitName = fileName.split("\\.");
            if (splitName.length == 2) {
                return fileName.replace("." + splitName[1], ".jpg");
            }
        }

        return fileName;
    }

    public static String generateJpgFileName(String prefixName, String orginalName) {
        if (ObjectUtils.isEmpty(orginalName)) {
            return null;
        }
        String fileName = generateFileName(prefixName, orginalName);
        if (fileName != null) {
            String[] splitName = fileName.split("\\.");
            if (splitName.length == 2) {
                return fileName.replace("." + splitName[1], ".jpg");
            }
        }

        return fileName;
    }

    public static Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public static String getMultipartFileName(MultipartFile file) {
        if (!file.isEmpty()) {
            return file.getOriginalFilename();
        }
        return null;
    }

    public static void createFolderIfNoExist(String directoryName) {
        File directory = new File(directoryName);
        if (!directory.exists()) {
//            directory.mkdir();
            directory.mkdirs();// If you require it to make the entire directory path including parents, use directory.mkdirs(); here instead.
        }
    }

    public static void getAndCheckUploadPath(String directoryName) {
        if (!ObjectUtils.isEmpty(directoryName)) {
            createFolderIfNoExist(directoryName);
        }
    }

    public static String generateCdnPath(String cdnPath, String directory, String filename) {
        return cdnPath + ConstantData.BACK_SLACK + directory + (filename != null ? filename : "");
    }

    public static String getImagesYearMonthDirectory() {
        return ConstantData.IMAGES + ConstantData.BACK_SLACK + DateUtil.getLocalDateByForMat(DateUtil.getLocalDateNow(), DateUtil.CDN_YEAR_MONTH_FORMAT) + ConstantData.BACK_SLACK;
    }

    public static String getFilesYearMonthDirectory() {
        return ConstantData.FILES + ConstantData.BACK_SLACK + DateUtil.getLocalDateByForMat(DateUtil.getLocalDateNow(), DateUtil.CDN_YEAR_MONTH_FORMAT) + ConstantData.BACK_SLACK;
    }

    public static String getDirectoryForUpload(String cdnUploadPath, String directory) {
        getAndCheckUploadPath(cdnUploadPath + directory);
        return cdnUploadPath + directory;
    }

    //thumbnailator resize
    public static BufferedImage thumbnailatorResizeImage(BufferedImage originalImage, int targetWidth, int targetHeight, double quality) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(originalImage)
                .size(targetWidth, targetHeight)
                .outputFormat("JPEG")
                .outputQuality(quality)//0.90
                .toOutputStream(outputStream);
        byte[] data = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        return ImageIO.read(inputStream);
    }

    public static String generateThumbnailName(String fileName, String thumPostFixName) {
       /* String[] splitName = fileName.split("\\.");
        if(splitName.length==2){
            return fileName.replace("."+splitName[1], thumPostFixName + "."+splitName[1]);
        }*/
        return fileName.replace(".jpg", thumPostFixName + ".jpg");
    }

    public static BufferedImage correctOrientation(BufferedImage image, File file) throws IOException {
        int orientation = 1;
        try {
            com.drew.metadata.Metadata metadata = ImageMetadataReader.readMetadata(file);
            ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            if (directory != null && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
                orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (orientation) {
            case 1:
                // No rotation needed
                break;
            case 3:
                image = Thumbnails.of(image).size(image.getWidth(), image.getHeight()).rotate(180).asBufferedImage();
                break;
            case 6:
                image = Thumbnails.of(image).size(image.getWidth(), image.getHeight()).rotate(90).asBufferedImage();
                break;
            case 8:
                image = Thumbnails.of(image).size(image.getWidth(), image.getHeight()).rotate(-90).asBufferedImage();
                break;
            default:
                break;
        }

        return image;
    }

    public static boolean fileExists(String sFileName) {
        if (sFileName.startsWith("classpath:")) {
            String path = sFileName.substring("classpath:".length());
            ClassLoader cl = ClassUtils.getDefaultClassLoader();
            URL url = cl != null ? cl.getResource(path) : ClassLoader.getSystemResource(path);
            return (url != null);
        } else {
            Path path = Paths.get(sFileName);
            return Files.exists(path);
        }
    }

    public static byte[] convertBase64ToByteArray(String base64String) {
        byte[] decodedImg = null;
        if (AppUtil.isEmpty(base64String)) {
            return null;
        }
        if (base64String.contains(ConstantData.PART_SEPARATOR)) {
            String encodedImg = base64String.split(ConstantData.PART_SEPARATOR)[1];
            decodedImg = Base64.getDecoder().decode(encodedImg.getBytes(StandardCharsets.UTF_8));
        }
        return decodedImg;
    }

    public static String getMimeType(File file) throws IOException {
        if (file == null) {
            return null;
        }
        Tika tika = new Tika();
        return tika.detect(file);
    }

    public static String getMimeType(MultipartFile file) {
        return file != null ? getFileType(file) : null;
//        return file != null ? file.getContentType() : null;
    }

    public static String getMimeType(String fileName) {
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream"; // Default to binary data
        }
    }

    public static String getFileType(MultipartFile file) {
        try {
            // Instantiate Tika detector
            Tika tika = new Tika();
            // Detecting the file type
            // Extracting file extension from detected MIME type
//            String fileExtension = getFileExtension(detectedType);
            return tika.detect(file.getInputStream());
        } catch (IOException e) {
            // Handle IOException
            e.printStackTrace();
            return null;
        }
    }

    public static String detectBase64MimeType(String base64String) {
        byte[] base64Bytes = FileUtil.convertBase64ToByteArray(base64String);
        Tika tika = new Tika();
        return tika.detect(base64Bytes);
    }

    public static String getFileExtensionByMimeType(String mimeTypeString) {
        if (AppUtil.isEmpty(mimeTypeString)) {
            return null;
        }
        MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
        MimeType type;
        try {
            type = allTypes.forName(mimeTypeString);
        } catch (MimeTypeException e) {
            throw new RuntimeException(e);
        }
        if (type != null) {
            return type.getExtension();
        }
        return null;
    }

    public static String detectBase64MimeTypeV1(String base64String) {
        String[] strings = base64String.split(",");

        return switch (strings[0]) {//check image's extension
            case "data:image/jpeg;base64" -> "image/jpeg";
            case "data:image/png;base64" -> "image/png";
            default ->//should write cases for more images types
                    null;
        };
    }

    public static int getFileSizeFromBase64V1(String in) {
        int count = 0;
        int pad = 0;
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if (c == '=') pad++;
            if (!Character.isWhitespace(c)) count++;
        }
        return (count * 3 / 4) - pad;
    }

    public static int getFileSizeFromBase64(String in) {
        byte[] fileContent = FileUtil.convertBase64ToByteArray(in);
        if (fileContent != null) {
            return fileContent.length;
        }
        return 0;
    }

    public static boolean isImage(String mimeType) {
        return mimeType.contains(ConstantData.IMAGE);
    }

    public static Long getFileSize(MultipartFile file) {
        return file != null ? file.getSize() : 0;
    }

    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    public static boolean hasExcelFormat(MultipartFile file) {

        return ConstantData.CONTENT_TYPE_EXCEL.equals(file.getContentType());
    }

    public static boolean folderExist(String folderPath) {
        File folder = new File(folderPath);
        return folder.exists() && folder.isDirectory();
    }

    public static boolean folderCreate(String folderPath) {
        File folder = new File(folderPath);
        // Use mkdirs() to create parent directories if they don't exist
        return folder.mkdirs();
    }
    public static String trimFileName(String originalFileName, int limit) {
        if (originalFileName == null || limit < 1) return originalFileName;

        int lastDotIndex = originalFileName.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == 0 || lastDotIndex == originalFileName.length() - 1) {
            // No valid extension
            return originalFileName.length() > limit ? originalFileName.substring(0, limit) : originalFileName;
        }

        String namePart = originalFileName.substring(0, lastDotIndex);
        String extension = originalFileName.substring(lastDotIndex); // includes the dot
        String trimmedName = namePart.length() > limit ? namePart.substring(0, limit) : namePart;
        return trimmedName + extension;
    }

}
