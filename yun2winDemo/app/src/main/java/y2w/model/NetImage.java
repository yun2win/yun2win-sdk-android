package y2w.model;


import y2w.entities.NetImageEntity;

/**
 * Created by maa2 on 2016/4/8.
 */
public class NetImage {

    private NetImageEntity entity;

    public NetImage(NetImageEntity entity){
        this.entity = entity;
    }

    public NetImageEntity getEntity() {
        return entity;
    }


}
