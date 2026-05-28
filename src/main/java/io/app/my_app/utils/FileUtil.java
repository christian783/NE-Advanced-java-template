package io.app.my_app.utils;

import io.app.my_app.model.enums.FileSizeType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
    /**
     * GetFileName from a path
     *
     * @param path File Path
     * @return String fileName
     */
    public static String getFileNameFromFilePath(String path) {
        String fileName = new File(path).getName();
        if (fileName.indexOf(".") > 0)
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        return fileName;
    }

    /**
     * Gert fileSize from a path
     *
     * @param fileLocalPath File Local Path
     * @return long File Size
     * @throws IOException I/O exception
     */
    public static long getFileSizeFromPath(String fileLocalPath) throws IOException {
        Path path = Paths.get(fileLocalPath);
        return Files.size(path);
    }

    /**
     * Get FileSizeType from fileSize
     *
     * @param size File Size in TB, GB, MB, KB
     * @return String of FileSizedTypeEnum
     */
    public static String getFileSizeTypeFromFileSize(long size) {
        if (size >= (1024L * 1024 * 1024 * 1024))
            return FileSizeType.TB.toString();
        else if (size >= 1024 * 1024 * 1024)
            return FileSizeType.GB.toString();
        else if (size >= 1024 * 1024)
            return FileSizeType.MB.toString();
        else if (size >= 1024)
            return FileSizeType.KB.toString();
        else
            return FileSizeType.B.toString();
    }


    /**
     * Get formatted fileSize from file Size
     *
     * @param size File size
     * @param type FileSize type
     * @return int formattedFileSize
     */
    public static int getFormattedFileSizeFromFileSize(double size, FileSizeType type) {
        if (type == FileSizeType.TB)
            return (int) (size / (1024L * 1024 * 1024 * 1024));
        else if (type == FileSizeType.GB)
            return (int) (size / (1024 * 1024 * 1024));
        else if (type == FileSizeType.MB)
            return (int) (size / (1024 * 1024));
        else if (type == FileSizeType.KB)
            return (int) (size / (1024));
        else
            return (int) size;
    }

    /**
     * Get File Type From File Path
     *
     * @param path String File Path
     * @return String fileType
     * @throws IOException I/O exception
     */
    public static String getFileTypeFromFilePath(String path) throws IOException {
        return Files.probeContentType(Paths.get(path));
    }

    /**
     * Get File Extension From File
     *
     * @param file File
     * @return String fileExtension
     */
    private static String getFileExtensionFromFile(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return "." + fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }


    /**
     * Generate Random UUID
     *
     * @param fileName FileName
     * @return String UUID
     */
    public static String generateUUID(String fileName) {
        int period = fileName.lastIndexOf(".");
        String prefix = fileName.substring(0, period);
        String suffix = fileName.substring(period);
        return (prefix + "-" + RandomUtil.randomRefNumber().toLowerCase() + suffix).replaceAll(" ", "");
    }
}