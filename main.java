import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import menber.*;
import sun.java2d.pipe.SpanShapeRenderer;

import javax.swing.*;

import static java.lang.System.*;

public class main {
    public static Myconnect mc=new Myconnect("Mysql账户","Mysql账户密码");// 普遍用户
    public static Myconnect mc2=new Myconnect("Mysql账户","Mysql账户密码");// 只能查看视图
    public static String userID;
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Scanner s1 = new Scanner(System.in);
        while (true) {
            System.out.println("************************************************");
            System.out.println("  1. 登录       2. 注册     3. 查看商品列表   4. 退出");
            switch (s1.next()) {
                case "1":
                    login();
                    break;
                case "2":
                    register();
                    break;
                case "3":
                    out.println("请先登录！");
                    login();
                    break;
                case "4":
                    mc.close();
                    return;
                default:
                    System.out.println("输入错误，请重试！");
            }
        }
    }
    // 登录方法
    public static void  login() throws ClassNotFoundException, SQLException {
        Scanner s1=new Scanner(System.in);
        System.out.println("请输入账户和密码");
        System.out.print("账户:");
        String username=s1.next();
        System.out.print("密码:");
        String password=s1.next();
        userID=username;
        // 检查与数据库是否连接成功
try {
    mc.ConnectMysql();
}catch (Exception e)
{
    System.out.println("连接失败！请检查网络设置");
    return;
}
        boolean b=  mc.Search("SELECT status  FROM t_user where id = '"+username+"' and password = '"+password+"';");
        // 用户是否正确
        if (b)
        {
            System.out.println("账户或密码错误！请重试");
            mc.close();
            return;
        }
        mc.rs.next();
        if (mc.rs.getInt(1)!=1)
        {
            System.out.println("对不起！您的账户已冻结");
            mc.close();
            return;
        }

        if(mc.Search("SELECT username  FROM t_user where id = '"+username+"' and password = '"+password+"';"))
        {
            out.println("登录失败！账户或密码错误");
            return;
        }
        // 账户可以使用，且密码账户正确，进入购物系统
        mc.rs.next();
        System.out.println("                   欢迎 "+mc.rs.getString("username")+"!");
        //**************
        //  进入购物页面
        //**************
        ShoppingPage();
    }
    // 注册方法(完成)
    public static void  register() throws SQLException {
        Scanner s1=new Scanner(System.in);

            System.out.println("请输入您要注册的用户名");
            String username = s1.next();

            // 默认只有查询用户的权限
            try {
                mc.ConnectMysql();
            }catch (Exception e)
            {
                System.out.println("连接失败！请检查网络设置");
                return;
            }

            // 判断是否同名
            if (!mc.Search("SELECT * FROM t_user where username ='"+username+"';")) {
                System.out.println("对不起，您的用户名重复");
                return;
            }
            // 插入
            mc.Search("select MAX(id)FROM t_user;");
            mc.rs.next();
            int id = mc.rs.getInt(1)+1;
            System.out.println("请输入您的密码");
            String password = s1.next();
            System.out.println("请输入您的电话");
            String phone = s1.next();
            System.out.println("请输入您的地址");
            String address = s1.next();

            String sql="INSERT INTO t_user (id,username,password,phone,address,status) " +
                    "VALUES('"+id+"','"+username+"','"+password+"','"+phone+"','"+address+"',1);";
            if(!mc.InsertData(sql))
            System.out.println("注册成功! 您的账号为："+id);
    }
    // 购物页面
    public static void ShoppingPage() throws SQLException, ClassNotFoundException {
        mc.ConnectMysql();
        mc.Search("select * from t_product;");


        System.out.println("1.查看订单     2.查看购物车       3.结算");
        System.out.println("ws用于翻页，ad用于选择商品，c加入购物车,f刷新商品");

        int pages = 0, select = 0;
        ArrayList<t_product> goods = new ArrayList<t_product>();
        while (mc.rs.next()) {
            t_product t1 = new t_product(
                    mc.rs.getString("id"),
                    mc.rs.getString("name"),
                    mc.rs.getDouble("price"));
            goods.add(t1);
        }

        // 打印第一页的商品
        for (int i = 0; i < 4&&i<goods.size(); i++) {
            if (i != select) {
                System.out.print("\t"+i +"."+ goods.get(i).name);
                System.out.print("("+goods.get(i).price+")\t");
            }
            else
            {
                System.out.print("\t*"+goods.get(i).name);
                System.out.print("("+goods.get(i).price+")\t");
            }
        }

        System.out.println();
        System.out.println("4.注销        5.修改密码   ");

        Scanner sc=new Scanner(System.in);
        // 这里判断是否刷新
        while (true) {
            switch (sc.next())
            {
                case "1":   // 查看订单
                    ViewOrder();
                    break;
                case"2":    // 查看购物车
                    ShowShoppingCart();
                    break;
                case "3":   // 结算
                    CloseAccount();
                    break;
                case "4":   // 注销
                    return;
                case "5":   // 修改密码
                    System.out.println("请输入您的新密码:");
                    mc.statement.executeUpdate("UPDATE t_user SET password='"+sc.next()+ "' WHERE id='"+userID+"';");
                    System.out.println("修改成功！请重新登录！");
                    return;
                case "c":    // 加入购物车
                    //goods.get(pages*4+select+1);
                    // 查找数据库是否买过该商品？
                    boolean b=mc.Search("select * from t_shopping_cart where u_id ='"+userID+"' " +
                            "and p_id='"+goods.get(pages*4+select).id+"';");
                    if (!b) {
                        mc.rs.next();
                        int num = mc.rs.getInt("num");
                        num++;
                        mc.Update("UPDATE t_shopping_cart SET num='" + num + "' WHERE u_id='" + userID + "' " +
                                "and p_id='" + goods.get(pages * 4 + select).id + "';");
                    }else {
                        //否，插入
                        mc.InsertData("INSERT INTO t_shopping_cart(u_id,p_id,num,price,p_name) " +
                                "VALUES('" + userID + "','" +
                                goods.get(pages * 4 + select).id +
                                "','1','" +
                                goods.get(pages * 4 + select).price +
                                "','"+goods.get(pages * 4 + select).name+"');");
                    }break;
                case "f":
                    pages = 0; select = 0;
                    while (mc.rs.next()) {
                    t_product t1 = new t_product(
                            mc.rs.getString("id"),
                            mc.rs.getString("name"),
                            mc.rs.getDouble("price"));
                    goods.add(t1);
                }break;
                case "s":
                    if (pages<(goods.size()+3)/4-1)
                    {
                        pages++;
                        select=0;
                    }break;
                case "w":
                    if (pages>0)
                    {
                        pages--;
                        select=0;
                    }
                    break;
                case "a":
                    if (select==0&&(pages*4+goods.size()%4)==goods.size())
                    {
                       select = goods.size()%4-1;
                    }
                    else if (select==0)
                    {
                        select=3;
                    }
                    else
                    {
                        select--;
                    }
                    break;
                case "d":
                    if (select==goods.size()%4-1&&(pages*4+goods.size()%4)==goods.size())
                    {
                        select=0;
                    }
                    else if (select==3)
                    {
                        select=0;
                    }else
                    {
                        select++;
                    }
                    break;
                default:
                    System.out.println("输入无效,请重试");
            }

            // 重新显示页面
            System.out.println("1.查看订单     2.查看购物车       3.结算");
            System.out.println("ws用于翻页，ad用于选择商品，c加入购物车,空格刷新商品\n");

            for (int i = 0; i < 4&&i+pages*4<goods.size(); i++) {
                if (i != select) {
                    System.out.print("\t"+i +"."+ goods.get(i+pages*4).name);
                    System.out.print("("+goods.get(i+pages*4).price+")\t");
                }
                else
                {
                    System.out.print("\t*"+goods.get(i+pages*4).name);
                    System.out.print("("+goods.get(i+pages*4).price+")\t");
                }
            }
            System.out.println("\n\n4.注销        5.修改密码   ");

        }
    }
    // 购物车内的删除和修改数量,结算操作
    public static void ShowShoppingCart() throws SQLException {


        // 根据userID查找其购物车，存放到cart
        ArrayList<t_shopping_cart> car=new ArrayList<t_shopping_cart>();
        double total = t_shopping_cart.refresh(mc,car,userID);


        Scanner sc=new Scanner(in);
        int select=0,pages=0;
        while (true) {
            out.println("1. 结账      2. 删除      3. 修改数量    4. 返回");
            out.println("      使用ws翻页，ad进行商品选择");

            //********打印购物车里的内容**********
            for (int i = 0; i < 4&&pages*4+i<car.size(); i++) {
                if (i != select) {
                    System.out.print("\t"+i +"."+ car.get(pages*4+i).p_name);
                    System.out.print("("+car.get(pages*4+i).price*car.get(pages*4+i).num+")\t");
                }
                else
                {
                    System.out.print("\t*"+car.get(pages*4+i).p_name);
                    System.out.print("("+car.get(pages*4+i).price*car.get(pages*4+i).num+")\t");
                }
            }

            switch (sc.next())
            {
                case "1":
                    CloseAccount();
                    break;
                case "2":
                    // 清空关于该产品的信息
                    mc.Update("DELETE  FROM t_shopping_cart WHERE p_id='"+car.get(pages*4+select).p_id+"';");
                    total=t_shopping_cart.refresh(mc,car,userID);
                    break;
                case "3":
                    out.println("请输入修改后的数量");
                    int num=sc.nextInt();
                    mc.Update("UPDATE t_shopping_cart SET num='"+num+"' " +
                            "where p_id='"+car.get(pages*4+select).p_id+"';");
                    total=t_shopping_cart.refresh(mc,car,userID);
                    break;
                case "4":
                    return;
                case "s":
                    if (pages<(car.size()+3)/4-1)
                    {
                        pages++;
                        select=0;
                    }break;
                case "w":
                    if (pages>0)
                    {
                        pages--;
                        select=0;
                    }
                    break;
                case "a":
                    if (select==0&&(pages*4+car.size()%4)==car.size())
                    {
                        select = car.size()%4-1;
                    }
                    else if (select==0)
                    {
                        select=3;
                    }
                    else
                    {
                        select--;
                    }
                    break;
                case "d":
                    if (select==car.size()%4-1&&(pages*4+car.size()%4)==car.size())
                    {
                        select=0;
                    }
                    else if (select==3)
                    {
                        select=0;
                    }else
                    {
                        select++;
                    }
                    break;
                default:
                    out.println("输入有误，请重试！");
            }
        }
    }
    // 查看订单
    public static void ViewOrder() throws SQLException {
        // 根据用户id查询orderID（t_order）
        ArrayList<String>orderID=new ArrayList<String>();
        mc.Search("select id from t_order where user_id='"+userID+"';");
        while (mc.rs.next())
        {
            orderID.add(mc.rs.getString("id"));
        }
        // 根据orderID查询详细订单（t_item）
        for (int i=0;i<orderID.size();i++) {
            out.println("订单号: "+orderID.get(i));double num=0;
            mc.Search("select * from t_item where order_id='"+orderID.get(i)+"';");
            while (mc.rs.next()) {
                out.print("用户账户："+mc.rs.getString("id")+"  ");
                out.print("商品编号："+mc.rs.getString("product_id")+"  ");
                out.print("数量"+mc.rs.getString("num")+"  ");
                out.print("单价"+mc.rs.getString("price")+"  ");
                out.println();
                num=num+mc.rs.getInt("num")*mc.rs.getDouble("price");
            }
            out.println("\t\t\t\t总价格:"+num);
        }
        return;
    }

    // 结算订单
    public static void CloseAccount() throws SQLException {
        ArrayList<t_shopping_cart> car=new ArrayList<t_shopping_cart>();
        double total = t_shopping_cart.refresh(mc,car,userID);
        // 清空该用户的购物车信息
        mc.Update("DELETE  FROM t_shopping_cart WHERE u_id='"+userID+"';");
        // 添加订单信息数据
        long totalMilliSeconds = System.currentTimeMillis();
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        String d=formatter.format(calendar.getTime());

        // 插入订单列表
        mc.InsertData("INSERT INTO t_order(id,user_id,no,price,createdate)"+
                "VALUES ('"+totalMilliSeconds+"','"+userID+"','"+car.size()+"','"+total+"','"+d+"');");

        // 插入item列表
        for (int i=0;i<car.size();i++) {
            mc.InsertData("INSERT INTO t_item(id,product_id,num,price,order_id)" +
                    "VALUES ('" + userID + "','" + car.get(i).p_id + "','" +
                    car.get(i).num + "','" + car.get(i).price + "','" + totalMilliSeconds + "');");
        }

        // 重新显示购物车
        total=t_shopping_cart.refresh(mc,car,userID);
        out.println("已清算购物车所有内容");
    }
}
