package us.codecraft.webmagic.sql;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Stack;


public class ConnectionManager {
    int minConn = 2; // 连接池最少连接数
    int maxConn = 22;// 连接池最多连接数
    String user = "root"; // 连接数据库使用的用户名
    String password = "123456"; // 连接数据库使用的用户名密码
    String url = "jdbc:mysql://localhost:3306/Recommend_News?useUnicode=true&characterEncoding=utf8&useSSL=false";// 所连接的数据库的URL(连接字符串)
    String logFile = "D:\\spider\\dbpool.log";// 日志文件路径
    PrintWriter loger = null; // 记录日志Log的对象
    int connAmount = 0; // 当前现有的连接的个数
    Stack<Connection> connStack = new Stack<Connection>(); // 使用Stack保存数据库连接, 也可以使用数组, Vector等保存
    /**
     * 构造函数 功能: 1.初始化 2.打开Log文件 3.注册驱动程序 4.根据最小连接数生成连接
     */
    private ConnectionManager() {
        try {
            loger = new PrintWriter(new FileWriter(logFile, true), true);
        } catch (IOException ioe) {
            loger = new PrintWriter(System.err);
        }
        // 注册驱动程序
        try {
            Class.forName("com.mysql.jdbc.Driver");
            log("成功注册驱动程序");
        } catch (Exception e) {
            log("无法注册驱动程序");
        }
        // 根据最少连接数生成连接
        for (int i = 0; i < minConn; i++) {
            Connection con = newConnection();
            if (con != null)
                connStack.push(con);
        }
    }

    // 将文本信息msg写入日志文件
    private void log(String msg) {
        loger.println(new Date() + ":" + msg);
    }

    private static ConnectionManager instance; // 数据库连接池ConnManager的实例

    public static synchronized ConnectionManager getInstance() {
        // 返回唯一实例。如果是第一次调用此方法，则创建该实例
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    // 创建新的连接
    private Connection newConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, user, password);
            connAmount++;
            log("连接池创建一个新的连接");
        } catch (SQLException e) {
            log("无法创建下列url的连接: " + url);
            return null;
        }
        return con;
    }

    public synchronized Connection getConnection() {
        /**
         * 从连接池获得一个可用连接.如没有空闲的连接且当前连接数小于最大连接数限制, 则创建新连接
         */
        Connection con = null;
        log("从连接池申请一个连接");
        log("现在可用的连接总数为: " + connStack.size());
        if (!connStack.empty()) {
            con = (Connection) connStack.pop();
        } else if (connAmount < maxConn) {
            con = newConnection();
        } else {
            try {
                log("等待连接");
                wait(100000);// 等待其他进程释放连接,单位是毫秒
                return getConnection();
            } catch (InterruptedException ie) {

            }
        }
        return con;
    }

    public synchronized void freeConnection(Connection con) {
        // 将不再使用的连接返回给连接池
        connStack.push(con);
        notifyAll();// 唤醒在此对象监视器上等待的所有线程
        log("归还一个连接到连接池");
    }
}