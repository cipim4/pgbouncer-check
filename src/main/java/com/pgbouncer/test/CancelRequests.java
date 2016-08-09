package com.pgbouncer.test;

import org.postgresql.jdbc.PgStatement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CancelRequests {
    public static void main(String[] args) {

        if (args.length < 3) {
            System.out.println("Input arguments: <db_url> <user_name> <password>");
            return;
        }

        String db = args[0];
        String user = args[1];
        String password = args[2];

        try {
            Connection conn = DriverManager.getConnection(db, user, password);

            for (int i = 0; i < 10; i++) {
                Statement stmt = getStatementWithTimeout(conn);
                stmt.execute("SELECT 789");
                ResultSet r = stmt.getResultSet();
                r.next();
                if (r.getInt(1) == 42)
                    throw new Exception("Oops! I just got the result of the previous query...");
                r.close();
                stmt.close();

                stmt = getStatementWithTimeout(conn);
                stmt.execute("SELECT 42;");
                stmt.close();

                Thread.sleep(10);
            }

            System.out.println("It was all fine.. dang!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Statement getStatementWithTimeout(Connection conn) throws SQLException {
        PgStatement stmt = (PgStatement)conn.createStatement();
        stmt.setQueryTimeoutMs(100);
        return stmt;
    }

}
