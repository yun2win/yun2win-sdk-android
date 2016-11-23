package y2w.base;

import java.io.Serializable;
import java.net.URLDecoder;
import java.net.URLEncoder;

import y2w.common.Config;

/**
 * 访问url
 * Created by maa2 on 2016/1/6.
 */
public class Urls implements Serializable {

    //public final static String HOST = "http://192.168.0.133"; //验证测试
    public final static String HOST_PORT = Config.Host_Port;


    /*********************************************User*********************************************/
    public final static String User_Register = HOST_PORT + "/v1/users/register";
    public final static String User_Login = HOST_PORT + "/v1/users/login";
    public final static String User_Avatar_Def = "/images/default.jpg";//HOST_PORT+"/images/default.jpg"

    public final static String User_Update = HOST_PORT + "/v1/users/";
    public final static String User_Get = HOST_PORT + "/v1/users/";
    public final static String User_SetPassword = HOST_PORT + "/v1/users/setPassword";

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

    public final static String User_UserConversations_Delete = HOST_PORT+"/v1/users/";

    public final static String User_UserConversations_Update = HOST_PORT+"/v1/users/";


    /*********************************************Messages*********************************************/
    public final static String User_Messages_Send = HOST_PORT+"/v1/sessions/";
    public final static String User_Messages_Send_Last = "/messages";

    public final static String User_Messages_Get = HOST_PORT+"/v1/sessions/";
    public final static String User_Messages_Get_Last = "/messages";
    public final static String User_Messages_Get_Hostory = "/messages/history";

    public final static String User_Message_Update = HOST_PORT+"/v1/sessions/";
    public final static String User_Message_Update_Last = "/messages/";

    public final static String User_Message_Delete = HOST_PORT+"/v1/sessions/";
    public final static String User_Message_Delete_Last = "/messages/";

    /*********************************************Sessions*********************************************/
    public final static String User_SessionP2p_Get = HOST_PORT+"/v1/sessions/p2p/";

    public final static String User_Session_Single_Get = HOST_PORT+"/v1/sessions/single/";

    public final static String User_Session_Create = HOST_PORT+"/v1/sessions";
    public final static String User_Session_Get = HOST_PORT+"/v1/sessions/";
    public final static String User_Session_Update = HOST_PORT+"/v1/sessions/";

    public final static String User_Sessions_Store = HOST_PORT+"/v1/users/";
    public final static String User_Sessions_Store_Last = "/userSessions";
    public final static String User_Sessions_Get = HOST_PORT+"/v1/users/";
    public final static String User_Sessions_Get_Last = "/userSessions";
    public final static String User_Sessions_Delete = HOST_PORT+"/v1/users/";
    public final static String User_Sessions_Delete_Last = "/userSessions/";
    public final static String User_Sessions_Update = HOST_PORT+"/v1/users/";
    public final static String User_Sessions_Update_Last = "/userSessions/";

    public final static String User_SessionMember_Add = HOST_PORT+"/v1/sessions/";
    public final static String User_SessionMember_Add_Last = "/members";
    public final static String User_SessionMember_Delete = HOST_PORT+"/v1/sessions/";
    public final static String User_SessionMember_Delete_Last = "/members/";
    public final static String User_SessionMembers_Get = HOST_PORT+"/v1/sessions/";
    public final static String User_SessionMembers_Get_Last = "/members";
    public final static String User_SessionMembers_update = HOST_PORT+"/v1/sessions/";
    public final static String User_SessionMembers_update_Last = "/members/";

    /*********************************************File*********************************************/
    public final static String User_Messages_File_UpLoad = HOST_PORT+"/v1/attachments";
    public final static String User_Messages_File_DownLoad = HOST_PORT+"/v1/";
    public final static String User_Messages_EMOJI_DownLoad = HOST_PORT;

    /*********************************************emojis*********************************************/
    public final static String User_Messages_Emojis_Get = HOST_PORT+"/v1/emojis";
    /*********************************************AppVersion*********************************************/
    public final static String User_App_Version_Get = HOST_PORT+"/android.json";



}
