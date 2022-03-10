package com.illumo.imageredactor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton backBtn;
    private Button pixBtn;
    private Button negativeBtn;
    private ImageView image;
    private SeekBar pixBar;
    private TextView textCount;

    private int RESULT_LOAD_IMG = 1;
    private int pixelCount = 5;
    private int SCALE_FACTOR = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backBtn = findViewById(R.id.backBtn);
        pixBtn = findViewById(R.id.pixBtn);
        image = findViewById(R.id.imageView);
        pixBar = findViewById(R.id.pixBar);
        negativeBtn = findViewById(R.id.negativeBtn);
        textCount = findViewById(R.id.pixCount);


        if(getIntent().hasExtra("byteArray")) {
            ImageView previewThumbnail = new ImageView(this);
            Bitmap b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);
            previewThumbnail.setImageBitmap(b);
            image.setImageBitmap(b);
        }


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackBtn();
            }
        });

        pixBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pixelate(((BitmapDrawable)image.getDrawable()).getBitmap().getWidth()/pixelCount ,image);
            }
        });

        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Negative(image);
            }
        });

        pixBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int progress = seekBar.getProgress();
                pixelCount = progress + 5;
                textCount.setText(String.valueOf(pixelCount));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                pixelCount = progress + 5;
                textCount.setText(String.valueOf(pixelCount));
            }
        });


    }

    void BackBtn()
    {
        Intent intent = new Intent(this, BrowseActivity.class);
        startActivity(intent);
    }

    void Negative(ImageView image)
    {
        int R, G, B, A;

        int sumR = 0, sumG = 0, sumB = 0, sum = 0;

        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888 , true);
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();

        try {
            for (int i = 0; i < imageWidth; i++)
            {
                for (int j = 0; j < imageHeight; j++)
                {

                    Color color = new Color();

                    int pixel = bitmap.getPixel(i,j);

                    A = Color.alpha(pixel);

                    R = 255 - Color.red(pixel);
                    G = 255 - Color.green(pixel);
                    B = 255 - Color.blue(pixel);

                    bitmap.setPixel(i, j, Color.argb(A, R, G, B));

                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this,"Error", Toast.LENGTH_LONG).show();
        }

        image.setImageBitmap(bitmap);
    }

    void Pixelate(int pixSize, ImageView image)
    {
        //pixSize = pixSize * SCALE_FACTOR;

        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888 , true);

        int newWidth = (bitmap.getWidth() / pixSize) * pixSize;
        int newHeight = (bitmap.getHeight() / pixSize) * pixSize;

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                bitmap, newWidth, newHeight, false);

        bitmap = resizedBitmap;

        try {
            for (int i = 0; i < bitmap.getHeight(); i+=pixSize)
            {
                for (int j = 0; j < bitmap.getWidth(); j+=pixSize)
                {
                    for(int pixX = j; pixX < j + pixSize; pixX++)
                    {
                        for(int pixY = i; pixY < i + pixSize; pixY++)
                        {
                            bitmap.setPixel(pixX, pixY, MediumColor(pixSize, bitmap, j, i));
                        }
                    }

                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this,"Error", Toast.LENGTH_LONG).show();
        }

        image.setImageBitmap(bitmap);
    }

    int MediumColor(int pixSize, Bitmap bitmap, int j, int i)
    {
        int R=0, G=0, B=0, A=0;
        int sumR = 0, sumG = 0, sumB = 0, sum = 0;
        int pixelColor;
        int pixel = bitmap.getPixel(j,i);

        for(int pixX = j; pixX < j + pixSize; pixX++)
        {
            for(int pixY = i; pixY < i + pixSize; pixY++)
            {
                A = Color.alpha(pixel);

                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                sumR += R;
                sumG += G;
                sumB += B;

                sum++;
            }
        }

        pixelColor = Color.argb(A, sumR/sum, sumG/sum, sumB/sum);

        sumR = 0;
        sumG = 0;
        sumB = 0;
        sum = 0;

        return pixelColor;
    }

}