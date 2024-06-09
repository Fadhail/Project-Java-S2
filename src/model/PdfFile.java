package model;

public class PdfFile {
    private int id;
    private String fileName;
    private String fileDescription;
    private String fileCategory;
    private String filePath;
    private String uploadedAt;

    // Constructor with all fields
    public PdfFile(int id, String fileName, String fileDescription, String fileCategory, String filePath, String uploadedAt) {
        this.id = id;
        this.fileName = fileName;
        this.fileDescription = fileDescription;
        this.fileCategory = fileCategory;
        this.filePath = filePath;
        this.uploadedAt = uploadedAt;
    }

    // Getters for fields
    public int getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileDescription() {
        return fileDescription;
    }

    public String getFileCategory() {
        return fileCategory;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getUploadedAt() {
        return uploadedAt;
    }
}
