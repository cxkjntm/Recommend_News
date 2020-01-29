package us.codecraft.webmagic.sql;

        import java.sql.*;

public class Dbconn {
    public static Statement ReturnStatement() throws SQLException {
        ConnectionManager pool = ConnectionManager.getInstance();
        Connection connection = pool.getConnection();
        Statement statement = connection.createStatement();
        return statement;
    }
}
