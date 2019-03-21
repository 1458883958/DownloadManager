package com.wdl.dmdemo;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;

/**
 * 创建时间： 2019/3/21 10:02
 * 描述：    DownloadManager工具类
 */
@SuppressWarnings("unused")
public class DMUtil {
    private Context mContext;
    private static final String PACKAGE_NAME = "com.android.providers.downloads";
    private static final String URL = "https://s.beta.myapp.com/myapp/rdmexp/exp/file2/2019/03/20/comcardplusmobilecardplus_1.0.1_d891081f-6af3-59b9-bf55-c0bcff1cdc0e.apk";
    private static final String TITLE = "whale";
    private static final String DESC = "whale";

    public DMUtil(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 可能会出错Cannot update URI: content://downloads/my_downloads/-1
     * 检查下载管理器是否被禁用
     *
     * @return true
     */
    public boolean checkDownloadManagerEnable() {
        try {
            // 获取下载管理器的状态
            int state = mContext.getPackageManager().getApplicationEnabledSetting(PACKAGE_NAME);
            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                // 跳转系统设置
                try {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + PACKAGE_NAME));
                    mContext.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    mContext.startActivity(intent);
                }
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public long download() {
        Uri uri = Uri.parse(URL);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        // 设置允许使用的网络类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        // 下载中以及下载后都显示通知栏
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 设置文件保存位置
        request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, "whale.apk");
        request.setTitle(TITLE);
        request.setDescription(DESC);
        request.setMimeType("application/vnd.android.package-archive");
        // 可被媒体扫描器找到
        request.allowScanningByMediaScanner();
        // 可见可管理
        request.setVisibleInDownloadsUi(true);
        // 返回任务ID
        DownloadManager dm = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        try {
            return dm.enqueue(request);
        } catch (Exception e) {
            return -1;
        }

    }

    /**
     * 下载前先移除前一个任务，防止重复下载
     *
     * @param id long
     */
    public void clearCurrentTask(long id) {
        DownloadManager dm = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        try {
            dm.remove(id);
        } catch (Exception ignored) {
        }
    }
}
