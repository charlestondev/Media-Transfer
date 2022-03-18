package com.hits.mediatransfer;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by charleston on 17/09/17.
 */

public class JobSchedulerService extends JobService {
    private Iterator<String> iterator;
    public boolean sending = false;
    public String url ="http://192.168.5.254:1100/upload";
    //public static final String CAMERA_IMAGE_BUCKET_NAME = Environment.getExternalStorageDirectory().toString()+ "/DCIM/Camera";
    public static final String CAMERA_IMAGE_BUCKET_NAME = "/storage/1E8E-B121/DCIM/Camera";
    public static final String CAMERA_IMAGE_BUCKET_ID = getBucketId(CAMERA_IMAGE_BUCKET_NAME);

    @Override
    public boolean onStartJob(JobParameters params) {
        Toast.makeText(this, "Job started", Toast.LENGTH_LONG).show();
        List<String> photos = getCameraImages(getApplicationContext());
        iterator = photos.iterator();
        /*for (int i = 0; i < 50; i++) {
            iterator.next();
        }*/
        /*int count = 0;
        while(iterator.hasNext()){
            count++;

            Log.d("photoName", iterator.next());
        }
        Log.d("count", count+"");*/
        String lastPhotoSent = getLastSent();
        String currentPhoto = "";
        //Log.d("lastPhoto", lastPhotoSent);
        if(iterator.hasNext())
            currentPhoto = iterator.next();

        //se há fotos
        if(!currentPhoto.equals("")){
            //se nunca foi enviada uma foto
            if(lastPhotoSent.equals("")){
                Log.d("debug", "never was sent");
                sendPhoto(currentPhoto, params);
                return true;
            }

            while(iterator.hasNext() && Long.parseLong(getPhotoId(currentPhoto))<=Long.parseLong(lastPhotoSent)){
                currentPhoto = iterator.next();
            }

            if(Long.parseLong(getPhotoId(currentPhoto))>Long.parseLong(lastPhotoSent)){
                Log.d("debug", "Continuing where it left off");
                sendPhoto(currentPhoto, params);
                return true;
            }
            else if(Long.parseLong(getPhotoId(currentPhoto))!=Long.parseLong(lastPhotoSent)){
                Log.d("debug", "from the start again");
                iterator = photos.iterator();
                sendPhoto(iterator.next(), params);
                return true;
            }
        }
        Log.d("debug", "all done!");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Toast.makeText(this, "Job stopped", Toast.LENGTH_LONG).show();
        Log.d("job", "stopped");
        return false;
    }

    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }
    public static List<String> getCameraImages(Context context) {
        final String[] projection = { MediaStore.Images.Media.DATA };
        final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = { CAMERA_IMAGE_BUCKET_ID };
        final String orderBy = MediaStore.Images.Media.DATA+" ASC";
        final Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                orderBy);
        ArrayList<String> result = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            do {
                final String data = cursor.getString(dataColumn);
                result.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }
    public void sendPhoto(String photoName,final JobParameters jobParameters){

        final String photo = photoName;
        File imgFile = new File(photo);
        RequestParams params = new RequestParams();
        try {
            FileInputStream is = new FileInputStream(imgFile);
            String field = getMD5sum(is);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");
            String extension = imgFile.getName().substring(imgFile.getName().lastIndexOf('.'));
            params.put(sdf.format(imgFile.lastModified())+"_"+field+extension, imgFile);
            //params.put("texto", "oi mundo!");
        } catch(FileNotFoundException e) {

        } catch (IOException e){}

        AsyncHttpClient client = new AsyncHttpClient();

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                if(bytesWritten == totalSize)
                    sending = false;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("debug", "enviado "+getPhotoId(photo));
                saveLastSent(getPhotoId(photo));
                if(iterator.hasNext())
                    sendPhoto(iterator.next(), jobParameters);
                else{
                    jobFinished(jobParameters, false);
                    Log.d("debug", "pronto!");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("Falhou", "falhou");
            }

        });
    }
    public String getPhotoId(String path){
        String photoId = path.substring(path.indexOf("_")+1, path.lastIndexOf("."));
        return (photoId.substring(0, photoId.indexOf("_")) + photoId.substring(photoId.indexOf("_")+1)).replaceAll("\\D+", "");
    }
    public String getLastSent(){
        //SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString("last_sent", "");
    }
    public void saveLastSent(String lastSent){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("last_sent", lastSent);
        editor.commit();
    }


    private static char[] hexDigits = "0123456789abcdef".toCharArray();
    public static String getMD5sum(InputStream is) throws IOException {
        String md5 = "";

        try {
            byte[] bytes = new byte[4096];
            int read = 0;
            MessageDigest digest = MessageDigest.getInstance("MD5");

            while ((read = is.read(bytes)) != -1) {
                digest.update(bytes, 0, read);
            }

            byte[] messageDigest = digest.digest();

            StringBuilder sb = new StringBuilder(32);

            for (byte b : messageDigest) {
                sb.append(hexDigits[(b >> 4) & 0x0f]);
                sb.append(hexDigits[b & 0x0f]);
            }

            md5 = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return md5;
    }
}
