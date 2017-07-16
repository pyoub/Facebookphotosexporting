package org.hiddenfounders.pyoub.minifacebookphotosexporting;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;


public class grid_photo extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    ArrayList<image_adapter> a = new ArrayList<>();
    ArrayList<String> idi = new ArrayList<>();
    String id = new String();
    image_adapter_grid gridViewAdapter ;
    Bundle mBundle ;
    ProgressDialog barProgressDialog ;
    GridView gridView;
    Button button;
    View view = null;
    ProgressBar progressBar;
    ArrayList<String>title = new ArrayList<>();
    StorageReference storageRef ;
    FirebaseStorage storage ;
    Handler updateBarHandler;
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_grid_photo, container, false);
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        FirebaseApp.initializeApp(getActivity());

        barProgressDialog = new ProgressDialog(getActivity());
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar2);
        mBundle = getArguments();
        id=mBundle.getString("id");
        updateBarHandler = new Handler();
        button = (Button)view.findViewById(R.id.button2);
        gridView = (GridView)view.findViewById(R.id.gridv);
        getActivity().setTitle("choose the images");

        AccessToken.setCurrentAccessToken((AccessToken) mBundle.get("access"));
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+id+"/photos",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        if (response.getError() == null) {
                            JSONObject joMain = response.getJSONObject(); //convert GraphResponse response to JSONObject
                            if (joMain.has("data")) {
                                JSONArray jaData = joMain.optJSONArray("data"); //find JSONArray from JSONObject
                                Log.d( "al: " ,jaData.toString()+"");
                                for (int i = 0; i < jaData.length(); i++) {//find no. of album using jaData.length()

                                    try {
                                        idi.add(jaData.getJSONObject(i).getString("id"));
                                        final int finalI = i;
                                        new GraphRequest(
                                                AccessToken.getCurrentAccessToken(),
                                                "/"+jaData.getJSONObject(i).getString("id")+"/picture",
                                                null,
                                                HttpMethod.GET,
                                                new GraphRequest.Callback() {
                                                    public void onCompleted(GraphResponse response) {
                /* handle the result */
                //download image
                                                        Log.d("title", "onCompleted: " + title);
                                                        new DownloadImage().execute(response.getConnection().getURL().toString(),idi.get(finalI));

                                                    }
                                                }
                                        ).executeAsync();
            /* handle the result */
                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }}}else{
                           getActivity().finish();
                        }}}
        ).executeAsync();


        return view;

    }



    class DownloadImage extends AsyncTask<String,Void,Bitmap> {

        String id = new String();

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];

            id = new String(urls[1]);
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                Log.e("Error", mIcon11.getByteCount() + "");
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            a.add(new image_adapter(result, new CheckBox(getActivity()),id));
            Log.d("id", "onPostExecute:     " + a.size());

            gridViewAdapter = new image_adapter_grid(getActivity(), R.layout.image_adapter, a);
            progressBar.setVisibility(View.INVISIBLE);
            gridView.setAdapter(gridViewAdapter);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Handler handle = new Handler() {
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            barProgressDialog.incrementProgressBy(1); // Incremented By Value 2

                            if (barProgressDialog.getProgress() == barProgressDialog.getMax()) {
                                barProgressDialog.dismiss();
                            }
                        }
                    };
                    FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                            final int[] i = {0};
                            int size = 0;
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Log.i("permession", "onAuthStateChanged: " + user.getDisplayName() + "  perme" + user.getProviders().toString());

                            if (user != null) {
                                for (image_adapter image : a
                                        ) {
                                    if (image.getcheck() == true)
                                        size++;
                                }


                                barProgressDialog.setTitle("Upload Image ...");
                                barProgressDialog.setMessage("Upload in progress ...");
                                barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
                                barProgressDialog.setProgress(0);
                                barProgressDialog.setMax(size++);
                                barProgressDialog.show();
                                if(size!=0){
                                for (final image_adapter image : a
                                        ) {
                                    Log.d("image", "image: " + i[0]);
                                    if (image.getcheck() == true) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    while (barProgressDialog.getProgress() <= barProgressDialog.getMax()) {
                                                        StorageReference mountainsRef = storageRef.child("folder/" + image.getId() + ".jpg");

                                                        Bitmap bitmap = image.getBitmap();
                                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                                        byte[] data = baos.toByteArray();
                                                        UploadTask uploadTask = mountainsRef.putBytes(data);
                                                        uploadTask.addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception exception) {
                                                                // Handle unsuccessful uploads
                                                                new Runnable(){

                                                                    @Override
                                                                    public void run() {
                                                                        i[0] =0;
                                                                        Toast.makeText(getActivity(),"erreur",Toast.LENGTH_LONG);
                                                                    }
                                                                };

                                                            }
                                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                                                handle.sendMessage(handle.obtainMessage());
                                                                i[0]++;
                                                            }
                                                        });
                                                        Log.d("dialog", "run: "+barProgressDialog.getProgress());

                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();

                                    }
                                }
                                barProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                                getActivity());

                                        // set title
                                        if(i[0]!=0) {
                                            alertDialogBuilder.setTitle("Your images are uploads");
                                        }
                                        else alertDialogBuilder.setTitle("Your uploads is not complete");
                                        // set dialog message
                                        alertDialogBuilder
                                                .setMessage("do you want continue??")
                                                .setCancelable(false)
                                                .setPositiveButton("Continue",new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int id) {
                                                        getActivity().finish();
                                                    }
                                                })
                                                .setNegativeButton("Exit",new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int id) {
                                                        getActivity().moveTaskToBack(true);
                                                        getActivity().finish();
                                                        dialog.cancel();
                                                    }
                                                });

                                        AlertDialog alertDialog = alertDialogBuilder.create();

                                        // show it
                                        alertDialog.show();
                                    }
                                });

                                }





                                // User is signed in
                                Log.d("usein", "onAuthStateChanged:signed_in:" + user.getUid());
                            } else {
                                // User is signed out
                                Log.d("userout", "onAuthStateChanged:signed_out");
                            }
                            // ...
                        }
                    });
                }
            });

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    a.get(position).setcheck(!a.get(position).checkBox.isChecked());
                    Log.d("id", "oncheck:     " + a.get(position).checkBox.isChecked());
                    gridViewAdapter.notifyDataSetChanged();
                }
            });
        }
    }
    }
