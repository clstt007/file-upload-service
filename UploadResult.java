// Result wrapper for upload operations with success/failure states.
public class UploadResult {

    private final boolean success;
    private final String errorMessage;
    private final ProcessedData data;

    private UploadResult(boolean success, String errorMessage, ProcessedData data) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    // For successful upload
    public static UploadResult success(ProcessedData data) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null for success result");
        }
        return new UploadResult(true, null, data);
    }

    // For failed upload
    public static UploadResult failure(String errorMessage) {
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Error message required for failure result");
        }
        return new UploadResult(false, errorMessage, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ProcessedData getData() {
        return data;
    }

    @Override
    public String toString() {
        return success ? "Success: " + data : "Failure: " + errorMessage;
    }
}