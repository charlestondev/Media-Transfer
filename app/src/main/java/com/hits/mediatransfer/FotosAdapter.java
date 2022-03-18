package com.hits.mediatransfer;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class FotosAdapter extends RecyclerView.Adapter<FotosAdapter.ViewHolder> {
    private List<Foto> mDataset;
    private RecyclerView.Adapter mAdapter;
    private DisplayMetrics displayMetrics;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public FrameLayout mItem;
        public ViewHolder(FrameLayout v) {
            super(v);
            mItem = v;
        }
    }

    public FotosAdapter(List<Foto> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public FotosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        displayMetrics = parent.getResources().getDisplayMetrics();
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.produto, parent, false);
        // set the view's size, margins, paddings and layout parameters
        context = parent.getContext();
        ViewHolder vh = new ViewHolder((FrameLayout)v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ViewHolder tmpHolder = holder;
        //todo change findViewById to viewHolder
        ((TextView)holder.mItem.findViewById(R.id.nome_produto)).setText(mDataset.get(position).nome);
        holder.mItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File imgFile = new File(mDataset.get(position).nome);


                String url ="http://192.168.5.254:1100/upload";
                // gather your request parameters

                RequestParams params = new RequestParams();
                try {
                    params.put("profile_picture", imgFile);
                } catch(FileNotFoundException e) {}

                AsyncHttpClient client = new AsyncHttpClient();
                client.post(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onProgress(long bytesWritten, long totalSize) {
                        super.onProgress(bytesWritten, totalSize);
                        Log.d("sended:", bytesWritten+" total"+ totalSize);
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("debug", "enviado");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }

                });
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

}