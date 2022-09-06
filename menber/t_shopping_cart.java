package menber;

import java.sql.SQLException;
import java.util.ArrayList;

import static java.lang.System.out;

public class t_shopping_cart {
    public String  u_id;
    public String  p_id;
    public int     num;
    public double  price;
    public String p_name;
    public static double refresh(Myconnect mc, ArrayList<t_shopping_cart> arr,String userID) throws SQLException {
        arr.clear();
        mc.Search("select * from t_shopping_cart where u_id='"+userID+"';");
        if (!mc.rs.isBeforeFirst())
        {
            System.out.println("购物车为空，您还没有购买任何东西噢！");
            return -1;
        }

        double money=0;
        while (mc.rs.next())
        {
            t_shopping_cart temp=new t_shopping_cart();
            temp.u_id=mc.rs.getString("u_id");
            temp.p_id=mc.rs.getString("p_id");
            temp.num=mc.rs.getInt("num");
            temp.price=mc.rs.getDouble("price");
            temp.p_name =mc.rs.getString("p_name");
            arr.add(temp);
            // 计算总价格
            money=money+temp.num* temp.price;
        }
        return money;
    }

}
