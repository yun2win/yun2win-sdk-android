package y2w.model;

import y2w.entities.EmojiEntity;
import y2w.manage.CurrentUser;

/**
 * Created by maa2 on 2016/4/8.
 */
public class Emoji {

    private EmojiEntity entity;

    public Emoji(EmojiEntity entity){
        this.entity = entity;
    }

    public EmojiEntity getEntity() {
        return entity;
    }


}
