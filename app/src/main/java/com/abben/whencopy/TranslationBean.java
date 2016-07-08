package com.abben.whencopy;

import java.util.List;

/**
 * Created by abbenyyyyyy on 16/6/19.
 */
public class TranslationBean {

    /**
     * usphonetic : hɛˈlo, hə-
     * phonetic : hə'ləʊ; he-
     * ukphonetic : hə'ləʊ; he-
     * explains : ["n. 表示问候， 惊奇或唤起注意时的用语","int. 喂；哈罗","n. (Hello)人名；(法)埃洛"]
     */

    private BasicBean basic;
    /**
     * translation : ["你好"]
     * basic : {"usphonetic":"hɛˈlo, hə-","phonetic":"hə'ləʊ; he-","ukphonetic":"hə'ləʊ; he-","explains":["n. 表示问候， 惊奇或唤起注意时的用语","int. 喂；哈罗","n. (Hello)人名；(法)埃洛"]}
     * query : hello
     * errorCode : 0
     * web : [{"value":["你好","您好","hello"],"key":"Hello"},{"value":["헬로 베이비","Hello Baby","Hello Baby"],"key":"Hello Baby"},{"value":["开心家族 (2010年电影)","开心鬼上身","片"],"key":"Hello Ghost"}]
     */

    private String query;
    private int errorCode;
    private List<String> translation;
    /**
     * value : ["你好","您好","hello"]
     * key : Hello
     */

    private List<WebBean> web;

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
        private String usphonetic;
        private String phonetic;
        private String ukphonetic;
        private List<String> explains;

        public String getUsphonetic() {
            return usphonetic;
        }

        public void setUsphonetic(String usphonetic) {
            this.usphonetic = usphonetic;
        }

        public String getPhonetic() {
            return phonetic;
        }

        public void setPhonetic(String phonetic) {
            this.phonetic = phonetic;
        }

        public String getUkphonetic() {
            return ukphonetic;
        }

        public void setUkphonetic(String ukphonetic) {
            this.ukphonetic = ukphonetic;
        }

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

    @Override
    public String toString() {
        return "TranslationBean{" +
                "basic=" + basic +
                ", query='" + query + '\'' +
                ", errorCode=" + errorCode +
                ", translation=" + translation +
                ", web=" + web +
                '}';
    }
}
