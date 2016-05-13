package com.prime.test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
/**
 * 
 * @author Reza Rahimi <rahimi@mcreations.com>
 *
 */
public class TestJdbcLdap {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Class.forName("com.novell.sql.LDAPDriver");
        Driver driver = DriverManager.getDriver("jdbc:ldap");
        String url = "jdbc:ldap://localhost:10389;useCleartext=true";

        Connection conn = null;
        // conn = connectWithProperties(url);
        conn = connectWithConnectionString(driver, url);
        Map<String, Integer> columns = new LinkedHashMap<String, Integer>();
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        java.sql.Statement statement = conn.createStatement();
        ResultSet users = statement
                .executeQuery("SELECT uid, givenName, mail , cn, sn,userPassword FROM inetOrgPerson");

        extractData(users, columns, data);
        printDataTabular(columns, data);
        columns = new LinkedHashMap<String, Integer>();
        data = new ArrayList<Map<String, String>>();
        ResultSet roles = statement
                .executeQuery("SELECT cn,roleOccupant_s FROM organizationalRole WHERE {fn SetSeparator(';')} = ';'");
        extractData(roles, columns, data);
        printDataTabular(columns, data);

        conn.close();
    }

    private static Connection connectWithConnectionString(Driver driver, String url) throws SQLException {
        String connectionString = url + ";user=cn=admin,dc=example,dc=com" + ";password=secret"
                + ";baseDN=dc=example,dc=com";
        Connection conn = driver.connect(connectionString, null);
        return conn;
    }

    private static Connection connectWithProperties(String url) throws SQLException {
        Properties props = new Properties();
        props.put("user", "cn=admin,dc=example,dc=com");
        props.put("password", "secret");
        props.put("baseDN", "dc=example,dc=com");
        props.put("useCleartext", true);
        Connection conn = DriverManager.getConnection(url, props);
        return conn;
    }

    private static void extractData(ResultSet users, Map<String, Integer> columns, List<Map<String, String>> data)
            throws SQLException {
        ResultSetMetaData usersMetaData = users.getMetaData();
        int userCcolumnCount = usersMetaData.getColumnCount();
        while (users.next()) {
            int j = 1;
            Map<String, String> row = new LinkedHashMap<String, String>();
            for (int i = 1; i <= userCcolumnCount; i++) {
                String key = usersMetaData.getColumnName(i);
                Integer maxColWith = 0;
                String value = users.getString(i);
                value = value == null ? "" : value;
                int length = value.length();
                if (length < key.length())
                    length = key.length();
                if (columns.containsKey(key)) {
                    maxColWith = columns.get(key);
                    if (length > maxColWith) {
                        columns.put(key, length);
                    }
                } else {
                    columns.put(key, length);
                }
                row.put(key, value);
            }
            data.add(row);
        }
    }

    private static void printDataTabular(Map<String, Integer> columns, List<Map<String, String>> data) {
        int maxWidth = 2;
        for (Iterator iterator = columns.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            maxWidth += (columns.get(key) + 3);
        }
        System.out.println(StringUtils.repeat("", "-", maxWidth));
        System.out.print("| ");
        for (Iterator iterator = columns.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            System.out.print(StringUtils.rightPad(key, columns.get(key)) + " | ");
        }
        System.out.println();
        System.out.println(StringUtils.repeat("", "-", maxWidth));
        for (Iterator iterator = data.iterator(); iterator.hasNext();) {
            Map<String, String> map = (Map<String, String>) iterator.next();
            System.out.print("| ");
            for (Iterator iterator2 = map.keySet().iterator(); iterator2.hasNext();) {
                String key = (String) iterator2.next();
                String val = map.get(key);
                val = val == null ? " " : val;
                System.out.print(StringUtils.rightPad(val, columns.get(key)) + " | ");
            }
            System.out.println();

        }
        System.out.println(StringUtils.repeat("", "-", maxWidth));
        System.out.println();
    }
}
