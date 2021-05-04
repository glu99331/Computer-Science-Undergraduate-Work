import java.util.Properties;
import java.sql.*;

public class JavaDemoFunctionCalling {
    public static void main(String args[]) throws
            ClassNotFoundException, SQLException {

        //check if jdbc driver is properly linked
        Class.forName("org.postgresql.Driver");

        //connection
        String url = "jdbc:postgresql://localhost:5432/";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "password");

        //connection
        Connection conn = DriverManager.getConnection(url, props);

        //create a query
        Statement st = conn.createStatement();
        String query1 = "SELECT * FROM STUDENT WHERE Major='CS'";

        //execute a query
        ResultSet resultSet = st.executeQuery(query1);

        //retrieve result
        String rId = "";
        String rName = "";
        String rMajor = "";
        while (resultSet.next()) {
            rId = resultSet.getString("SID");
            rName = resultSet.getString("Name");
            rMajor = resultSet.getString("major");
            System.out.println(rId + " " + rName + " " + rMajor);
        }


        //calling a function with return value
        String rReturn;
        CallableStatement properCase = conn.prepareCall("{ ? = call get_upper_case( ? ) }");
        properCase.registerOutParameter(1, Types.VARCHAR);
        properCase.setString(2, "rakan");
        properCase.execute();
        rReturn = properCase.getString(1);
        properCase.close();
        System.out.println(rReturn);


        //calling a function that returns query
        int id;
        PreparedStatement noInjection = conn.prepareStatement("select * from returnning_new_student_table(123)");
        ResultSet resultSet2 = noInjection.executeQuery();
        while (resultSet2.next()) {
            id = resultSet2.getInt("sid_r");
            rName = resultSet2.getString("name_r");
            System.out.println(id + " " + rName);
        }
    }
}