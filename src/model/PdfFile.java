package model;

public class PdfFile {
    private int id;
    private String fileName;
    private String fileDescription;
    private String fileCategory;
    private String uploadedAt;
    private int userId; // Foreign key referencing User id

    public PdfFile(int id, String fileName, String fileDescription, String fileCategory, String uploadedAt, int userId) {
        this.id = id;
        this.fileName = fileName;
        this.fileDescription = fileDescription;
        this.fileCategory = fileCategory;
        this.uploadedAt = uploadedAt;
        this.userId = userId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDescription() {
        return fileDescription;
    }

    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
    }

    public String getFileCategory() {
        return fileCategory;
    }

    public void setFileCategory(String fileCategory) {
        this.fileCategory = fileCategory;
    }

    public String getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(String uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
