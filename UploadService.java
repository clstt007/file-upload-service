import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;

// Service for handling file uploads
public class UploadService {

    private static final Logger logger = Logger.getLogger(UploadService.class.getName());

    private Set<String> allowedExtensions;
    private long maxFileSize;
    private final Map<String, ProcessedData> localDB;

    public UploadService() {
        this.localDB = new HashMap<>();
        setupLogger();
        loadConfig();
        logger.info("Upload service started");
    }

    // Load configuration from config.properties file
    private void loadConfig() {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            props.load(input);

            // Load allowed file types
            String types = props.getProperty("allowed.file.types", "txt,csv");
            allowedExtensions = new HashSet<>(Arrays.asList(types.split(",")));

            // Load max file size
            String maxSize = props.getProperty("max.file.size.mb", "10");
            maxFileSize = Long.parseLong(maxSize) * 1024 * 1024;

            logger.info("Config loaded - Allowed types: " + allowedExtensions + ", Max size: " + maxSize + "MB");

        } catch (IOException e) {
            // Use defaults if config file not found
            allowedExtensions = Set.of("txt", "csv");
            maxFileSize = 10 * 1024 * 1024;
            logger.warning("Config file not found, using defaults");
        }
    }

    private void setupLogger() {
        try {
            logger.setUseParentHandlers(false);

            // Custom formatter: [YYYY-MM-DD HH:mm:ss.SSS] [LEVEL]: message
            java.util.logging.Formatter customFormatter = new java.util.logging.Formatter() {
                private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

                @Override
                public String format(LogRecord record) {
                    return String.format("[%s] [%s]: %s%n",
                            dateFormat.format(new Date(record.getMillis())),
                            record.getLevel(),
                            record.getMessage());
                }
            };

            // Console handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(customFormatter);
            consoleHandler.setLevel(Level.ALL);
            logger.addHandler(consoleHandler);

            // File handler - logs to upload_service.log
            FileHandler fileHandler = new FileHandler("upload_service.log", true);
            fileHandler.setFormatter(customFormatter);
            fileHandler.setLevel(Level.ALL);
            logger.addHandler(fileHandler);

            logger.setLevel(Level.ALL);

        } catch (Exception e) {
            System.err.println("Logger setup failed: " + e.getMessage());
        }
    }

    // Upload and process files
    public UploadResult uploadFile(String fileName, byte[] fileContent) {
        logger.info("Upload started: " + fileName);

        try {
            // Validate input
            if (fileName == null || fileName.trim().isEmpty()) {
                throw new FileException("Filename cannot be empty");
            }
            if (fileContent == null || fileContent.length == 0) {
                throw new FileException("File content cannot be empty");
            }

            // Check file type
            if (!is_allowed_file(fileName)) {
                String msg = "Invalid file type. Only " + allowedExtensions + " allowed";
                logger.warning(msg);
                return UploadResult.failure(msg);
            }

            // Check file size
            if (fileContent.length > maxFileSize) {
                String msg = "File too large (max " + (maxFileSize / 1024 / 1024) + "MB)";
                logger.warning(msg);
                return UploadResult.failure(msg);
            }

            // Process the file
            ProcessedData data = process_file(fileName, fileContent);

            // Save to local DB
            save_to_database(data);

            logger.info("Upload completed: " + fileName + ", ID: " + data.getId() +
                    ", Lines: " + data.getLineCount() + ", Words: " + data.getWordCount());
            return UploadResult.success(data);

        } catch (FileException e) {
            logger.severe("Upload failed: " + e.getMessage());
            return UploadResult.failure(e.getMessage());
        } catch (Exception e) {
            logger.severe("Unexpected error: " + e.getMessage());
            return UploadResult.failure("Upload failed");
        }
    }

    // Check file type
    private boolean is_allowed_file(String fileName) {
        int lastDot = fileName.lastIndexOf('.');

        if (lastDot == -1 || lastDot == fileName.length() - 1) {
            return false;
        }

        String extension = fileName.substring(lastDot + 1).toLowerCase();
        return allowedExtensions.contains(extension);
    }

    // Process file and count lines and words
    private ProcessedData process_file(String fileName, byte[] fileContent) throws FileException {
        logger.info("Processing: " + fileName);

        try {
            String content = new String(fileContent, StandardCharsets.UTF_8);

            int lineCount = 0;
            int wordCount = 0;

            BufferedReader reader = new BufferedReader(new StringReader(content));
            String line;

            while ((line = reader.readLine()) != null) {
                lineCount++;

                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    String[] words = trimmed.split("\\s+");
                    wordCount += words.length;
                }
            }

            logger.info("Processed - Lines: " + lineCount + ", Words: " + wordCount);

            return new ProcessedData(fileName, lineCount, wordCount, fileContent.length);

        } catch (IOException e) {
            throw new FileException("Processing failed: " + e.getMessage());
        }
    }

    // Save processed data to local DB
    private void save_to_database(ProcessedData data) {
        localDB.put(data.getId(), data);
        logger.info("Saved to database: " + data.getId());

        // Save to JSON file
        DatabasePersistence.saveToFile(localDB);
    }
}