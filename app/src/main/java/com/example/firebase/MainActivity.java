package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.firebase.model.Client;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button btnSearchJ, btnUploadJ, btnDownloadJ;
    private ImageView imgUploadJ, imgDownloadJ;
    private EditText edtIndexJ;

    private StorageReference storageReference;
    private String archiveName = null;
    Uri imagePath;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference =  firebaseDatabase.getReference("images");

    private List<Client> clientList = new ArrayList<>();
    private ArrayAdapter<Client> clientArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase();
        Widgets();
        Buttons();
    }

    public void Widgets() {
        btnSearchJ = (Button)findViewById(R.id.btnSearch);
        btnUploadJ = (Button)findViewById(R.id.btnUpload);
        btnDownloadJ = (Button)findViewById(R.id.btnDownload);

        imgUploadJ = (ImageView)findViewById(R.id.imgUpload);
        imgDownloadJ = (ImageView)findViewById(R.id.imgDownload);

        edtIndexJ = (EditText)findViewById(R.id.edtIndex);
    }

    public void Firebase() {
        FirebaseApp.initializeApp(MainActivity.this);

        storageReference = FirebaseStorage.getInstance().getReference("images");
        Search("");
    }

    public void Buttons() {
        btnSearchJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPhoto();
            }
        });

        btnUploadJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreDatabase();
                UploadPhoto();
            }
        });

        btnDownloadJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search("");
                DownloadPhoto(MainActivity.this);
            }
        });
    }

    private void SearchPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private void StoreDatabase() {
        Date date = new Date();
        Client client = new Client();

        client.setId("1");
        archiveName = String.valueOf(date.getDate() + date.getTime());
        client.setName(archiveName);
        client.setAddress(client.getName());

        DatabaseReference clientReference = databaseReference.child(client.getName().toString());
        clientReference.setValue(client);
    }

    private void UploadPhoto() {
        StorageReference storageRef = storageReference.child(archiveName);

        storageRef.putFile(imagePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "Imagem carregado com sucesso!", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Algum erro aconteceu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Search(String name) {
        Query query = FirebaseDatabase.getInstance().getReference("images");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clientList.clear();

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    if (dataSnapshot1.exists()) {
                        Client client = dataSnapshot1.getValue(Client.class);
                        clientList.add(client);
                    }
                }

                clientArrayAdapter = new ArrayAdapter<Client>(MainActivity.this, android.R.layout.simple_list_item_1, clientList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void DownloadPhoto(Context context) {
        Integer index = 0;
        index = Integer.valueOf(edtIndexJ.getText().toString());
        Search("");
        StorageReference storageReference1 = storageReference;

        if (!clientList.isEmpty()) {
            storageReference1.child(clientList.get(index).getAddress().toString()).getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(MainActivity.this).load(uri).into(imgDownloadJ);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imagePath = data.getData();
            imgUploadJ.setImageURI(imagePath);
        }
    }
}
