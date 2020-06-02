package com.example.photoeditor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class gallery extends AppCompatActivity {

    GridView gridview;
    ArrayList<File> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        gridview=(GridView)findViewById(R.id.gridview);

        list= imageReader(Environment.getExternalStorageDirectory());
        gridview.setAdapter(new gridAdpter());

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent=new Intent(gallery.this,Image.class);
                intent.putExtra("gallery",list.get(i).toString());
                startActivity(intent);
            }
        });
    }

    private ArrayList<File> imageReader(File gridAdpter) {
        ArrayList<File> b=new ArrayList<>();
        File f=new File(gridAdpter+"/Photo Editor/");
        File[] file=f.listFiles();
        for(int i=0;i<file.length;i++)
        {
            if(file[i].isDirectory())
            {
                b.addAll(imageReader(file[i]));
            }
            else
            {
                if(file[i].getName().endsWith(".jpg"))
                {
                    b.add(file[i]);
                }
            }
        }
        return b;
    }

    public class gridAdpter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {

            View view=null;
            if(view==null)
            {
                view=getLayoutInflater().inflate(R.layout.row_layout,parent,false);
                ImageView imageview=(ImageView)view.findViewById(R.id.imageview_grid);
                imageview.setImageURI(Uri.parse(list.get(i).toString()));
            }
            return view;
        }
    }
}
