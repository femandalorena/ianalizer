package com.example.ianalizer;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.List;

public class TextRecognizeActivity extends AppCompatActivity {
    Bitmap bmp;
    ImageView imagen;
    Button copiar, compartir;
    TextView descripcion, contador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorAccentLight));
        setContentView(R.layout.activity_reconocer_texto);
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        this.imagen = (ImageView) this.findViewById(R.id.textoEscaneado);
        imagen.setImageBitmap(bmp);
        detect(findViewById(android.R.id.content).getRootView());
        copiar = (Button) findViewById(R.id.copiarTexto);
        compartir = (Button) findViewById(R.id.compartirTexto);
        descripcion = (TextView) findViewById(R.id.descripcionTexto);
        contador = (TextView) findViewById(R.id.contadorTexto);
        copiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (! descripcion.getText().toString().equals("")) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Texto reconocido", descripcion.getText().toString());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getApplicationContext(), "copiado", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(getApplicationContext(), "No hubo texto detectado", Toast.LENGTH_SHORT).show();
                }
            }
        })
        ;
        compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (! descripcion.getText().toString().equals("")) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,descripcion.getText());
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Texto reconocido");
                startActivity(Intent.createChooser(shareIntent, "Compartir"));
                } else{
                    Toast.makeText(getApplicationContext(), "No hubo texto detectado", Toast.LENGTH_SHORT).show();
                }
            }
        })
        ;
    }

    public void detect (View v){
        if (bmp == null ){
            Toast.makeText(getApplicationContext(), "algo salió mal", Toast.LENGTH_SHORT).show();
        } else{
            FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bmp);
            FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            firebaseVisionTextRecognizer.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    process_text(firebaseVisionText);

                }
            });

        }
    }
    public void process_text (FirebaseVisionText firebaseVisionText){
        List<FirebaseVisionText.TextBlock> blocks = firebaseVisionText.getTextBlocks();
if (blocks.size() == 0){
    contador.setText("Sin detecciones");
} else {
    for (FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks()){
        String text = block.getText();
        descripcion.setText(text);
        contador ();
    }
}
    }

    public void contador(){
        String primeraParte, segundaParte;
        int caracteres = descripcion.getText().length() ;
        int lineas = descripcion.getLineCount();
            if (caracteres == 1) {
                primeraParte = "Un caracter detectado";
            } else {
                primeraParte = Integer.toString(caracteres) + " caracteres detectados";
            }
            if (lineas == 1) {
                segundaParte = "una línea.";
            } else {
                segundaParte = Integer.toString(lineas) + " líneas.";
            }
            contador.setText(primeraParte + " en "+  segundaParte);

    }
    @Override
    public void onBackPressed() {
        Intent anotherIntent = new Intent(TextRecognizeActivity.this, MenuActivity.class);
        startActivity(anotherIntent);
    }
}
