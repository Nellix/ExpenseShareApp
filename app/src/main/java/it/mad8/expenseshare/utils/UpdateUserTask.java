package it.mad8.expenseshare.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import it.mad8.expenseshare.R;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.model.datamapper.UserDataMapper;

/**
 * Created by Aniello Malinconico on 08/05/2017.
 */

public class UpdateUserTask extends AsyncTask<String, Void, Bitmap> {

    private Context context;


    public UpdateUserTask(Context c) {
        this.context=c;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        try {
            updateUser();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }



    private void updateUser() throws IOException {
        Bitmap loadBitmap;
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final UserModel user;
        if (firebaseUser.getPhotoUrl() != null) {

            loadBitmap = BitmapFactory.decodeStream((InputStream) new URL(firebaseUser.getPhotoUrl().toString()).getContent());
            user = new UserModel(firebaseUser.getDisplayName(), firebaseUser.getEmail());


        } else {
            loadBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_person_black_24dp);
            user = new UserModel(firebaseUser.getDisplayName(), firebaseUser.getEmail());

        }

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(UserDataMapper.USERS);

  /*
        //prepare a user id


        //first upload image then create a groupe
        StorageReference groupImageRef = FirebaseStorage.getInstance().getReference(UserDataMapper.USER_IMAGE_PATH).child(firebaseUser.getUid());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        loadBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask =  groupImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context, R.string.user_add_unsuccess, Toast.LENGTH_SHORT).show();
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL

                Toast.makeText(context, R.string.user_add_success, Toast.LENGTH_SHORT).show();

            }
        });
            */

        databaseReference.child(firebaseUser.getUid()).setValue(new UserDataMapper(user));


    }

}
