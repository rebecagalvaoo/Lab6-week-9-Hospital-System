
package ca.hospital.week9_hospitalsystem;

import java.sql.Connection;
import java.sql.SQLException;

public class db_test {

    public static void main(String[] args) {

        try {
            Connection connection =
                    db_connection.getConnection();

            System.out.println("Database connected successfully!");

            connection.close();

        } catch (SQLException e) {

            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}
