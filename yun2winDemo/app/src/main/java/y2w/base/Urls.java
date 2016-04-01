package y2w.base;

import java.io.Serializable;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 访问url
 * Created by maa2 on 2016/1/6.
 */
public class Urls implements Serializable {

    public final static String HTTP = "http://";
    public final static String HTTPS = "https://";
    //public final static String HOST = "http://192.168.0.133"; //验证测试
    public final static String HOST = "http://112.74.210.208";
    //public final static String HOST = "http://192.168.0.181";
    public final static String HOST_PORT = HOST+":8080";


    /*********************************************User*********************************************/
    public final static String User_Register = HOST_PORT + "/v1/users/register";
    public final static String User_Login = HOST_PORT + "/v1/users/login";
    public final static String User_Avatar_Def = HOST_PORT+"/images/default.jpg";

    public final static String User_Update = HOST_PORT + "/v1/users/";
    public final static String User_Get = HOST_PORT + "/v1/users/";

    public final static String User_Contact_Update = HOST_PORT + "/v1/users/";
    public final static String User_Contact_Update_Last = "/contacts/";
    public final static String User_Contact_Add = HOST_PORT+"/v1/users/";
    public final static String User_Contacts_Get = HOST_PORT+"/v1/users/";
    public final static String User_Contacts_Last = "/contacts";
    public final static String User_Contact_Get = HOST_PORT+"/v1/users/";
    public final static String User_Contact_Delete = HOST_PORT+"/v1/users/";
    public final static String User_Contact_Delete_Last = "/contacts/";

    public final static String User_Contact_Search = HOST_PORT+"/v1/users?filter_term=";

    public final static String User_UserConversations_Get = HOST_PORT+"/v1/users/";
    public final static String User_UserConversations_Last = "/userConversations";

    public final static String User_UserConversation_Get = HOST_PORT+"/v1/users/";
    public final static String User_UserConversation_Last = "/userConversations/";

    public final static String User_UserConversations_delete = HOST_PORT+"/v1/users/";

    /*********************************************Messages*********************************************/
    public final static String User_Messages_Send = HOST_PORT+"/v1/sessions/";
    public final static String User_Messages_Send_Last = "/messages";

    public final static String User_Messages_Get = HOST_PORT+"/v1/sessions/";
    public final static String User_Messages_Get_Last = "/messages";
    public final static String User_Messages_Get_Hostory = "/messages/history";
    /*********************************************Sessions*********************************************/
    public final static String User_SessionP2p_Get = HOST_PORT+"/v1/sessions/p2p/";

    public final static String User_Session_Create = HOST_PORT+"/v1/sessions";
    public final static String User_Session_Get = HOST_PORT+"/v1/sessions/";

    public final static String User_Sessions_Store = HOST_PORT+"/v1/users/";
    public final static String User_Sessions_Store_Last = "/userSessions";
    public final static String User_Sessions_Get = HOST_PORT+"/v1/users/";
    public final static String User_Sessions_Get_Last = "/userSessions";
    public final static String User_Sessions_Delete = HOST_PORT+"/v1/users/";
    public final static String User_Sessions_Delete_Last = "/userSessions/";

    public final static String User_SessionMember_Add = HOST_PORT+"/v1/sessions/";
    public final static String User_SessionMember_Add_Last = "/members";
    public final static String User_SessionMember_Delete = HOST_PORT+"/v1/sessions/";
    public final static String User_SessionMember_Delete_Last = "/members";
    public final static String User_SessionMembers_Get = HOST_PORT+"/v1/sessions/";
    public final static String User_SessionMembers_Get_Last = "/members";

    /*********************************************File*********************************************/
    public final static String User_Messages_File_UpLoad = HOST_PORT+"/v1/attachments";






}
