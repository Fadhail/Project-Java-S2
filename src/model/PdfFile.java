package model;

public class PdfFile {
    private int id;
    private String fileName;
    private String fileDescription;
    private String fileCategory;
    private String filePath;
    private String uploadedAt;
    private int userId; // Foreign key referencing User id

    public PdfFile(int id, String fileName, String fileDescription, String fileCategory, String filePath, String uploadedAt, int userId) {
        this.id = id;
        this.fileName = fileName;
        this.fileDescription = fileDescription;
        this.fileCategory = fileCategory;
        this.filePath = filePath;
        this.uploadedAt = uploadedAt;
        this.userId = userId;
    }

    // Getters and Setters
    // ID
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    // File Name
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    // File Description
    public String getFileDescription() {
        return fileDescription;
    }
    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
    }

    // File Category
    public String getFileCategory() {
        return fileCategory;
    }
    public void setFileCategory(String fileCategory) {
        this.fileCategory = fileCategory;
    }

    // File Path
    public String getFilePath() {return filePath;}
    public void setFilePath(String filePath) {this.filePath = filePath;}

    // Uploaded At
    public String getUploadedAt() {return uploadedAt;}
    public void setUploadedAt(String uploadedAt) {this.uploadedAt = uploadedAt;}

    // User ID
    public int getUserId() {return userId;}
    public void setUserId(int userId) {this.userId = userId;}
}
