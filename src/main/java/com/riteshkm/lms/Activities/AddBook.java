package com.riteshkm.lms.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.riteshkm.lms.Database.BookData;
import com.riteshkm.lms.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by ROHIT on 9/11/2017.
 */

public class AddBook extends AppCompatActivity {

    BookData myDb;

    EditText name,author,isbn = null;
    Button save,cover;

    ImageView imageView;

    final static int REQUEST_CODE = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        myDb = new BookData(this);

        Toolbar addToolbar = (Toolbar)findViewById(R.id.add_toolbar);
        setSupportActionBar(addToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (EditText)findViewById(R.id.book_name);
        author = (EditText)findViewById(R.id.author_name);
        isbn = (EditText)findViewById(R.id.isbn_number);

        imageView = (ImageView)findViewById(R.id.imageView_add);

        save = (Button)findViewById(R.id.save_book);
        cover = (Button)findViewById(R.id.book_cover);

        /** Button to Set the cover **/
        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**Asking the permission **/
                ActivityCompat.requestPermissions(
                        AddBook.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE
                );

            }
        });

        /**This save all the data entered in the form **/
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Name = name.getText().toString();
                String Author = author.getText().toString();
                String Isbn = isbn.getText().toString();

                /*Toast.makeText(AddBook.this,"Name " + Name + " Author " + Author + " Isbn " + Isbn,Toast.LENGTH_LONG).show();*/

                /** Partial form validation,user cannot save the book without filling all the details **/
                if ( Name.equals("") || Author.equals("") || Isbn.equals("") ){
                    /*Toast.makeText(AddBook.this,"Enter all the information",Toast.LENGTH_LONG).show();*/
                    showMessage("Cannot Enter the Book","You need to enter all the details of the book");
                }else {
                    myDb.insertData(Name,
                            Author,
                            Isbn);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddBook.this, "Book Added", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AddBook.this,ShowBooks.class));
                            finish();
                        }
                    }, 800);

                }

                /*myDb.insertData(name.getText().toString(),
                        author.getText().toString(),
                        isbn.getText().toString(),imageViewToByte(imageView));*/


               /*imageView.setImageResource(R.mipmap.ic_launcher);*/
            }
        });

    }

    /**Alert in case of something gets wrong **/
    private void showMessage(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.show();
    }


   /** Function to create Image into Bytes **/
    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    /**For user permission to access storage **/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_CODE);
            }
            else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /** on result after the selection of photo **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK ){
            /*Uri uri = data.getData();*/

            try {
               /* InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);*/

                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);

            } catch (FileNotFoundException e) {

                Toast.makeText(AddBook.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }else {
            Toast.makeText(AddBook.this,"You haven't pick the image",Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
