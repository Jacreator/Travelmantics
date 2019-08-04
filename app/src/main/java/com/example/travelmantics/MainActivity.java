package com.example.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {


    private static final int PICTURE_REQUEST_CODE = 42;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    EditText txtTitle;
    EditText txtDesc;
    EditText txtPrice;
    TravelDeal deal;
    Button btnImage;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase Instance
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;

//        Connection to database
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

//        finding and setting the text in activity
        txtTitle = findViewById(R.id.txtTitle);
        txtDesc = findViewById(R.id.txtDescription);
        txtPrice = findViewById(R.id.txtPrice);
        imageView = findViewById(R.id.image);


//        getting the intent
        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (deal == null) {
            deal = new TravelDeal();
        }
        this.deal = deal;
        txtTitle.setText(deal.getTitle());
        txtDesc.setText(deal.getDescription());
        txtPrice.setText(deal.getPrice());
        showImage(deal.getImageUrl());
        btnImage = findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                this intent allow the use to select a data and return it
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                this set the type of the data to Image/jpg
                intent.setType("image/*");
//                this is to accept data only on the device
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Insert Picture"), PICTURE_REQUEST_CODE);

            }
        });
    }

    //    Overrided onOptionsItemSelected
    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show();
                clean();
                backToList();
                return true;
            case R.id.delete_menu:
                deleteDeal();
                Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show();
                backToList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //    Cleaning the data Set after each use
    private void clean() {
//        setting all input fields to empty strings
        txtPrice.setText("");
        txtDesc.setText("");
        txtTitle.setText("");
        txtTitle.requestFocus();
    }

    //    Saving deals to database
    private void saveDeal() {

//        Getting the Input from the App
        deal.setTitle(txtTitle.getText().toString());
        deal.setPrice(txtPrice.getText().toString());
        deal.setDescription(txtDesc.getText().toString());

//        using the Constructor of TravelDeal to pass details to the database
        if (deal.getId() == null) {
//        sending the details to Firebase
            mDatabaseReference.push().setValue(deal);
        } else {
            mDatabaseReference.child(deal.getId()).setValue(deal);
        }

    }

    //    Deleting a deal from database
    private void deleteDeal() {
        if (deal == null) {
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_SHORT).show();

            if (deal.getImageName() != null && !deal.getImageName().isEmpty() ) {
                StorageReference ref = FirebaseUtil.mstorageReference.child(deal.getImageName());
                ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Delete image", "Image deleted!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Delete image", e.getMessage());
                    }
                });
            }
            return;
        }

        mDatabaseReference.child(deal.getId()).removeValue();
    }

    //    this method take us back to the listActivity
    private void backToList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    //    this makes the non admin user not write deals
    private void enableEditTexts(boolean isEnabled) {
        txtPrice.setEnabled(isEnabled);
        txtDesc.setEnabled(isEnabled);
        txtTitle.setEnabled(isEnabled);
    }

    private void showImage(String url) {
        if (url != null && !url.isEmpty()) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.with(this).load(url).resize(width, width * 2 / 3).centerCrop().into(imageView);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            StorageReference ref = FirebaseUtil.mstorageReference.child(imageUri.getLastPathSegment());
            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUrl;
                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                    while(!uri.isComplete());
                    Uri url = uri.getResult();

                    Log.d("Upload URL", "Upload Success, download URL ");
                    downloadUrl = url.toString();
                    deal.setImageUrl(downloadUrl);
                    showImage(downloadUrl);

                    String pictureName = taskSnapshot.getStorage().getPath();
                    deal.setImageName(pictureName);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.save_menu).setVisible(true);
            menu.findItem(R.id.delete_menu).setVisible(true);
            enableEditTexts(true);
            findViewById(R.id.btnImage).setEnabled(true);
        } else {
            menu.findItem(R.id.save_menu).setVisible(false);
            menu.findItem(R.id.delete_menu).setVisible(false);
            enableEditTexts(false);
            findViewById(R.id.btnImage).setEnabled(false);
        }
        return true;
    }


}

