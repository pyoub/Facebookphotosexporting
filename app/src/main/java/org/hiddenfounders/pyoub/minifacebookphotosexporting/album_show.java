package org.hiddenfounders.pyoub.minifacebookphotosexporting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;

public class album_show extends AppCompatActivity {
GridView gridView =null;
    ProgressBar pro ;
    GridViewAdapter ima;
    ArrayList<ImageItem> image = new ArrayList<>();
    ImageView imageView;
    ArrayList<String> id=new ArrayList<>() ;
    ArrayList<String> title=new ArrayList<>() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_album_show);
        Intent intent = getIntent();
        pro = (ProgressBar)findViewById(R.id.progressBar);
        imageView = new ImageView(this);
        gridView = (GridView)findViewById(R.id.gv);
        setTitle("Your Albums");
        AccessToken.setCurrentAccessToken((AccessToken)intent.getExtras().get("tok"));
        Log.d("iduser", "Facebook Albums: " + AccessToken.getCurrentAccessToken().getUserId());

/*make API call*/
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),  //your fb AccessToken
                "/" + AccessToken.getCurrentAccessToken().getUserId() + "/albums",//user id of login user
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(final GraphResponse response) {
                        if (response.getError() == null) {
                            JSONObject joMain = response.getJSONObject(); //convert GraphResponse response to JSONObject
                            if (joMain.has("data")) {
                                JSONArray jaData = joMain.optJSONArray("data"); //find JSONArray from JSONObject
                                for (int i = 0; i < jaData.length(); i++) {//find no. of album using jaData.length()

                                    try {
                                        id.add(new String(jaData.getJSONObject(i).getString("id")));
                                        title.add(new String(jaData.getJSONObject(i).getString("name")));

                                        Log.d("cone", "onCompleted: "+jaData.getJSONObject(i).getString("name"));

                                        final int finalI = i;
                                        new GraphRequest(
                                                AccessToken.getCurrentAccessToken(),
                                                "/"+jaData.getJSONObject(i).getString("id")+"/picture",
                                                null,
                                                HttpMethod.GET,
                                                new GraphRequest.Callback() {
                                                    public void onCompleted(GraphResponse response) {
                /* handle the result */
                                                        Log.d("title", "onCompleted: " + title);

                                                        new DownloadImage().execute(response.getConnection().getURL().toString(),title.get(finalI),id.get(finalI));
                                                    }
                                                }
                                        ).executeAsync();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),response.getError().getErrorMessage().toString(),Toast.LENGTH_LONG).show();
                            Log.d("erreur", "onCompleted: "+response.getError().getErrorMessage().toString());
                            pro.setVisibility(View.INVISIBLE);
                            LinearLayout layout = new LinearLayout(getApplicationContext());
                            layout.setGravity(Gravity.CENTER);
                            Button button = new Button(getApplicationContext());
                            button.setText("Refresh");
                            TextView t =new TextView(getApplicationContext());
                            t.setText("error with connection");
                            layout.addView(t);
                            layout.addView(button);
                            album_show.this.setContentView(layout);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    album_show.this.finish();
                                }
                            });
                        }
                    }

                }

        ).executeAsync();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        this.finish();
        return true;
    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap>{
            String title;
            String id;
        protected Bitmap doInBackground(String... urls){
            String urldisplay = urls[0];
            title=new String(urls[1]);
            id = new String(urls[2]);
            Bitmap mIcon11 = null;
            try{
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            }catch (Exception e){
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result){
            image.add(new ImageItem(result,title,id));
            image.sort(new Comparator<ImageItem>() {
                @Override
                public int compare(ImageItem o1, ImageItem o2) {
                    return o1.getTitle().compareTo(o2.getTitle());
                }
            });
            ima =new GridViewAdapter(album_show.this,R.layout.image,image);
            pro.setVisibility(View.INVISIBLE);
            gridView.setAdapter(ima);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    Intent inte = new Intent(album_show.this,gridView.getClass());

                    bundle.putString("id", image.get(position).getId());
                    bundle.putParcelable("access",AccessToken.getCurrentAccessToken());
                    Fragment fragmentManager = new grid_photo();
                    fragmentManager.setArguments(bundle);

                    getSupportFragmentManager().beginTransaction().replace(R.id.fram,fragmentManager).commit();

                }
            });
        }

    }

}
