package com.aircas.visualclient.model;

import com.google.common.collect.ImmutableList;
import com.spotify.docker.client.messages.Image;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ImageModel {

    private String repository;
    private String tag;
    private String imageId;
    private String created;
    private String size;

    public ImageModel(Image image) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        this.setCreated(formatter.format(new Date(Long.parseLong(image.created()) * 1000)));
        this.setSize(toSizeString(image.size()));
        this.setImageId(image.id().split(":")[1].substring(0, 12));
        try {
            int index = image.repoTags().get(0).lastIndexOf(":");
            this.setRepository(image.repoTags().get(0).substring(0, index));
            this.setTag(image.repoTags().get(0).substring(index + 1));
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            if (this.repository == null) this.setRepository("");
            if (this.tag == null) this.setTag("");
        }
    }

    public ImageModel() {
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String toSizeString(long size) {
        if (size < 1000) {
            return size + "B";
        } else if (size < 1000000) {
            return size / 1000 + "KB";
        } else if (size < 1000000000L) {
            return size / 1000000 + "MB";
        } else {
            return size / 1000000000L + "GB";
        }
    }
}
