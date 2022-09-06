package menber;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.Date;

public class t_order {
    String id;
    String user_id;
    int no;
    double price;
    Date createdate;
    t_order()
    {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        createdate= new Date(System.currentTimeMillis());
    }
}
