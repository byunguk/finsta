package com.homework.finsta.model;

import com.homework.finsta.util.Const;

import org.json.JSONObject;

/**
 * Created by kbw815 on 7/26/15.
 */
public class Data {
    private String id;
    private String type;
    private String imageUrl;
    private String videoUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public static Data fromJSONObject(JSONObject dataJSONObject)
    {
        Data data = new Data();

        data.setId(dataJSONObject.optString(Const.FIELD_ID));
        data.setType(dataJSONObject.optString(Const.FIELD_TYPE));
        JSONObject imagesJSONObject = dataJSONObject.optJSONObject(Const.FIELD_IMAGES);
        JSONObject videosJSONObject = dataJSONObject.optJSONObject(Const.FIELD_VIDEOS);
        if (Const.FIELD_VIDEO.equals(data.type))
        {
            JSONObject videoSRJSONObject = videosJSONObject.optJSONObject(Const.FIELD_STANDARD_RESOLUTION);
            data.setVideoUrl(videoSRJSONObject.optString(Const.FIELD_URL));
        }
        JSONObject imageSRJSONObject = imagesJSONObject.optJSONObject(Const.FIELD_STANDARD_RESOLUTION);
        data.setImageUrl(imageSRJSONObject.optString(Const.FIELD_URL));

        return data;
    }
}
