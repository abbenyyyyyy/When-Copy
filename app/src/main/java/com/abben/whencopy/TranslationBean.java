package com.abben.whencopy;

import java.util.List;

/**
 * Created by abbenyyyyyy on 16/6/19.
 */
public class TranslationBean {


    private BasicBean basic;
    /**
     * translation : ["达克"]
     * basic : {"explains":["n. 音频压缩软件（Toms lossless Audio Kompressor）"]}
     * query : TAK
     * errorCode : 0
     * web : [{"value":["Tom's verlustfreier Audiokompressor","Toms Lossless Audio Kompressor","达府"],"key":"TAK"},{"value":["坂口拓","板口拓"],"key":"Tak Sakaguchi"},{"value":["麦德华"],"key":"Tak Mak"}]
     */

    private String query;
    private int errorCode;
    private List<String> translation;
    /**
     * value : ["Tom's verlustfreier Audiokompressor","Toms Lossless Audio Kompressor","达府"]
     * key : TAK
     */

    private List<WebBean> web;

    /**有道词典-基本词典*/
    public BasicBean getBasic() {
        return basic;
    }

    public void setBasic(BasicBean basic) {
        this.basic = basic;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<String> getTranslation() {
        return translation;
    }

    public void setTranslation(List<String> translation) {
        this.translation = translation;
    }

    public List<WebBean> getWeb() {
        return web;
    }

    public void setWeb(List<WebBean> web) {
        this.web = web;
    }

    public static class BasicBean {
        private List<String> explains;

        public List<String> getExplains() {
            return explains;
        }

        public void setExplains(List<String> explains) {
            this.explains = explains;
        }
    }

    public static class WebBean {
        private String key;
        private List<String> value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public List<String> getValue() {
            return value;
        }

        public void setValue(List<String> value) {
            this.value = value;
        }
    }
}
