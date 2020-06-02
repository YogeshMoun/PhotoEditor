package com.example.photoeditor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Image extends AppCompatActivity implements View.OnClickListener {

    Button save,blur,lighten,darken,embross,sharpen,original,edge,cancel;
    Button delete,filter,rotate;
    ImageView imageview;
    Bitmap original_data=null,original_image=null;
    OutputStream outputstream;
    //HorizontalScrollView horizontalScrollView;
    final int PIC_CROP = 2;
    final int crop_result=400;
    String pathofImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);


        save=(Button)findViewById(R.id.save_image);
        blur=(Button)findViewById(R.id.blur);
        lighten=(Button)findViewById(R.id.lighter);
        darken=(Button)findViewById(R.id.darker);
        embross=(Button)findViewById(R.id.emboss);
        sharpen=(Button)findViewById(R.id.sharp);
        original=(Button)findViewById(R.id.original);
        imageview=(ImageView)findViewById(R.id.imageview);
        edge=(Button)findViewById(R.id.edge);
        cancel=(Button)findViewById(R.id.cancel_image);
        //horizontalScrollView=(HorizontalScrollView)findViewById(R.id.hori);

        delete=(Button)findViewById(R.id.delete);
        filter=(Button)findViewById(R.id.filter);
        rotate=(Button)findViewById(R.id.rotate);

        cancel.setOnClickListener(this);
        edge.setOnClickListener(this);
        save.setOnClickListener(this);
        blur.setOnClickListener(this);
        lighten.setOnClickListener(this);
        darken.setOnClickListener(this);
        embross.setOnClickListener(this);
        sharpen.setOnClickListener(this);
        original.setOnClickListener(this);
        delete.setOnClickListener(this);
        filter.setOnClickListener(this);
        rotate.setOnClickListener(this);

        Intent intent = getIntent();
        if(getIntent().hasExtra("img"))//(camera)Returns true if an extra
            // value is associated with the given name
        {
            Bitmap bitmap = (Bitmap) intent.getParcelableExtra("img");
            String camera1=getIntent().getExtras().getString("imagepath");
            pathofImage=camera1;
            original_data=bitmap;
            original_image=bitmap;
            imageview.setImageBitmap(bitmap);
        }
        if(getIntent().hasExtra("select"))//select from gallery
        {
            String path=getIntent().getExtras().getString("select");
            Uri f=Uri.parse(path);
            pathofImage=path;
            try {
                Bitmap bm=BitmapFactory.decodeStream(getContentResolver().openInputStream(f));
                original_data=bm;
                original_image=bm;
                imageview.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(getIntent().hasExtra("gallery"))//select from app's gallery
        {
            String path=getIntent().getExtras().getString("gallery");
            File file=new File(path);
            pathofImage=path;
            Bitmap bt=BitmapFactory.decodeFile(file.getAbsolutePath());
            original_data=bt;
            original_image=bt;
            imageview.setImageBitmap(bt);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.save_image:
                    save();
                break;
            case R.id.original:
                original();
                cancel.setVisibility(View.INVISIBLE);
                break;
            case R.id.blur:
                    findblur();
                    cancel.setVisibility(View.VISIBLE);
                break;
            case R.id.lighter:
                cancel.setVisibility(View.VISIBLE);
                break;
            case R.id.darker:
                cancel.setVisibility(View.VISIBLE);
                break;
            case R.id.emboss:
                cancel.setVisibility(View.VISIBLE);
                break;
            case R.id.sharp:
                    sharpness();
                cancel.setVisibility(View.VISIBLE);
                break;
            case R.id.edge:
                findEdge();
                cancel.setVisibility(View.VISIBLE);
                break;
            case R.id.cancel_image:
                cancelOperation();
                cancel.setVisibility(View.INVISIBLE);
                break;
            case R.id.delete:
                    deleteImage();
                //cancel.setVisibility(View.VISIBLE);
                break;
            case R.id.filter:
                    findFilter();
                //cancel.setVisibility(View.VISIBLE);
                break;
            case R.id.rotate:
                    rotateImage();
                break;

        }

    }

    private void deleteImage() {
        File file=new File(pathofImage);
        if(file.exists())
        {
            if(file.delete())
            {
                Toast.makeText(this,"file Deleted :" + pathofImage,Toast.LENGTH_LONG).show();
                Intent intent=new Intent(Image.this,gallery.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this,"file not Deleted :" + pathofImage,Toast.LENGTH_LONG).show();
            }
        }
        else
            Toast.makeText(this,"Path not Existed!" + pathofImage,Toast.LENGTH_LONG).show();
    }


    private void rotateImage() {
        float degree=90;
        cancel.setVisibility(View.VISIBLE);
        Bitmap bt=original_image;
        Matrix matrix=new Matrix();
        //setup rotate degree
        matrix.postRotate(degree);
        //create new bitmap after rotate
        Bitmap bitmap=Bitmap.createBitmap(bt,0,0,bt.getWidth(),bt.getHeight(),matrix,true);
        original_image=bitmap;
        imageview.setImageBitmap(original_image);
    }


    private void findFilter() {
        blur.setVisibility(View.VISIBLE);
        lighten.setVisibility(View.VISIBLE);
        darken.setVisibility(View.VISIBLE);
        embross.setVisibility(View.VISIBLE);
        sharpen.setVisibility(View.VISIBLE);
        original.setVisibility(View.VISIBLE);
        edge.setVisibility(View.VISIBLE);

    }

    private void cancelOperation() {
        imageview.setImageBitmap(original_data);
    }

    private void findblur() {
        Bitmap bt=original_data;
        int width=bt.getWidth();
        int height=bt.getHeight();
        Bitmap result=Bitmap.createBitmap(width,height,bt.getConfig());
        double[][] sharp=new double[][]{
                {1,2,1},{2,4,2},{1,2,1}
        };

        int[][] pixels=new int[3][3];
        int a,r,g,b;
        int sumA,sumR,sumB,sumG;
        double offset=0;
        double factor=16;
        for(int x=0;x<height-2;x++)
        {
            for(int y=0;y<width-2;y++)
            {
                //get pixels
                for(int i=0;i<3;++i)
                {
                    for(int j=0;j<3;++j)
                    {
                        pixels[i][j]=bt.getPixel(y+i,x+j);
                    }
                }

                //sum of color's
                sumA=sumB=sumG=sumR=0;
                //get alpha of center pixel---->each pixel contains color information (such as values describing intensity of red,
                // green, and blue) and also contains a value for its opacity known as its 'alpha' value. An alpha value of
                // 1 means totally opaque, and an alpha value of 0 means totally transparent
                a= Color.alpha(pixels[1][1]);

                //get sum of RGB
                for(int i=0;i<3;i++)
                {
                    for(int j=0;j<3;j++)
                    {
                        sumR+=(Color.red(pixels[i][j])*sharp[i][j]);
                        sumG+=(Color.green(pixels[i][j])*sharp[i][j]);
                        sumB+=(Color.blue(pixels[i][j])*sharp[i][j]);
                    }
                }

                //final color
                r=(int)(sumA/(offset+factor) +Color.red(pixels[1][1]));
                if(r<0)r=0;
                else if(r>255)r=255;

                g=(int)(sumG/(offset+factor)  +Color.green(pixels[1][1]));
                if(g<0)g=0;
                else if(g>255)g=255;

                b=(int)(sumB/(offset+factor)  +Color.blue(pixels[1][1]));
                if(b<0)b=0;
                else if(b>255)b=255;

                //final result after sharp
                result.setPixel(y+1,x+1,Color.argb(a,r,g,b));
            }
        }
        imageview.setImageBitmap(result);
    }

    private void findEdge() {

        Bitmap bt=original_data;
        int width=bt.getWidth();
        int height=bt.getHeight();
        Bitmap result=Bitmap.createBitmap(width,height,bt.getConfig());
        double[][] sharp=new double[][]{
                {0,-2,0},{-2,8,-2},{0,-2,0}
        };
        int[][] pixels=new int[3][3];
        int a,r,g,b;
        int sumA,sumR,sumB,sumG;
        double offset=1;
        double factor=4;
        for(int x=0;x<height-2;x++)
        {
            for(int y=0;y<width-2;y++)
            {
                //get pixels
                for(int i=0;i<3;++i)
                {
                    for(int j=0;j<3;++j)
                    {
                        pixels[i][j]=bt.getPixel(y+i,x+j);
                    }
                }

                //sum of color's
                sumA=sumB=sumG=sumR=0;
                //get alpha of center pixel---->each pixel contains color information (such as values describing intensity of red,
                // green, and blue) and also contains a value for its opacity known as its 'alpha' value. An alpha value of
                // 1 means totally opaque, and an alpha value of 0 means totally transparent
                a= Color.alpha(pixels[1][1]);

                //get sum of RGB
                for(int i=0;i<3;i++)
                {
                    for(int j=0;j<3;j++)
                    {
                        sumR+=(Color.red(pixels[i][j])*sharp[i][j]);
                        sumG+=(Color.green(pixels[i][j])*sharp[i][j]);
                        sumB+=(Color.blue(pixels[i][j])*sharp[i][j]);
                    }
                }

                //final color
                r=(int)(sumA/(offset*factor));
                if(r<0)r=0;
                else if(r>255)r=255;

                g=(int)(sumG/factor*offset);
                if(g<0)g=0;
                else if(g>255)g=255;

                b=(int)(sumB/offset*factor);
                if(b<0)b=0;
                else if(b>255)b=255;

                //final result after sharp
                result.setPixel(y+1,x+1,Color.argb(a,r,g,b));
            }
        }
        imageview.setImageBitmap(result);

    }

    private void sharpness()
    {
        Bitmap bt=original_data;
        int width=bt.getWidth();
        int height=bt.getHeight();
        Bitmap result=Bitmap.createBitmap(width,height,bt.getConfig());
        double[][] sharp=new double[][]{
                {-1,-1,-1},{-1,8,-1},{-1,-1,-1}
        };
        int[][] pixels=new int[3][3];
        int a,r,g,b;
        int sumA,sumR,sumB,sumG;
        double offset=1;
        double factor=1;
        for(int x=0;x<height-2;x++)
        {
            for(int y=0;y<width-2;y++)
            {
                //get pixels
                for(int i=0;i<3;i++)
                {
                    for(int j=0;j<3;j++)
                    {
                        pixels[i][j]=bt.getPixel(y+i,x+j);
                    }
                }

                //sum of color's
                sumA=sumB=sumG=sumR=0;
                //get alpha of center pixel---->each pixel contains color information (such as values describing intensity of red,
                // green, and blue) and also contains a value for its opacity known as its 'alpha' value. An alpha value of
                // 1 means totally opaque, and an alpha value of 0 means totally transparent
                a= Color.alpha(pixels[1][1]);

                //get sum of RGB
                for(int i=0;i<3;i++)
                {
                    for(int j=0;j<3;j++)
                    {
                        sumR+=(Color.red(pixels[i][j])*sharp[i][j]);
                        sumG+=(Color.green(pixels[i][j])*sharp[i][j]);
                        sumB+=(Color.blue(pixels[i][j])*sharp[i][j]);
                    }
                }

                //final color
                r=(int)(sumA/(offset*factor) +Color.red(pixels[1][1]));
                if(r<0)r=0;
                else if(r>255)r=255;

                g=(int)(sumG/factor*offset  +Color.green(pixels[1][1]));
                if(g<0)g=0;
                else if(g>255)g=255;

                b=(int)(sumB/offset*factor  +Color.blue(pixels[1][1]));
                if(b<0)b=0;
                else if(b>255)b=255;

                //final result after sharp
                result.setPixel(y+1,x+1,Color.argb(a,r,g,b));
            }
        }
        imageview.setImageBitmap(result);
    }

    private void original()
    {
        imageview.setImageBitmap(original_data);
    }

    private void save()
    {
        BitmapDrawable drawable=(BitmapDrawable)imageview.getDrawable();// it allows you to
        // fetch a drawable object associated with a particular resource ID for the given screen density/theme
        Bitmap bitmap=drawable.getBitmap();
        File filepath= Environment.getExternalStorageDirectory();
        File dir=new File(filepath.getAbsolutePath()+"/Photo Editor/");
        dir.mkdir();
        File file =new File(dir,System.currentTimeMillis()+".jpg");
        try {
            outputstream=new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputstream);
        Toast.makeText(getApplicationContext(),"Saved Scussesfully",Toast.LENGTH_LONG).show();
        try {
            outputstream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
