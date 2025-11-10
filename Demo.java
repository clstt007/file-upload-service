import java.nio.charset.StandardCharsets;

// Main program to test the file upload service
public class Demo {

    public static void main(String[] args) {
        System.out.println("File Upload Service Demo\n");

        UploadService service = new UploadService();

        // Test 1: Valid .txt file
        System.out.println("\nTest 1: Valid .txt file");
        testFile(service, "test.txt", "Hello World\nSecond line");

        // Test 2: Valid .csv file
        System.out.println("\nTest 2: Valid .csv file");
        testFile(service, "data.csv", "Name,Age\nJohn,30");

        // Test 3: Invalid file type (.pdf)
        System.out.println("\nTest 3: Invalid file type (.pdf)");
        testFile(service, "document.pdf", "some content");

        // Test 4: Empty filename
        System.out.println("\nTest 4: Empty filename");
        testFile(service, "", "content");

        // Test 5: Null content
        System.out.println("\nTest 5: Null content");
        testFile(service, "test.txt", null);

        // Test 6: Large file (> 10MB)
        System.out.println("\nTest 6: Large file (> 10MB)");
        byte[] largeFile = new byte[11 * 1024 * 1024];
        UploadResult result = service.uploadFile("large.txt", largeFile);
        System.out.println("Result: " + (result.isSuccess() ? "Success" : "Failed - " + result.getErrorMessage()));

        // Test 7: UTF-8 encoding
        System.out.println("\nTest 7: UTF-8 encoding");
        String utf8Content = "Chinese: 你好";
        testFile(service, "utf8.txt", utf8Content);

        // Test 8: Mixed line endings (\r\n, \n, \r)
        System.out.println("\nTest 8: Mixed line endings");
        String mixedEndings = "Line1\r\nLine2\nLine3\rLine4";
        testFile(service, "mixed.txt", mixedEndings);

        // Test 9: Special characters
        System.out.println("\nTest 9: Special characters");
        String specialChars = "Symbols: @#$%^&*()\nEmoji: 😀🎉";
        testFile(service, "special.txt", specialChars);

        System.out.println("\nDemo Complete");
    }

    private static void testFile(UploadService service, String fileName, String content) {
        byte[] bytes = (content == null) ? null : content.getBytes(StandardCharsets.UTF_8);
        UploadResult result = service.uploadFile(fileName, bytes);

        if (result.isFailure()) {
            System.out.println("Failed - " + result.getErrorMessage());
        }
    }
}