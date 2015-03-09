package pt.lsts.asa.util;

import pt.lsts.asa.settings.Settings;

import java.util.Locale;

import android.util.Log;
import android.widget.VideoView;

/**
 * Created by jloureiro on 18-12-2014.
 */
public class StringUtils {

    public static final String TAG = "StringUtils";

	public static String getCamUrl(){
		String cam = Settings.getString("model","Digital Camera");
		cam = cam.toLowerCase(Locale.UK);
		switch (cam){
            case "digital camera":
                return getAxisUrlWithResolutionSpecification();
            case "analog camera":
                return getAxisGenericUrl();
			default:
				return "ERROR";
		}

	}

    public static String getAxisGenericUrl(){
        //http://10.0.20.113/axis-cgi/mjpg/video.cgi

        String protocol = "http";
        String ip_port= Settings.getString("cam_ip", "10.0.20.113");
        String location = "axis-cgi/mjpg/video.cgi";

        //String resolution = Settings.getString("resolution","640x480");

        String completeUrl = protocol;
        completeUrl += "://";
        completeUrl += ip_port;
        completeUrl += "/";
        completeUrl += location;
        //completeUrl += resolution;

        Log.i(TAG, completeUrl);
        return completeUrl;
    }

	public static String getAxisUrlWithResolutionSpecification(){
		//http://10.0.20.112/axis-cgi/mjpg/video.cgi?date=1&clock=1&camera=1&resolution=640x480

		String protocol = "http";
		String ip_port= Settings.getString("cam_ip", "10.0.20.112");
		String location = "axis-cgi/mjpg/video.cgi?date=0&clock=0&camera=1&resolution=";
        String resolution = Settings.getString("resolution","640x480");

        String completeUrl = protocol;
        completeUrl += "://";
        completeUrl += ip_port;
        completeUrl += "/";
        completeUrl += location;
        completeUrl += resolution;

        Log.i(TAG, completeUrl);
		return completeUrl;

	}

	public static String validateResolution(String resolution, VideoView videoView){
		String[] res = resolution.split("x");
		if (res.length!=2){
			resolution = getVideoViewResolution(videoView);
			//Toast.makeText(context, "used original - 1st one: "+resolution, Toast.LENGTH_SHORT).show();
		}else {
			try {
				Integer.parseInt(res[0]);
				Integer.parseInt(res[1]);
			} catch (Exception e) {
				resolution = getVideoViewResolution(videoView);
				//Toast.makeText(context, "used original - 2nd one: "+resolution, Toast.LENGTH_SHORT).show();
			}
		}
		return resolution;
	}

	public static String getVideoViewResolution(VideoView videoView){
		int width = videoView.getWidth();
		int height = videoView.getHeight();
		String resolution=width+"x"+height;
		return resolution;
	}

    public static String removeSysExtraInfo(String sysName){
        String res = sysName.split(" | ")[0];
        return res;
    }

    public static String timeSinceLastMessage(long t1, long t2){
        String string="Time since last message:\n";
        long hours=0;
        long minutes=0;
        long seconds = 0;
        long millisec = t1-t2;
        if (millisec>1000){
            seconds = millisec/1000;
            if (seconds>60){
                minutes = seconds/60;
                if (minutes>60){
                    hours = minutes/60;
                    minutes = minutes%60;
                }
                seconds = seconds%60;
            }
            millisec = millisec%1000;
        }
        if (hours>0){
            string += hours+"h ";
        }
        if (minutes>0){
            string += minutes+"m ";
        }
        if (seconds>0){
            string += seconds+"s ";
        }
        if (millisec>0){
            string += millisec+"ms";
        }

        return string;
    }

    public static boolean validateSetting(String setting){
        String parts[] = setting.split(",");
        if (parts.length<5)
            return false;
        String type = parts[0];
        switch (type){
            case "java.lang.String":
                break;
            case "java.lang.Integer":
                try{
                    int x = Integer.parseInt(parts[4]);
                    break;
                }catch (NumberFormatException e){
                    return false;
                }
            case "java.lang.Boolean":
                break;
            case "java.util.LinkedHashSet":
                break;
            default:
                return false;
        }

        return true;
    }

}
