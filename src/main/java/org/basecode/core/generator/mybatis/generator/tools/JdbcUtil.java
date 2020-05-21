package org.basecode.core.generator.mybatis.generator.tools;


import org.basecode.core.generator.mybatis.generator.tools.JdbcConfig;

import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Jdbc操作工具类
 */
public class JdbcUtil {
    //private static final Log logger = LogFactory.getLog(JdbcUtil.class);
	public static final char UNDERLINE = '_';
    /**
     * 从结果集里当前行某一列数据，列号index从1开始
     *
     * @param rs
     * @param index 列号 从1开始， 如1，2，3....
     * @return
     * @throws SQLException
     */
    public static Object getResultSetValue(ResultSet rs, int index)
            throws SQLException {
        Object obj = rs.getObject(index);
        if (obj instanceof Blob) {
            obj = rs.getBytes(index);
        } else if (obj instanceof Clob) {
            obj = clobToString(rs.getClob(index));
        } else if ((obj != null) && (obj.getClass().getName().startsWith("oracle.sql.TIMESTAMP"))) {
            obj = rs.getTimestamp(index);
        } else if ((obj != null) && (obj.getClass().getName().startsWith("oracle.sql.DATE"))) {
            String metaDataClassName = rs.getMetaData().getColumnClassName(index);
            if (("java.sql.Timestamp".equals(metaDataClassName)) || ("oracle.sql.TIMESTAMP".equals(metaDataClassName))) {
                obj = rs.getTimestamp(index);
            } else {
                obj = rs.getDate(index);
            }
        } else if ((obj != null) && (obj instanceof Date) &&
                ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index)))) {
            obj = rs.getTimestamp(index);
        }

        return obj;
    }

    /**
     * 把Oracle的Clob类型转化为String
     *
     * @param clob
     * @return
     */
    public static String clobToString(Clob clob) {
        String str = "";
        Reader inStream;
        try {
            inStream = clob.getCharacterStream();
            char[] c = new char[(int) clob.length()];
            inStream.read(c);
            //data是读出并需要返回的数据，类型是String
            str = new String(c);
            inStream.close();
            return str;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static void safelyClose(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void safelyClose(PreparedStatement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void safelyClose(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void safelyClose(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void safelyClose(ResultSet rs, Statement stmt) {
        safelyClose(rs);
        safelyClose(stmt);
    }

    public static void safelyClose(ResultSet rs, Statement stmt, Connection conn) {
        safelyClose(rs);
        safelyClose(stmt);
        safelyClose(conn);
    }

    public static void safelyClose(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        safelyClose(rs);
        safelyClose(pstmt);
        safelyClose(conn);
    }

    public static void safelyClose(PreparedStatement pstmt, Connection conn) {
        safelyClose(pstmt);
        safelyClose(conn);
    }

    public static void safelyClose(Statement stmt, Connection conn) {
        safelyClose(stmt);
        safelyClose(conn);
    }

    public static Connection getConn(JdbcConfig jdbcConfig) {
        String driver = jdbcConfig.getDriver();
        String url = jdbcConfig.getUrl();
        String user = jdbcConfig.getUser();
        String password = jdbcConfig.getPassword();
        try {
            //System.out.println("数据库驱动="+driver);
            Class.forName(driver).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, user, password);
            if (con != null) {
                //System.out.println("取得jdbc数据连接成功！");
            } else {
                System.out.println("取得jdbc数据连接失败！" + jdbcConfig);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
    
    /**
     * 驼峰格式字符串转换为下划线格式字符串
     * 
     * @param param
     * @return
     */
    public static String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 下划线格式字符串转换为驼峰格式字符串
     * 
     * @param param
     * @return
     */
    public static String underlineToCamel(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 下划线格式字符串转换为驼峰格式字符串2
     * 
     * @param param
     * @return
     */
    public static String underlineToCamel2(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        StringBuilder sb = new StringBuilder(param);
        Matcher mc = Pattern.compile("_").matcher(param);
        int i = 0;
        while (mc.find()) {
            int position = mc.end() - (i++);
            sb.replace(position - 1, position + 1, sb.substring(position, position + 1).toUpperCase());
        }
        return sb.toString();
    }
}
