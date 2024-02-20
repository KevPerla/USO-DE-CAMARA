package com.example.ejemplo_guiasem5;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button btnTomarFoto;
    ImageView imgFoto;
    String rutaImagenes;
    private static final int REQUEST_CODIGO_CAMARA = 200;
    private static final int REQUEST_CODIGO_CAPTURAR_IMAGEN = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgFoto = findViewById(R.id.imgFoto);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);

        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realizarProcesoFotografia();
                tomarFoto();
            }
        });
    }

    private void realizarProcesoFotografia() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (ActivityCompat.checkSelfPermission(
                    MainActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                tomarFoto();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CODIGO_CAMARA);
            }
        } else {
            tomarFoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODIGO_CAMARA) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                tomarFoto();
            } else {
                Toast.makeText(MainActivity.this, "Se requiere permisos para la camara", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void tomarFoto() {
        Intent intentCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // if (intentCamara.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intentCamara, 1);
            File archivoFoto = null;
            archivoFoto = crearArchivo();
            if (archivoFoto != null) {
                Uri rutaFoto = FileProvider.getUriForFile(MainActivity.this, "com.example.ejemplo_guiasem5", archivoFoto);
                intentCamara.putExtra(MediaStore.EXTRA_OUTPUT, rutaFoto);
                startActivityForResult(intentCamara, REQUEST_CODIGO_CAPTURAR_IMAGEN);
            }
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        {
            if (requestCode == REQUEST_CODIGO_CAPTURAR_IMAGEN) {
                if (resultCode == Activity.RESULT_OK) {
                    imgFoto.setImageURI(Uri.parse(rutaImagenes));
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private File crearArchivo() {
        String nomenclatura = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String prefijoArchivo = "APPCAM_" + nomenclatura + "_";
        File directorioImagen = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File miImagen = null;
        try {
            miImagen = File.createTempFile(prefijoArchivo, ".jpg", directorioImagen);
            rutaImagenes = miImagen.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return miImagen;
    }
}