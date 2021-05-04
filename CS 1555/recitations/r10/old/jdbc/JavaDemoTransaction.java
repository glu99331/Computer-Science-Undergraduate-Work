 
 
import java.sql.*;
import java.util.Properties;

public class JavaDemoTransaction {
    public static void main(String args[]) throws
            SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://localhost/postgres";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "password");
        Connection conn =
                DriverManager.getConnection(url, props);

        Statement st = conn.createStatement();
        try {
            conn.setAutoCommit(false);
            st.executeUpdate("INSERT INTO recitation9.student (sid, name, class, major) VALUES ('145', 'Marios', 3, 'CS');");
            st.executeUpdate("INSERT INTO recitation9.student (sid, name, class, major) VALUES ('156', 'Andreas', 3, 'CS');");
            conn.commit();
        } catch (SQLException e1) {
            try {
                conn.rollback();
            } catch (SQLException e2) {
                System.out.println(e2.toString());
            }
        }


    }
}