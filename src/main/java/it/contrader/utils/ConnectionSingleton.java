package it.contrader.utils;

import java.sql.Connection;
import java.sql.DriverManager;


public class ConnectionSingleton
{

    private static Connection connection = null;

    private ConnectionSingleton()
    {

    }


    public static Connection getInstance()
    {
        if (connection == null)
        {
            try
            {
                String vendor="mysql";
                String driver="com.mysql.cj.jdbc.Driver";
                String host="127.0.0.1";
                String port="3306";
                String dbName="snowracedb";
                String username="";
                String password ="";
                String jdbcAdditionalParams="useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useLegacyDatetimeCode=false";

                Class<?> c = Class.forName(driver);

                System.out.println("Ho caricato: " + c.getName());
                String url = "jdbc:" + vendor + "://" + host + ":" + port + "/" + dbName+"?"+jdbcAdditionalParams;

                connection = DriverManager.getConnection(url, username, password);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return connection;
    }

}
