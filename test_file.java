import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.http.HttpServletRequest;

public class FileHandler {

    // VULNERABLE
    public File getFileVulnerable(HttpServletRequest request) {
        String filename = request.getParameter("filename"); // Input could be: "../../../../etc/passwd"
        String baseDir = "/var/www/uploads/";
        
        // BAD: Directly appending user input allows directory traversal escape
        return new File(baseDir + filename);
    }

    // SECURE FIX
    public File getFileSecure(HttpServletRequest request) throws IOException {
        String filename = request.getParameter("filename");
        Path baseDir = Paths.get("/var/www/uploads/").toRealPath();
        
        // GOOD: Resolve the path and explicitly verify it stays inside the base directory
        Path requestedPath = baseDir.resolve(filename).normalize();
        
        if (!requestedPath.startsWith(baseDir)) {
            throw new SecurityException("Access Denied: Path traversal detected!");
        }
        
        return requestedPath.toFile();
    }
}