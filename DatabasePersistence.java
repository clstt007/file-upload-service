import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// Handles saving processed data records into a JSON file
public class DatabasePersistence {

    private static final String DB_FILE = "local.json";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static void saveToFile(Map<String, ProcessedData> database) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DB_FILE))) {
            writer.write("{\n");
            writer.write("\"records\": [\n");

            List<ProcessedData> records = new ArrayList<>(database.values());
            for (int i = 0; i < records.size(); i++) {
                ProcessedData data = records.get(i);
                writer.write("{\n");
                writer.write("\"id\": \"" + data.getId() + "\",\n");
                writer.write("\"fileName\": \"" + data.getFileName() + "\",\n");
                writer.write("\"lineCount\": " + data.getLineCount() + ",\n");
                writer.write("\"wordCount\": " + data.getWordCount() + ",\n");
                writer.write("\"fileSize\": " + data.getFileSize() + ",\n");
                writer.write("\"processedAt\": \"" + data.getProcessedAt().format(DATE_FORMAT) + "\"\n");
                writer.write("}");

                if (i < records.size() - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }

            writer.write("],\n");
            writer.write("\"totalRecords\": " + records.size() + ",\n");
            writer.write("\"lastUpdated\": \"" + LocalDateTime.now().format(DATE_FORMAT) + "\"\n");
            writer.write("}\n");

        } catch (IOException e) {
            System.err.println("Failed to save database: " + e.getMessage());
        }
    }
}