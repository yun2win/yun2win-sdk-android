package y2w.model.messages;

/**
 * 消息类型已经相关常量定义
 * Created by yangrongfang on 2016/2/23.
 */
public class MessageType {
    public final static int MessageTypeCount = 13;

    public static String System = "system";
    public static String Text = "text";
    public static String Image = "image";
    public static String Audio = "audio";
    public static String Video = "video";
    public static String File = "file";
    public static String Location = "location";
    /***********系统*************/
    public final static int TextSystem = -2 ;
    /***********我方*************/
    public final static int TextRight = 0 ;
    public final static int ImageRight = 2 ;
    public final static int AudioRight = 4 ;
    public final static int VideoRight = 6 ;
    public final static int FileRight = 8 ;
    public final static int LocationRight = 10 ;

    /***********对方*************/
    public final static int TextLeft = 1;
    public final static int ImageLeft = 3;
    public final static int AudioLeft = 5 ;
    public final static int VideoLeft = 7 ;
    public final static int FileLeft = 9 ;
    public final static int LocationLeft = 11 ;

}
