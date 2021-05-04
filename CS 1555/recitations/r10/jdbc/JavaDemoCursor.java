

import java.sql.*;
import java.util.Properties;

public class JavaDemoCursor {
    public static void main(String args[]) throws
            SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://localhost/postgres";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "password");
        Connection dbcon =
                DriverManager.getConnection(url, props);

        Statement st = dbcon.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        ResultSet resultSet = st.executeQuery("SELECT * FROM recitation9.STUDENT");
        int pos = resultSet.getRow();      // Get cursor position, pos = 0
        boolean b = resultSet.isBeforeFirst();    // true
        int rid;
        String rname, rmajor;
        resultSet.next();                  // Move cursor to the first row
        pos = resultSet.getRow();          // Get cursor position, pos = 1

        System.out.println(pos);
        rid = resultSet.getInt("SID");
        rname = resultSet.getString("Name");
        rmajor = resultSet.getString(3);
        System.out.println(rid + " " + rname + " " + rmajor);

        b = resultSet.isFirst();    // true
        resultSet.last();           // Move cursor to the last row
        pos = resultSet.getRow();   // If table has 10 rows, pos = 10

        System.out.println(pos);
        rid = resultSet.getInt("SID");
        rname = resultSet.getString("Name");
        rmajor = resultSet.getString(3);
        System.out.println(rid + " " + rname + " " + rmajor);

        b = resultSet.isLast();     // true
        resultSet.afterLast();      // Move cursor past last row
        pos = resultSet.getRow();   // If table has 10 rows,
        // value would be 11
        b = resultSet.isAfterLast();   // true

    }
}