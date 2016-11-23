package y2w.model;


import y2w.entities.WebValueEntity;

/**
 * Created by maa2 on 2016/4/8.
 */
public class WebValue {

    private WebValueEntity entity;

    public WebValue(WebValueEntity entity){
        this.entity = entity;
    }

    public WebValueEntity getEntity() {
        return entity;
    }

}
