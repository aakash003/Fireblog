package com.example.aakash.fireblog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MainActivity extends ActionBarActivity {

    private RecyclerView mBlogList;
    private DatabaseReference mDatabaseRefer;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseLikes;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListen;

    private boolean likes=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth=FirebaseAuth.getInstance();
        mAuthStateListen=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                        Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }
            }
        };


        setContentView(R.layout.activity_main);


        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);


        TextView appTitle=(TextView)findViewById(R.id.toolbar_title);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/x.ttf");
        appTitle.setTypeface(custom_font);


        mDatabaseRefer= FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseRefer.keepSynced(true);
        mDatabaseUsers=FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        mDatabaseLikes=FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseLikes.keepSynced(true);

        mBlogList=(RecyclerView)findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        checkUserExist();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthStateListen);

        FirebaseRecyclerAdapter<Blog,BlogViewHolder> mAdapter=new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(Blog.class,
                        R.layout.blog_row,
                        BlogViewHolder.class,
                        mDatabaseRefer){

            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {

                final String post_key=getRef(position).getKey();


                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setUsername(model.getUsername());

                //viewHolder.setLikeBtn(post_key);
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent singleBlogIntent=new Intent(MainActivity.this,BlogSingleActivity.class);
                        singleBlogIntent.putExtra("blog_id",post_key);
                        startActivity(singleBlogIntent);


                    }
                });

                viewHolder.mLikeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        likes=true;

                            mDatabaseLikes.addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(likes){
                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                        mDatabaseLikes.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        likes = false;
                                    } else {
                                        mDatabaseLikes.child(post_key).child(mAuth.getCurrentUser().getUid()).
                                                setValue("Random");
                                        likes = false;

                                    }
                                }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                    }
                });
            }

        };
        mBlogList.setAdapter(mAdapter);

    }


    private void checkUserExist() {
        if(mAuth.getCurrentUser()!=null) {
            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user_id)) {
                        Intent setUpIntent = new Intent(MainActivity.this, SetUpActivity.class);
                        setUpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setUpIntent);


                    } else {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View mView;

        ImageButton mLikeButton;

        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuthLike;


        public BlogViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            mLikeButton=(ImageButton) mView.findViewById(R.id.bLike);
            mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuthLike=FirebaseAuth.getInstance();
            mDatabaseLike.keepSynced(true);

        }
        /*public void setLikeBtn(final String post_key)
        {
            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(post_key).hasChild(mAuthLike.getCurrentUser().getUid()))
                    {
                        mLikeButton.setImageResource(R.mipmap.thumpsp_colores);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }*/
        public void setTitle(String title)
        {
            TextView post_title=(TextView)mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public void setDesc(String desc){
            TextView post_desc=(TextView)mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }
        public void setUsername(String username)
        {
            TextView post_username=(TextView)mView.findViewById(R.id.post_username);
            post_username.setText(username);
        }

        public void setImage(final Context context, final String image)
        {
            final ImageView postImage=(ImageView)mView.findViewById(R.id.post_image);
            ///Picasso.with(context).load(image).into(postImage);
            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(postImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(image).into(postImage);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_add)
        {
            startActivity(new Intent(MainActivity.this,PostActivity.class));
        }
        if(item.getItemId()==R.id.action_logout)
        {
            logOut();
        }
        if(item.getItemId()==R.id.action_settings)
        {
            startActivity(new Intent(MainActivity.this,SetUpActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        mAuth.signOut();
    }
}
