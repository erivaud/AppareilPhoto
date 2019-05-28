package com.example.appareilphoto;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int RETOUR_PRENDRE_PHOTO = 1;


    private Button btnPrendrePhoto;
    private Button btnEnreg;
    private ImageView imgAffichePhoto;
    private String photoPath = null;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initActivity();

        ActivityCompat.requestPermissions(
                MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

    }

    /**
     * initialisation de l'activité
     */
    private void initActivity() {

        btnPrendrePhoto = (Button)findViewById(R.id.btnPrendrePhoto);
        imgAffichePhoto = (ImageView)findViewById(R.id.imgAffichePhoto);
        btnEnreg = (Button)findViewById(R.id.btnEnreg);

        createClickBtnPrendrePhoto();
        createOnClickBtnEnreg();
    }

    /**
     * évènement click sur bouton btnEnreg
     */
    private void createOnClickBtnEnreg(){
        btnEnreg.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enregistrer la photo
                MediaStore.Images.Media.insertImage(getContentResolver(),
                        image,
                        "nom_image", "description");
            }
        });

    }

    /**
     * évènement click sur bouton btnPrendrePhoto
     */
    private void createClickBtnPrendrePhoto() {
        btnPrendrePhoto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                prendreUnePhoto();
            }
        });

    }

    /**
     * accès à l'appareil photo et mémoriser dans un fichier temporaire
     */
    private void prendreUnePhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // s'il y a un appareil photo sur le téléphone
        if(intent.resolveActivity(getPackageManager()) != null) {
            //créer un nouveau nom de fichier
            String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            // Créer le fichier
            File photoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File photoFile = File.createTempFile("photo"+time, ".jpg", photoDir);
                // enregistrer le chemin complet
                photoPath = photoFile.getAbsolutePath();
                //créer l'uri
                Uri photoUri = FileProvider.getUriForFile(
                        MainActivity.this,
                        MainActivity.this.getApplicationContext().getPackageName()+".provider",
                        photoFile);
                //transfert uri vers intent pour enregistrement photo dans fichier temporaire
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                // ouvrir l'activity par rapport à l'intent
                startActivityForResult(intent, RETOUR_PRENDRE_PHOTO);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * retour de l'appel de l'appareil photo (startActivityForResult) - ici on n'a que l'appareil photo,
     * mais sinon ça renvoie toute activité
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // vérifie que le bon code retour et l'état du retour OK
        if(requestCode == RETOUR_PRENDRE_PHOTO && resultCode == RESULT_OK){
            image = BitmapFactory.decodeFile(photoPath);
            // afficher image
            imgAffichePhoto.setImageBitmap(image);
        }

    }

}
