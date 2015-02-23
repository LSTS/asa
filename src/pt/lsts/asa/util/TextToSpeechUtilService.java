package pt.lsts.asa.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.io.File;

/**
 * Created by jloureiro on 2/23/15.
 */
public class TextToSpeechUtilService extends Service {

    public static final String TAG = "TextToSpeechUtilService";
    private TextToSpeech tts;


    public TextToSpeechUtilService(TextToSpeech tts){
        this.tts=tts;
    }


    /**
     *
     * @param intent
     * @param flags 0: getEnginesAvailable; 1:DownloadAudioFiles; 2:associateAudioFiles; 3:downloadAndAssociate
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.i(TAG,"onStartCommand()");
        switch (flags){
            case 0:
                for (TextToSpeech.EngineInfo engine: tts.getEngines()){
                    //do something with this
                }
                break;
            case 1:
                downloadAudioFiles();
                break;
            case 2:
                associateAudioFiles();
                break;
            case 3:
                downloadAudioFiles();
                associateAudioFiles();
                break;
            default:
                break;
        }

        return START_NOT_STICKY;//does not resuscitate
    }

    public void associateAudioFiles(){
        String audioFilesMainPath = FileOperations.mainDirString+"audio-numbers/";
        String extension = ".wav";
        for (int i=0;i<=100;i++){
            Log.i(TAG,"tts.addEarcon(\"\"+"+i+","+audioFilesMainPath+i+extension+");");
            tts.addSpeech("" + i, audioFilesMainPath + i + extension);
        }
    }

    public void downloadAudioFiles(){
        String mainUrl= "https://evolution.voxeo.com/library/audio/prompts/numbers/";
        String extension = ".wav";
        String destinationPath = FileOperations.mainDirString+"audio-numbers/";
        File dir = new File(destinationPath);
        dir.mkdirs();//initialize audio-numbers folder if it does not exist

        for (int i=0;i<=100;i++){
            String urlString = mainUrl+i+extension;
            Log.i(TAG,"downloading file: "+urlString);
            String filename =i+extension;
            FileOperations.downloadFile(urlString, destinationPath, filename);
        }

    }

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate()");
    }


    @Override
    public void onDestroy() {
        Log.v(TAG,"onDestroy()");
        stopSelf();
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
