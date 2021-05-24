package io.beka.util;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
        return fileName.replace(".jpg", thumPostFixName + ".jpg");
    }

}