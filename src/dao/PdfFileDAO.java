package dao;

import db.DatabaseConnection;
import model.PdfFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PdfFileDAO {
    public void saveFileInfo(PdfFile pdfFile) throws SQLException {
        String sql = "INSERT INTO pdf_files (fileName, fileDescription, fileCategory, filePath, uploadedAt, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pdfFile.getFileName());
            statement.setString(2, pdfFile.getFileDescription());
            statement.setString(3, pdfFile.getFileCategory());
            statement.setString(4, pdfFile.getFilePath());
            statement.setString(5, pdfFile.getUploadedAt());
            statement.setInt(6, pdfFile.getUserId());
            statement.executeUpdate();
        }
    }

    public PdfFile getFileById(int fileId) throws SQLException {
        PdfFile pdfFile = null;
        String sql = "SELECT * FROM pdf_files WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, fileId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String fileName = resultSet.getString("fileName");
                    String fileDescription = resultSet.getString("fileDescription");
                    String fileCategory = resultSet.getString("fileCategory");
                    String filePath = resultSet.getString("filePath");
                    String uploadedAt = resultSet.getString("uploadedAt");
                    int userId = resultSet.getInt("user_id");
                    pdfFile = new PdfFile(fileId, fileName, fileDescription, fileCategory, filePath, uploadedAt, userId);
                }
            }
        }
        return pdfFile;
    }

    public List<PdfFile> getAllFiles(int userId) throws SQLException {
        List<PdfFile> files = new ArrayList<>();
        String sql = "SELECT * FROM pdf_files WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int fileId = resultSet.getInt("id");
                    String fileName = resultSet.getString("fileName");
                    String fileDescription = resultSet.getString("fileDescription");
                    String fileCategory = resultSet.getString("fileCategory");
                    String filePath = resultSet.getString("filePath");
                    String uploadedAt = resultSet.getString("uploadedAt");
                    PdfFile pdfFile = new PdfFile(fileId, fileName, fileDescription, fileCategory, filePath, uploadedAt, userId);
                    files.add(pdfFile);
                }
            }
        }
        return files;
    }


    public void deleteFile(int fileId) throws SQLException, IOException {
        PdfFile pdfFile = getFileById(fileId);
        if (pdfFile == null) {
            throw new SQLException("No file found with id: " + fileId);
        }

        String sql = "DELETE FROM pdf_files WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, fileId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Delete failed, no rows affected.");
            }
            // Delete the file from the file system
            Files.delete(Paths.get(pdfFile.getFilePath()));
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
}
