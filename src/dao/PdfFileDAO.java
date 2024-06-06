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
        String sql = "INSERT INTO pdf_files (file_name, file_path) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pdfFile.getFileName());
            statement.setString(2, pdfFile.getFilePath());
            statement.executeUpdate();
        }
    }

    public List<PdfFile> getAllFiles() throws SQLException {
        List<PdfFile> files = new ArrayList<>();
        String sql = "SELECT * FROM pdf_files";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String fileName = resultSet.getString("file_name");
                String filePath = resultSet.getString("file_path");
                String uploadedAt = resultSet.getString("uploaded_at");
                files.add(new PdfFile(id, fileName, filePath, uploadedAt));
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
