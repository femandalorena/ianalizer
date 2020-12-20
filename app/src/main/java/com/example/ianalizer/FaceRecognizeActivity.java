package com.example.ianalizer;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileOutputStream;

public class FaceRecognizeActivity extends AppCompatActivity {
    Bitmap bmp, carasReconocidas;
    ImageView imagen;
    int contador;
    Button guardarRostro, copiarRegistroRostro;
    TextView descripcion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorAccentLight));
        setContentView(R.layout.activity_reconocer_rostros);
        guardarRostro = (Button) findViewById(R.id.guardarRostros);
        copiarRegistroRostro = (Button) findViewById(R.id.copiarRostros);
        descripcion = (TextView) findViewById(R.id.descripcionRostros);
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        this.imagen = (ImageView) this.findViewById(R.id.caraReconocida);
        imagen.setImageBitmap(bmp);
        contador = 0;
        final Paint boxPaint = new Paint();
        carasReconocidas = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas (carasReconocidas);
        canvas.drawBitmap(bmp, 0,0,null);
        boxPaint.setStrokeWidth(8);
        boxPaint.setColor(Color.GREEN);
        boxPaint.setStyle(Paint.Style.STROKE);
        procesar( canvas, boxPaint);
        descripcion.setText(cantidad());

        guardarRostro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    saveToGallery(carasReconocidas, "naaaaa");

            }
        })
        ;
        copiarRegistroRostro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Registro de rostros ", descripcion.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "copiado", Toast.LENGTH_SHORT).show();
            }
        })
        ;
    }

    public void procesar(Canvas canvas, Paint boxPaint){
        FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        if (!faceDetector.isOperational()){
            Toast.makeText(getApplicationContext(), "Algo salió mal", Toast.LENGTH_SHORT).show();
            return;
        }
        Frame frame = new Frame.Builder().setBitmap(bmp).build();
        SparseArray<Face> sparseArray = faceDetector.detect(frame);
        for (int i = 0 ; i < sparseArray.size(); i++){
            Face face = sparseArray.valueAt(i);
            float x1= face.getPosition().x;
            float y1= face.getPosition().y;
            float x2= x1 + face.getWidth();
            float y2= y1 + face.getHeight();
            RectF rectF = new RectF(x1,y1,x2,y2);
            canvas.drawRoundRect(rectF,2,2, boxPaint);
            contador=contador + 1;
        }
        imagen.setImageBitmap(carasReconocidas);
    }

    public String cantidad(){
        String texto;
        if (contador == 0){
            texto= "Parece que no hay personas en esta imagen.";
        } else if (contador == 1) {
            texto="Se reconoció una persona";
        }else {
            texto="Se reconocieron " + Integer.toString(contador) + " personas.";
        }
        return texto;
    }

    private void saveToGallery(Bitmap bitmatFile, String image_name) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + image_name+ ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmatFile.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent anotherIntent = new Intent(FaceRecognizeActivity.this, MenuActivity.class);
        startActivity(anotherIntent);
    }
}
