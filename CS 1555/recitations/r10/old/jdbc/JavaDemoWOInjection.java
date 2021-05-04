 

import java.sql.*;
import java.util.Properties;

public class JavaDemoWOInjection {
    private static boolean loggedIn;

    public static void main(String args[]) throws
            SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://localhost/postgres";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "password");
Connection conn =
        DriverManager.getConnection(url, props);

String username = "admin";
String password = "1' OR '1'='1'; drop schema recitation9 cascade; select 'something";
PreparedStatement stmt = conn.prepareStatement("SELECT * FROM recitation9.users WHERE username=? AND password=?");
stmt.setString(1, username);
stmt.setString(2, password);
ResultSet rs = stmt.executeQuery();
if (rs.next()) {
    loggedIn = true;
    System.out.println("Successfully logged in");
} else {
    System.out.println("Username and/or password not recognized");
}


    }
}