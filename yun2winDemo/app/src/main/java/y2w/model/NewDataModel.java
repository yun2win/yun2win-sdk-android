package y2w.model;

import android.os.Parcel;
import android.os.Parcelable;

import y2w.entities.searchEntities.SearchMessage;
import y2w.entities.searchEntities.SearchUserConversation;

/**
 * Created by Administrator on 2016/4/25.
 */
public class NewDataModel{
    private String type = "";//contactentity,userconversationentity messageentity
    private Contact contact;
    private SearchUserConversation searchUserConversation;
    private SearchMessage searchMessage;
    public  NewDataModel(String type,Contact contact,SearchUserConversation searchUserConversation,SearchMessage searchMessage){
        setType(type);
        setSearchContact(contact);
        setSearchUserconversation(searchUserConversation);
        setSearchMessage(searchMessage);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SearchUserConversation getSearchUserconversation() {
        return searchUserConversation;
    }

    public void setSearchUserconversation(SearchUserConversation searchUserConversation) {
        this.searchUserConversation = searchUserConversation;
    }

    public Contact getSearchContact() {
        return contact;
    }

    public void setSearchContact(Contact contact) {
        this.contact = contact;
    }

    public SearchMessage getSearchMessage() {
        return searchMessage;
    }

    public void setSearchMessage(SearchMessage searchMessage) {
        this.searchMessage = searchMessage;
    }
}
