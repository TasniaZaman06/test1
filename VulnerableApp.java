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

    // ----------------------------------------------------------------
    // CWE-89 : SQL Injection
    // Risk   : Attacker can manipulate the SQL query via `username`
    //          e.g. username = "' OR '1'='1" dumps all users.
    // ----------------------------------------------------------------
    public void cwe89_sqlInjection(String username) throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

        // ❌ VULNERABLE: user input directly concatenated into SQL
        String query = "SELECT * FROM users WHERE username = '" + username + "'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            System.out.println("User: " + rs.getString("username"));
        }
        conn.close();
    }

    // ----------------------------------------------------------------
    // CWE-79 : Cross-Site Scripting (XSS)
    // Risk   : Attacker injects <script>alert(1)</script> as `userInput`
    //          which executes in the victim's browser.
    // ----------------------------------------------------------------
    public void cwe79_xss(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("search");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // ❌ VULNERABLE: unsanitized user input written directly to HTML response
        out.println("<html><body>");
        out.println("<h2>Search results for: " + userInput + "</h2>");
        out.println("</body></html>");
    }

    // ----------------------------------------------------------------
    // CWE-22 : Path Traversal
    // Risk   : Attacker passes filename = "../../etc/passwd" to read
    //          arbitrary files on the server.
    // ----------------------------------------------------------------
    public String cwe22_pathTraversal(String filename) throws IOException {
        // ❌ VULNERABLE: no canonicalization or boundary check on filename
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

    // ----------------------------------------------------------------
    // CWE-78 : OS Command Injection
    // Risk   : Attacker passes host = "google.com; rm -rf /" to execute
    //          arbitrary system commands.
    // ----------------------------------------------------------------
    public String cwe78_commandInjection(String host) throws IOException {
        // ❌ VULNERABLE: user-controlled input passed directly to shell
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

    // ----------------------------------------------------------------
    // CWE-502 : Insecure Deserialization
    // Risk   : Attacker sends a malicious serialized payload that
    //          executes arbitrary code upon deserialization.
    // ----------------------------------------------------------------
    public Object cwe502_insecureDeserialization(byte[] data) throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);

        // ❌ VULNERABLE: no class filtering or validation before deserializing
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object obj = ois.readObject();  // Arbitrary class instantiation
        ois.close();
        return obj;
    }

    // ----------------------------------------------------------------
    // CWE-611 : XML External Entity (XXE) Injection
    // Risk   : Attacker submits XML with an external entity like:
    //          <!ENTITY xxe SYSTEM "file:///etc/passwd">
    //          to read local files or trigger SSRF.
    // ----------------------------------------------------------------
    public org.w3c.dom.Document cwe611_xxe(InputStream xmlInput) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // ❌ VULNERABLE: external entities are NOT disabled
        // Should set: factory.setFeature("http://xml.org/sax/features/external-general-entities", false)
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(xmlInput);
    }

    // ----------------------------------------------------------------
    // CWE-200 : Sensitive Data Exposure
    // Risk   : Full stack traces and internal error messages are returned
    //          to the client, leaking system internals to attackers.
    // ----------------------------------------------------------------
    public String cwe200_sensitiveDataExposure(HttpServletRequest request) {
        try {
            String userId = request.getParameter("id");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE id=" + userId);
            // ... process result
            return "OK";
        } catch (Exception e) {
            // ❌ VULNERABLE: raw exception details sent to client
            return "Error: " + e.getMessage()
                 + "\nCause: " + e.getCause()
                 + "\nTrace: " + Arrays.toString(e.getStackTrace());
        }
    }

    // ----------------------------------------------------------------
    // CWE-352 : Cross-Site Request Forgery (CSRF)
    // Risk   : Any malicious site can craft a form that submits a POST
    //          to /transfer and the server accepts it because no CSRF
    //          token is checked.
    // ----------------------------------------------------------------
    public String cwe352_csrf(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return "Not logged in";
        }

        String toAccount = request.getParameter("toAccount");
        String amount    = request.getParameter("amount");

        // ❌ VULNERABLE: no CSRF token validation — any origin can trigger this
        System.out.println("Transferring $" + amount + " to account: " + toAccount);
        // bankService.transfer(fromUser, toAccount, Double.parseDouble(amount));

        return "Transfer of $" + amount + " to " + toAccount + " successful.";
    }

    // ----------------------------------------------------------------
    // Main — demonstrates each vulnerable method call
    // ----------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        VulnerableApp app = new VulnerableApp();

        System.out.println("=== CWE Demo (Educational Only) ===\n");

        // CWE-89: classic SQLi payload
        System.out.println("[CWE-89] SQL Injection input: ' OR '1'='1");
        // app.cwe89_sqlInjection("' OR '1'='1");

        // CWE-78: command injection payload
        System.out.println("[CWE-78] Command Injection input: google.com; whoami");
        String result = app.cwe78_commandInjection("google.com; whoami");
        System.out.println(result);

        // CWE-22: path traversal payload
        System.out.println("[CWE-22] Path Traversal input: ../../etc/passwd");
        // app.cwe22_pathTraversal("../../etc/passwd");

        System.out.println("\nAll other CWEs require a running servlet container.");
    }
}