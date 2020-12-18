package com.example.ianalizer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class MenuActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    private final static int SELECT_PHOTO = 12345;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    Button reconocerImagen, reconocerTexto, reconocerRostro;
    ImageButton camara, galeria;
    TextView instruccion;
    Intent intent;
    private ImageView imagen;
    private FirebaseStorage storage;
    private StorageReference mStorageReference;
    public Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
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
                if (imagen != null) {
                    intent = new Intent(MenuActivity.this, ImageRecognizeActivity.class);
                    startActivity(intent);
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
                if (imagen != null) {
                    intent = new Intent(MenuActivity.this, FaceRecognizeActivity.class);
                    startActivity(intent);
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
                if (imagen != null) {
                    intent = new Intent(MenuActivity.this, TextRecognizeActivity.class);
                    startActivity(intent);
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
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
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
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == CAMERA_REQUEST ) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                photoURI = data.getData();
                imagen.setImageBitmap(photo);
                imagen.setImageURI(photoURI);
                uploadPicture();

            }
            if (requestCode == SELECT_PHOTO) {
                Uri pickedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                photoURI = pickedImage;
                imagen.setClipToOutline(true);
                imagen.setImageURI(photoURI);
                uploadPicture();
            }
            imagen.setVisibility(View.VISIBLE);
            instruccion.setVisibility(View.GONE);
        }
    }

    private void uploadPicture() {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Cargando Imagen...");
        pd.show();
        final String randomName = UUID.randomUUID().toString();
        StorageReference riversRef = mStorageReference.child("images/" + randomName);
        riversRef.putFile(photoURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Imagen cargada", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(),  "Error al cargar imagen", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double porcentaje =  (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Porcentaje: " + (int) porcentaje + "%");
                    }
                });
    }

}