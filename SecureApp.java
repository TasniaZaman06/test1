import java.io.*;
import java.nio.file.*;
import java.sql.*;

public class SecureApp {

    public enum UserRole { USER, ADMIN }

    public void cwe89_falsePositive(UserRole role) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/mydb";
        Connection conn = DriverManager.getConnection(url, "root", "password");
        String query = "SELECT * FROM config WHERE visibility = '" + role.name() + "'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
    }

    public void cwe22_falsePositive(String profileName) throws IOException {
        if (!profileName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Invalid profile name");
        }

        File file = new File("/var/app/profiles/" + profileName + ".json");
        BufferedReader reader = new BufferedReader(new FileReader(file));
    }

    public void cwe78_falsePositive(String serviceId) throws IOException {
        int safeId = Integer.parseInt(serviceId);

        String command = "/usr/local/bin/check_service.sh --id " + safeId;
        Runtime.getRuntime().exec(command);
    }
}