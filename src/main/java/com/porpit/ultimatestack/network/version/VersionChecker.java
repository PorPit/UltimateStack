package com.porpit.ultimatestack.network.version;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.porpit.ppcore.PPCore;
import com.porpit.ppcore.util.JSONHelper;
import com.porpit.ultimatestack.UltimateStack;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class VersionChecker {

    public static String newerModVersion = null;
    public static String updateDate = null;
    public static String updateInfo = null;
    public static String downloadUrl = "http://job.porpit.cn:233/job/UltimateStack/";
    public static final String VERSION_JSON_URL="https://raw.githubusercontent.com/PorPit/UltimateStack/1.12.2/version.json";

    public VersionChecker() {
        new Thread(() -> getUpdateData()).start();
    }

    private void getUpdateData() {
         JsonObject jsonObject= JSONHelper.getJsonObject(VERSION_JSON_URL);
         if(jsonObject!=null&&!jsonObject.isJsonNull()){
             newerModVersion = jsonObject.getAsJsonObject("promos").get("1.12.2-latest").getAsString();
             updateDate = jsonObject.getAsJsonObject("updateDate").get(newerModVersion).getAsString();
             updateInfo = jsonObject.getAsJsonObject("1.12.2").get(newerModVersion).getAsString();
             UltimateStack.logger.debug("Json:" + jsonObject);
             UltimateStack.logger.info("最新版本号:" + newerModVersion);
             UltimateStack.logger.info("更新日期:" +  updateDate);
             UltimateStack.logger.info("更新日志:" +  updateInfo);
         }
    }
}
