package com.example.ianalizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.R;

public class ImageRecognizeActivity extends AppCompatActivity {
    Bitmap bmp;
    ImageView imagen;
    Button guardarImagen, compartirImagen;
    TextView descripcion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorAccentLight));
        setContentView(R.layout.activity_reconocer_imagen);
        guardarImagen = (Button) findViewById(R.id.guardarImagen);
        compartirImagen = (Button) findViewById(R.id.compartirImagen);
        descripcion = (TextView) findViewById(R.id.descripcionImagen);
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        this.imagen = (ImageView) this.findViewById(R.id.recognizedImage);
        imagen.setImageBitmap(bmp);


        //procesar( canvas, boxPaint);
        //descripcion.setText(cantidad());

        guardarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //TODO
            }
        })
        ;
        compartirImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //TODO
            }
        })
        ;
    }





    @Override
    public void onBackPressed() {
        Intent anotherIntent = new Intent(ImageRecognizeActivity.this, MenuActivity.class);
        startActivity(anotherIntent);
    }
}
