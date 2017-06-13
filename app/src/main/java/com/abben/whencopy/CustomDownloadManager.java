package com.abben.whencopy;

import android.app.DownloadManager;
import android.content.Context;

/**
 * Created by Shaolin on 2017/6/13.
 */

public class CustomDownloadManager {
    DownloadManager downloadManager;

    public CustomDownloadManager(Context context){
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public class Builder{
        Context context;
        String downloadUrl;

        public Builder(Context context){
            this.context = context;
        }

        /**设置下载链接*/
        public Builder setDownloadUrl(String downloadUrl){
            this.downloadUrl = downloadUrl;
            return this;
        }



        public CustomDownloadManager create(){
            CustomDownloadManager customDownloadManager = new CustomDownloadManager(context);

            return customDownloadManager;
        }

    }

    /**开始下载*/
    public void start(){

    }

}
