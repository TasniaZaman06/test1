import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;

public class VulnerableApp {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "password";


    public void cwe89_sqlInjection(String username) throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        String query = "SELECT * FROM users WHERE username = '" + username + "'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            System.out.println("User: " + rs.getString("username"));
        }
        conn.close();
    }

    public void cwe79_xss(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("search");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h2>Search results for: " + userInput + "</h2>");
        out.println("</body></html>");
    }


    public String cwe22_pathTraversal(String filename) throws IOException {
        File file = new File("/var/app/uploads/" + filename);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        reader.close();
        return content.toString();
    }


    public String cwe78_commandInjection(String host) throws IOException {
        String command = "ping -c 1 " + host;
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(command);

        BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream()));

        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        return output.toString();
    }


    public Object cwe502_insecureDeserialization(byte[] data) throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object obj = ois.readObject();  
        ois.close();
        return obj;
    }


    public org.w3c.dom.Document cwe611_xxe(InputStream xmlInput) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(xmlInput);
    }


    public String cwe200_sensitiveDataExposure(HttpServletRequest request) {
        try {
            String userId = request.getParameter("id");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE id=" + userId);
            return "OK";
        } catch (Exception e) {
            return "Error: " + e.getMessage()
                 + "\nCause: " + e.getCause()
                 + "\nTrace: " + Arrays.toString(e.getStackTrace());
        }
    }


    public String cwe352_csrf(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return "Not logged in";
        }

        String toAccount = request.getParameter("toAccount");
        String amount    = request.getParameter("amount");
        System.out.println("Transferring $" + amount + " to account: " + toAccount);
        return "Transfer of $" + amount + " to " + toAccount + " successful.";
    }


    public static void main(String[] args) throws Exception {
        VulnerableApp app = new VulnerableApp();

        System.out.println("=== CWE Demo (Educational Only) ===\n");
        System.out.println("[CWE-89] SQL Injection input: ' OR '1'='1");
        System.out.println("[CWE-78] Command Injection input: google.com; whoami");
        String result = app.cwe78_commandInjection("google.com; whoami");
        System.out.println(result);
        System.out.println("[CWE-22] Path Traversal input: ../../etc/passwd");
        System.out.println("\nAll other CWEs require a running servlet container.");
    }
}