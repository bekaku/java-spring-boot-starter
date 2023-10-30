package com.bekaku.api.spring.util;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.tika.Tika;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Optional;
import java.util.UUID;

public class FileUtil {

    public static String generateFileName(String orginalName) {
        if (ObjectUtils.isEmpty(orginalName)) {
            return null;
        }
        Optional<String> extension = getExtensionByStringHandling(orginalName);
        return extension.map(s -> "" + DateUtil.getCurrentMilliTimeStamp() + ConstantData.UNDER_SCORE +
                UUID.randomUUID().toString().replace(ConstantData.MIDDLE_SCORE, "") +
                ConstantData.DOT + s).orElse(null);
    }

    public static String generateFileName(String prefixName, String orginalName) {
        if (ObjectUtils.isEmpty(orginalName)) {
            return null;
        }
        Optional<String> extension = getExtensionByStringHandling(orginalName);
        return extension.map(s -> (prefixName != null ? prefixName + ConstantData.UNDER_SCORE : "") + "" + DateUtil.getCurrentMilliTimeStamp() + ConstantData.UNDER_SCORE +
                UUID.randomUUID().toString().replace(ConstantData.MIDDLE_SCORE, "") +
                ConstantData.DOT + s).orElse(null);
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

    public static String getMimeType(File file) throws IOException {
        if (file == null) {
            return null;
        }
        Tika tika = new Tika();
        return tika.detect(file);
    }

    public static String getMimeType(MultipartFile file) {
        return file != null ? file.getContentType() : null;
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
}
