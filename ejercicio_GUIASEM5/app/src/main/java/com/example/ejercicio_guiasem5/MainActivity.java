package com.example.ejercicio_guiasem5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_IMAGEN_CAPTURA = 1;
    private static final int REQUEST_PERMISSION_CAMERA = 100;
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 200;
    private ImageView imageView;
    private Button btnTomarFoto;
    private ImageButton botonEmail;
    private ImageButton botonWhatsapp;

    private Uri imagenUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        botonEmail = findViewById(R.id.bemail);
        botonWhatsapp = findViewById(R.id.bWhatsapp);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);

        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tomarFoto();
            }
        });
        botonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imagenUri != null) {
                    EnviarEmail();
                } else {
                    Toast.makeText(MainActivity.this, "Debe tomar una foto primero", Toast.LENGTH_SHORT).show();
                }
            }
        });
        botonWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imagenUri != null) {
                    EnviarWhatsapp();
                } else {
                    Toast.makeText(MainActivity.this, "Debe tomar una foto primero", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void EnviarWhatsapp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.setPackage("com.whatsapp");
        intent.putExtra(Intent.EXTRA_STREAM, imagenUri);
        startActivity(intent);
    }

    private void EnviarEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"correo@ejemplo.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Asunto del correo");
        intent.putExtra(Intent.EXTRA_TEXT, "Mensaje del correo");
        intent.putExtra(Intent.EXTRA_STREAM, imagenUri);
        startActivity(intent);
    }

    private void tomarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 1);
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGEN_CAPTURA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

            // Guarda la imagen en la galería
            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, "title", "description"));
            imagenUri = uri;
        }
    }

    private void requestPermissionCamera() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
    }

    private boolean checkPermissionCamera() {
        return ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                tomarFoto();
            } else {
                Toast.makeText(this, "No tiene permiso para usar la cámara", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "No tiene permiso para guardar la imagen en la galería", Toast.LENGTH_SHORT).show();
            }
        }
    }
}