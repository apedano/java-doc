import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FilesFeatureExample {
    static String filePath = System.getProperty("user.dir") + "/java-code/java11/resources/";
    static String file_1 = filePath + "file_1.txt";

    /**
     * Files.readString() and .writeString()
     */
    public static void main(String[] args) throws IOException {

        // reading files is much easier now
        // not to be used with huge files
        Path path = Paths.get(file_1);
        String content = Files.readString(path);
        System.out.println(content);


        Path newFile = Paths.get(filePath + "newFile.txt");
        if(!Files.exists(newFile)) {
            Files.writeString(newFile, "some str", StandardOpenOption.CREATE);
        } else {
            Files.writeString(newFile, "some str", StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
}
