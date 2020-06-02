package com.example.photoeditor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

public class MainPage extends AppCompatActivity implements View.OnClickListener {

    Button camera,select,gallery;
    public static final int pic_id=1234;
    public static final int IMG_REQUEST=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        camera=(Button)findViewById(R.id.camera_button);
        select=(Button)findViewById(R.id.select_button);
        gallery=(Button)findViewById(R.id.gallery_button);

        camera.setOnClickListener(this);
        select.setOnClickListener(this);
        gallery.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.camera_button:
                    displayCamera();
                break;
            case R.id.select_button:
                select();
                break;
            case R.id.gallery_button:
                    gallery();
                break;
        }
    }

    private void gallery()
    {
        Intent intent=new Intent(MainPage.this,gallery.class);
        startActivity(intent);
    }

    private void select()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG_REQUEST);
    }

    private void displayCamera()
    {
        // Create the camera_intent ACTION_IMAGE_CAPTURE
        // it will open the camera for capture the image
        Intent camera_intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Start the activity with camera_intent,
        // and request pic id

        startActivityForResult(camera_intent,pic_id);
    }
    protected void onActivityResult(int requestcode,int resultcode,Intent data) // This method will help
    // to retrieve the image
    {
        super.onActivityResult(requestcode, resultcode, data);
        if(requestcode==IMG_REQUEST&&resultcode==RESULT_OK&&data!=null&& data.getData()!=null)
        {
            Uri path=data.getData();
            Intent intent=new Intent(MainPage.this,Image.class);
            intent.putExtra("select",path.toString());
            startActivity(intent);


        }
        // Match the request 'pic id with requestCode
        if(requestcode==pic_id)
        {
            // BitMap is data structure of image file
            // which stor the image in memory
            Bitmap bitmap=(Bitmap)data.getExtras().get("data");
            Intent intent=new Intent(MainPage.this,Image.class);
            /*create instance of File with name img.jpg*/
            Uri path=data.getData();
            /*put uri as extra in intent object*/
            //intent.putExtra("imagepath", path.toString());
            intent.putExtra("img",bitmap);
            startActivity(intent);
        }

    }
}
