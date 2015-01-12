package pt.lsts.asa.util;

import pt.lsts.asa.settings.Settings;

import java.util.Locale;

import android.widget.VideoView;

/**
 * Created by jloureiro on 18-12-2014.
 */
public class StringUtils {

	public static String getCamUrl(VideoView videoView){
		String cam = Settings.getString("cam_model","axis");
		cam = cam.toLowerCase(Locale.UK);
		switch (cam){
			case "axis":
				return getAxisUrl(videoView);
			case "aircam":
				return getAirCamUrl(videoView);
			default:
				return "ERROR";
		}

	}

	public static String getAirCamUrl(VideoView videoView){
		//rtsp://<CAM_IP>:554/live/ch00_0 - Full resolution - 1280 (w) x 720 (h)
		String protocol = Settings.getString("cam_protocol", "rtsp");
		String ip_port= Settings.getString("cam_ip_port", "10.0.20.102:554");
		String location = "live";

		String resolution = Settings.getString("cam_resolution", "0x0");
		resolution = resolution.toLowerCase(Locale.UK);
		switch (resolution){
			case "160x96":
				resolution="ch03_0";
				break;
			case "320x176":
				resolution="ch02_0";
				break;
			case "640x368":
				resolution="ch01_0";
				break;
			case "1280x720":
			default:
				resolution="ch00_0";
		}

		//resolution = validateResolution(resolution, videoView);

		String completeUrl=protocol+"://"+ip_port+"/"+location+"/"+resolution;
		return completeUrl;
	}

	public static String getAxisUrl(VideoView videoView){
		//rtsp://IPADDRESS/axis-media/media.amp?videocodec=h264&resolution=640x480
		String protocol = Settings.getString("cam_protocol", "rtsp");
		String ip_port= Settings.getString("cam_ip", "10.0.20.199");
		String location = "axis-media/media.amp";
		String codec = Settings.getString("cam_codec","h264");
		String resolution = Settings.getString("cam_resolution", "0x0");

		resolution = validateResolution(resolution, videoView);

		String completeUrl=protocol+"://"+ip_port+"/"+location+"?videocodec="+codec+"&resolution="+resolution;
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

}
