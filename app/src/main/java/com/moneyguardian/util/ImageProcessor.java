package com.moneyguardian.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.moneyguardian.R;

import java.io.ByteArrayOutputStream;

public class ImageProcessor {

    public static UploadTask processImage(Bitmap image, StorageReference imageRef, Context context) {
        return processImage(image, imageRef, context, null);
    }

    public static UploadTask processImage(Bitmap image, StorageReference imageRef, Context context, String onErrorString) {

        //we store the image into the store and link it to the user entity
        //in the database for it to be accessed
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(bytes);
        Bitmap finalSelectedImageBitmap = image;
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,
                                onErrorString,
                                Toast.LENGTH_LONG)
                        .show();
            }
        });
        return uploadTask;
    }
}

