package com.y2w.uikit.frame;

/**
 * Created by maa2 on 2016/1/21.
 */
public interface IEmoticonSelectedListener {
    void onEmojiSelected(String key);

    void onStickerSelected(String categoryName, String stickerName);
}
