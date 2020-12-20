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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import java.util.List;

public class ImageRecognizeActivity extends AppCompatActivity {
    Bitmap bmp;
    ImageView imagen;
    boolean espanol = false;
    Button traducirSalida, copiarImagen;
    TextView descripcion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorAccentLight));
        setContentView(R.layout.activity_reconocer_imagen);
        traducirSalida = (Button) findViewById(R.id.traducirImagen);
        copiarImagen = (Button) findViewById(R.id.copiarImagen);
        descripcion = (TextView) findViewById(R.id.descripcionImagen);
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        this.imagen = (ImageView) this.findViewById(R.id.recognizedImage);
        imagen.setImageBitmap(bmp);
        identificar(espanol);

        traducirSalida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                espanol = !espanol;
            identificar(espanol);
            }
        })
        ;
        copiarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Registro de objetos ", descripcion.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "copiado", Toast.LENGTH_SHORT).show();
            }
        })
        ;
    }

    public void identificar (boolean espanol) {
        InputImage image = InputImage.fromBitmap(bmp, 0);
        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        labeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        descripcion.setText("");
                        for (ImageLabel label : labels) {
                            String text = label.getText();
                            float confidence = label.getConfidence() * 100;
                            String conf2 = String.format("%.02f", confidence);
                            if (espanol){
                                translateText(text, conf2);
                            } else {
                                descripcion.append(text + ": " +conf2 + "% accuracity. \n" );
                            }

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Algo salió mal", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void translateText(String text, String conf2) {
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(FirebaseTranslateLanguage.EN)
                .setTargetLanguage(FirebaseTranslateLanguage.ES)
                .build();
        final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance()
                .getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                translator.translate(text).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        descripcion.append(s + ": " +conf2 + "% de certeza. \n" );
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent anotherIntent = new Intent(ImageRecognizeActivity.this, MenuActivity.class);
        startActivity(anotherIntent);
    }
}
