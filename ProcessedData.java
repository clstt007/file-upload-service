import java.time.LocalDateTime;
import java.util.UUID;

// Data model for processed files
public class ProcessedData {

    private final String id;
    private final String fileName;
    private final int lineCount;
    private final int wordCount;
    private final long fileSize;
    private final LocalDateTime processedAt;

    public ProcessedData(String fileName, int lineCount, int wordCount, long fileSize) {
        this.id = UUID.randomUUID().toString();
        this.fileName = fileName;
        this.lineCount = lineCount;
        this.wordCount = wordCount;
        this.fileSize = fileSize;
        this.processedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineCount() {
        return lineCount;
    }

    public int getWordCount() {
        return wordCount;
    }

    public long getFileSize() {
        return fileSize;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    @Override
    public String toString() {
        return "File: " + fileName + ", Lines: " + lineCount + ", Words: " + wordCount;
    }
}