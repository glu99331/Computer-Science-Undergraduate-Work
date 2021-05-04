

import java.util.Properties;
import java.sql.*;

public class JavaDemo {
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
        String query1 =
                "SELECT SID, Name, Major FROM recitation9.STUDENT WHERE Major='CS'";
        ResultSet res1 = st.executeQuery(query1);
        String rid;
        String rname, rmajor;
        while (res1.next()) {
            rid = res1.getString("SID");
            rname = res1.getString("Name");
            rmajor = res1.getString(3);
            System.out.println(rid + " " + rname + " " + rmajor);
        }
    }
}