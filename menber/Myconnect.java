package menber;
import java.sql.*;
public class Myconnect {
    public Myconnect(){}
    public Myconnect(String User,String password)
    {
        AdminUsername=User;
        AdminPassword=password;
    }




    String url = "jdbc:mysql://150.158.25.142:3306/shopping?useUnicode=true&characterEncoding=utf8&useSSL=true";
    String AdminUsername="jacob";
    String AdminPassword="1234";
    String sql = "SELECT username   FROM t_user where id = '1001' and password = '1234';";
    public Connection connection;
    public Statement statement;
    public ResultSet rs;
    // 连接
    public int ConnectMysql() throws SQLException, ClassNotFoundException {
        // 连接
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(url,AdminUsername,AdminPassword);
        statement=connection.createStatement();
        return 0;
    }
    //****************************
    //       查询SQL方法
    //****************************
    public boolean Search() throws SQLException {
        rs=null;
        // 查询
        statement=connection.createStatement();
        rs = statement.executeQuery(sql);
        // 如果sql语句为空
        if(sql.isEmpty())
        {
            return false;
        }
        if (rs==null)
        {
            return false;
        }
        else
        {
            sql=null;
            return true;
        }
    }
    public boolean Search(String sql) throws SQLException {
        rs = statement.executeQuery(sql);
        // 如果sql语句为空
        if(sql.isEmpty())
        {
            return false;
        }
        if (rs.isBeforeFirst())
        {
            return false;
        }
        else
        {
            sql=null;
            return true;
        }
    }

    //****************************
    //       插入SQL方法
    //****************************
    public boolean InsertData(String sql) throws SQLException {
        //System.out.println(sql);
        Statement stat = connection.createStatement();
        return stat.execute(sql);
    }
    public boolean InsertData() throws SQLException {
        Statement stat = connection.createStatement();
        return stat.execute(sql);
    }

    //****************************
    //       修改SQL方法
    //****************************
    public boolean Update(String sql) throws SQLException {
        //System.out.println(sql);
        int i=statement.executeUpdate(sql);
        System.out.println(sql);
        System.out.println(i);
        return i!=0?true:false;
    }
    public boolean Update() throws SQLException {
        statement.executeUpdate(sql);
        return true;
    }


    // 全部关闭
    public void close() throws SQLException {
        rs.close();
        statement.close();
        connection.close();
    }

    // 切换用户,将之前的身份关闭，以新身份登录
    public void SetAdminUser(String name,String password) throws SQLException, ClassNotFoundException {
        //close();
        AdminPassword=password;
        AdminUsername=name;
        ConnectMysql();
    }
    // 设置类内sql
    public void SetSql(String sql)
    {
        this.sql=sql;
    }









}
