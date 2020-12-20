package com.example.ianalizer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MenuActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private final static int SELECT_PHOTO = 12345;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    Button reconocerImagen, reconocerTexto, reconocerRostro;
    ImageButton camara, galeria;
    TextView instruccion;
    Bitmap bitmap;
    Intent intent;
    boolean continuar = false;
    private ImageView imagen;
    public Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorAccentLight));
        setContentView(R.layout.activity_menu_principal);

        this.imagen = (ImageView) this.findViewById(R.id.uploadedImage);
        camara = (ImageButton) findViewById(R.id.camaraButton);
        galeria = (ImageButton) findViewById(R.id.galeriaButton);
        reconocerImagen = (Button) findViewById(R.id.imagenes);
        reconocerTexto = (Button) findViewById(R.id.texto);
        reconocerRostro= (Button) findViewById(R.id.rostros);
        instruccion= (TextView) findViewById(R.id.instruction);
        reconocerImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (continuar) {
                    ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
                    byte[] byteArray = bStream.toByteArray();
                    Intent anotherIntent = new Intent(MenuActivity.this, ImageRecognizeActivity.class);
                    anotherIntent.putExtra("image", byteArray);
                    startActivity(anotherIntent);
                    finish();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Necesita subir una imagen", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        })
        ;
        reconocerRostro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (continuar) {
                    ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
                    byte[] byteArray = bStream.toByteArray();

                    Intent anotherIntent = new Intent(MenuActivity.this, FaceRecognizeActivity.class);
                    anotherIntent.putExtra("image", byteArray);
                    startActivity(anotherIntent);
                    finish();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Necesita subir una imagen", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        })
        ;
        reconocerTexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (continuar) {
                    ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
                    byte[] byteArray = bStream.toByteArray();
                    Intent anotherIntent = new Intent(MenuActivity.this, TextRecognizeActivity.class);
                    anotherIntent.putExtra("image", byteArray);
                    startActivity(anotherIntent);
                    finish();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Necesita subir una imagen", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        })
        ;

        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

        camara.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de acceso", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMAGE_CAPTURE ) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imagen.setImageBitmap(imageBitmap);
            }
            if (requestCode == SELECT_PHOTO) {
                Uri pickedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),pickedImage);
                }catch(IOException e ){
                    e.printStackTrace();
                }
                String[] filePath = {MediaStore.Images.Media.DATA};
                photoURI = pickedImage;
                imagen.setClipToOutline(true);
                imagen.setImageURI(photoURI);
            }
            imagen.setVisibility(View.VISIBLE);
            instruccion.setVisibility(View.GONE);
            continuar = true;
        }
    }

        private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

}