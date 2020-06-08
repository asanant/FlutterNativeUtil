package com.example.flutter_toolplugin;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FlutterToolpluginPlugin */
public class FlutterToolpluginPlugin implements MethodCallHandler {
   static  Registrar Registrar;
  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
      Registrar=registrar;
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_toolplugin");
    channel.setMethodCallHandler(new FlutterToolpluginPlugin());
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("getExternalStorage")) {
      result.success(Environment.getExternalStorageDirectory().getPath());
    } else if (call.method.equals("getChannelId")) {
       result.success(getChannelId(Registrar.context()));
    } else {
      result.notImplemented();
    }
  }



    private String getChannelId(Context context) {
        String channelId = "";
        ApplicationInfo appInfo = context.getApplicationInfo();
        String ret = extractZipComment(appInfo.sourceDir);
        //去除压缩工具添加注释后多出的乱码或者去除最后一位
        Pattern reg = Pattern.compile("\\w+");
        Matcher matcher = reg.matcher(ret);
        while (matcher.find()) {
            ret = matcher.group();
        }
        Log.d("d",String.format("注释%s",ret));
        if (ret != null) {
            String[] split = ret.split("_");
            if (split != null && split.length >= 2) {
                channelId = split[0];
                //  channelType = split[1]
            }
        }
        if (TextUtils.isEmpty(channelId)) {
            try {
                appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                if (appInfo == null) {
                    channelId = "11";
                }else{

                    try {
                        channelId =String.valueOf(appInfo.metaData.getInt("APP_CHANNEL"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (channelId=="0") {
                        channelId = appInfo.metaData.getString("APP_CHANNEL");

                    }
                }
                Log.d("d",String.format("channelId:%s",channelId));

            } catch ( Exception e) {
                channelId = "11";
                e.printStackTrace();
            }
        }

        return channelId;
    }



    /**
     * 读取APK注释的渠道信息
     * @param filename
     * @return
     */

    public  String extractZipComment (String filename) {
        String retStr = null;
        try {
            File file = new File(filename);
            int fileLen = (int)file.length();

            FileInputStream in = new FileInputStream(file);

            /* The whole ZIP comment (including the magic byte sequence)
             * MUST fit in the buffer
             * otherwise, the comment will not be recognized correctly
             *
             * You can safely increase the buffer size if you like
             */
            byte[] buffer = new byte[Math.min(fileLen, 8192)];
            int len;

            in.skip(fileLen - buffer.length);

            if ((len = in.read(buffer)) > 0) {
                retStr = getZipCommentFromBuffer (buffer, len);
            }

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retStr;
    }

    private  String getZipCommentFromBuffer (byte[] buffer, int len) {
        byte[] magicDirEnd = {0x50, 0x4b, 0x05, 0x06};
        int buffLen = Math.min(buffer.length, len);
        //Check the buffer from the end
        for (int i = buffLen-magicDirEnd.length-22; i >= 0; i--) {
            boolean isMagicStart = true;
            for (int k=0; k < magicDirEnd.length; k++) {
                if (buffer[i+k] != magicDirEnd[k]) {
                    isMagicStart = false;
                    break;
                }
            }
            if (isMagicStart) {
                //Magic Start found!
                int commentLen = buffer[i+20] + buffer[i+22]*256;
                int realLen = buffLen - i - 22;
                System.out.println ("ZIP comment found at buffer position " + (i+22) + " with len="+commentLen+", good!");
                if (commentLen != realLen) {
                    System.out.println ("WARNING! ZIP comment size mismatch: directory says len is "+
                            commentLen+", but file ends after " + realLen + " bytes!");
                }
                String comment = new String (buffer, i+22, Math.min(commentLen, realLen));
                return comment;
            }
        }
        System.out.println ("ERROR! ZIP comment NOT found!");
        return null;
    }


}
