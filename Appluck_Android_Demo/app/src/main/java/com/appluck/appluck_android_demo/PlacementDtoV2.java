package com.appluck.appluck_android_demo;

import androidx.annotation.Keep;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: songjiaxing
 * @Date: 2022/06/14/15:20
 * @Description:
 * @Version: 1.0
 */
@Keep
public class PlacementDtoV2 {

    private String link;

    private List<Creative> creatives;

    private int width;

    private int height;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<Creative> getCreatives() {
        return creatives;
    }

    public void setCreatives(List<Creative> creatives) {
        this.creatives = creatives;
    }

    public Creative pop() {
        if (creatives == null || creatives.isEmpty()) {
            return null;
        }
        final Creative remove = creatives.remove(0);
        creatives.add(remove);
        return remove;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Keep
    public static class Creative {
        private Integer id;
        private String src;

        public Creative() {
        }

        public Creative(Integer id, String src) {
            this.id = id;
            this.src = src;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }
    }

    @Keep
    public static class ReportInfo {
        private String event;
        private String creativeId;
        private String gaid;
        private String sk;
        private String v;

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public String getCreativeId() {
            return creativeId;
        }

        public void setCreativeId(String creativeId) {
            this.creativeId = creativeId;
        }

        public String getGaid() {
            return gaid;
        }

        public void setGaid(String gaid) {
            this.gaid = gaid;
        }

        public String getSk() {
            return sk;
        }

        public void setSk(String sk) {
            this.sk = sk;
        }

        public String getV() {
            return v;
        }

        public void setV(String v) {
            this.v = v;
        }
    }
}
