package com.example.aakash.fireblog;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class BlogSingleActivity extends AppCompatActivity {

    //private String mPost_key;
    private DatabaseReference mDatabase;



    private String post_image;
    private String post_desc;
    private String post_title;
    private String post_uid;
    private TextView mPostSingleTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single);

        View includedLayout = findViewById(R.id.content_desc);
        final TextView mPostSingleDesc = (TextView) includedLayout.findViewById(R.id.blog_desc_single);

        mPostSingleTitle=(TextView)findViewById(R.id.blog_single_title);
       // mPostSingleDesc=(TextView)findViewById(R.id.blog_desc_single);

         String mPost_key=getIntent().getExtras().getString("blog_id");


        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 post_title=(String) dataSnapshot.child("title").getValue();
                post_desc=(String) dataSnapshot.child("desc00").getValue();
                 post_image=(String) dataSnapshot.child("image").getValue();

                //Toast.makeText(BlogSingleActivity.this,post_desc + post_image,Toast.LENGTH_LONG).show();

                post_uid=(String) dataSnapshot.child("uid").getValue();



                mPostSingleDesc.setText(post_desc);


                try {
                    Picasso.with(getApplicationContext())
                            .load(post_image.toString())
                            .into((ImageView) findViewById(R.id.blog_single_image));
                    //Glide.with(this).load(post_image).into((ImageView) findViewById(R.id.blog_single_image));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mPostSingleTitle.setText(post_title);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
  //      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);




        //initCollapsingToolbar();

    }
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =(CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    //collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    //collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

}
