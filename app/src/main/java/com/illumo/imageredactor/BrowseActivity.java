package com.illumo.imageredactor;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class BrowseActivity extends AppCompatActivity {

    private static final int SCALE_FACTOR = 10;
    private Button browseBtn;
    private ImageView image = null;
    private Drawable oldDrawable;



    private int RESULT_LOAD_IMG = 1;
    private Boolean RESULT_HAVE_IMG = false;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                int newWidth = (selectedImage.getWidth()/SCALE_FACTOR);
                int newHeight = (selectedImage.getHeight()/SCALE_FACTOR);

                Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                        selectedImage, newWidth, newHeight, false);

                image.setImageBitmap(resizedBitmap);
                RESULT_HAVE_IMG = true;
                oldDrawable = image.getDrawable();



            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }

        ImageSender();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        browseBtn = findViewById(R.id.browseBtn);
        image = findViewById(R.id.imageView);

        browseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });
    }

    void ImageSender()
    {
        try {
            Intent imageRedactor = new Intent(this, MainActivity.class);
            if(image.getDrawable() != null) {
                Bitmap b = ((BitmapDrawable) image.getDrawable()).getBitmap(); // ваша картинка
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.PNG, 50, bs);
                imageRedactor.putExtra("byteArray", bs.toByteArray());
            }

            //Toast.makeText(this, "Byte Array Create",Toast.LENGTH_LONG).show();

            startActivity(imageRedactor);
        } catch (Exception e)
        {
            Toast.makeText(this, "Error to switch activity",Toast.LENGTH_LONG).show();
        }

    }
}
