package model;

public class PdfFile {
    private int id;
    private String fileName;
    private String filePath;
    private String uploadedAt;

    public PdfFile(int id, String fileName, String filePath, String uploadedAt) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.uploadedAt = uploadedAt;
    }

    public int getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getUploadedAt() {
        return uploadedAt;
    }
}
