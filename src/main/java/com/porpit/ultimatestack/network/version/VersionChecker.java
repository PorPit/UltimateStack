package com.porpit.ultimatestack.network.version;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.porpit.ultimatestack.UltimateStack;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class VersionChecker {

    public static String newerModVersion=null;
    public static String updateDate=null;
    public static String updateInfo=null;
    public VersionChecker()
    {
        new Thread(() -> getUpdateData()).start();
    }

    private void getUpdateData(){
        String result = "";
        BufferedReader in = null;
        try {
            UltimateStack.logger.info("尝试获取新版本信息！");
            String urlNameString = "https://raw.githubusercontent.com/PorPit/UltimateStack/1.12.2/version.json";
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段

            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            JsonObject jsonObject=new JsonParser().parse(result).getAsJsonObject();
            if(!jsonObject.isJsonNull())
            {

                    newerModVersion=jsonObject.get("modversion").getAsString();
                    updateDate=jsonObject.get("updateDate").getAsString();
                    updateInfo=jsonObject.get("info").getAsString();

            }

        } catch (Exception e) {
            System.out.println("获取新版本数据出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }
}
