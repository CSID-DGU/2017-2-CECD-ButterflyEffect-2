package csid.butterflyeffect;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import csid.butterflyeffect.game.model.Famer;

/**
 * Created by hanseungbeom on 2018. 4. 22..
 */

public class FirebaseTasks {
    private static StorageReference mReference;
    private static FirebaseDatabase mDatabase;

    private FirebaseTasks() {
    }

    public static StorageReference getStorageInstance() {
        if (mReference == null)
            mReference = FirebaseStorage.getInstance().getReference();
        return mReference;
    }

    public static FirebaseDatabase getDatabaseInstance() {
        if (mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance();
        return mDatabase;
    }

    public static void registerFamer(final Context context, final String phoneNumber, final int score, Bitmap bitmap) {

        //1.photo Upload

        final StorageReference storageRef = getStorageInstance().child("famer/" + phoneNumber + ".png");

        // Get the data from an ImageView as bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("#####",phoneNumber+"image upload completed!");
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                Famer famer = new Famer();
                famer.setPhoneNumber(phoneNumber);
                famer.setImageUrl(downloadUrl.toString());
                famer.setUpdatedTime(System.currentTimeMillis());
                famer.setScore(score);

                DatabaseReference databaseRef = getDatabaseInstance().getReference(context.getString(R.string.table_famer));
                databaseRef.setValue(famer);
            }
        });


        //2.database upload


    }

}
