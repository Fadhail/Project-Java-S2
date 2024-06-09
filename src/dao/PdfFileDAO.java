package dao;

import db.DatabaseConnection;
import model.PdfFile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PdfFileDAO {
    public void saveFileInfo(PdfFile pdfFile) throws SQLException {
        String sql = "INSERT INTO pdf_files (fileName, fileDescription, fileCategory, uploadedAt, user_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pdfFile.getFileName());
            statement.setString(2, pdfFile.getFileDescription());
            statement.setString(3, pdfFile.getFileCategory());
            statement.setString(4, pdfFile.getUploadedAt());
            statement.setInt(5, pdfFile.getUserId());
            statement.executeUpdate();
        }
    }

    public List<PdfFile> getAllFiles(int userId) throws SQLException {
        List<PdfFile> files = new ArrayList<>();
        String sql = "SELECT * FROM pdf_files WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String fileName = resultSet.getString("fileName");
                    String fileDescription = resultSet.getString("fileDescription");
                    String fileCategory = resultSet.getString("fileCategory");
                    String uploadedAt = resultSet.getString("uploadedAt");
                    files.add(new PdfFile(id, fileName, fileDescription, fileCategory, uploadedAt, userId));
                }
            }
        }
        return files;
    }


    public void deleteFile(int fileId) throws SQLException {
        String sql = "DELETE FROM pdf_files WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, fileId);
            statement.executeUpdate();
        }
    }
}
