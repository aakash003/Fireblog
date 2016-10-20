package com.example.aakash.fireblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetUpActivity extends AppCompatActivity {

    private ImageButton displayPic;
    private EditText displayName;
    private Button set;

    private static int GALLERY_REQUEST=1;

    private Uri imageUri=null;

    private DatabaseReference mDatabaseUser;
    private FirebaseAuth mAuth;
    private StorageReference mStorageImage;

    private ProgressDialog mProgress;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
        displayName=(EditText)findViewById(R.id.eDisplayName);
        displayPic=(ImageButton)findViewById(R.id.iBdisplayPic);
        set=(Button)findViewById(R.id.bSet);

        mProgress=new ProgressDialog(this);
        mDatabaseUser= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        mStorageImage= FirebaseStorage.getInstance().getReference().child("ProfileImage");


        displayPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startSetUpAccount();

            }
        });
    }

    private void startSetUpAccount() {
        mProgress.setMessage("Applying Changes..");
        mProgress.show();

        final String name=displayName.getText().toString();
        final String user_id=mAuth.getCurrentUser().getUid();

        if(!TextUtils.isEmpty(name)&&imageUri!=null)
        {
            StorageReference filepath=mStorageImage.child(imageUri.getLastPathSegment());
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String download=taskSnapshot.getDownloadUrl().toString();
                    mDatabaseUser.child(user_id).child("name").setValue(name);
                    mDatabaseUser.child(user_id).child("image").setValue(download);

                    mProgress.dismiss();
                    Intent mainIntent = new Intent(SetUpActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
            });


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST&&resultCode==RESULT_OK)
        {
            imageUri=data.getData();
            CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1)
                .start(this);



        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                displayPic.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

