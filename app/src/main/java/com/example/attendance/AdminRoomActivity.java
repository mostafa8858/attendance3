package com.example.attendance;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.example.attendance.Domin.Room;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AdminRoomActivity extends AppCompatActivity {
    private static final int IMAGE_REQEST_CODE = 102;
    private FloatingActionButton bnCreateRoom;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReferenceForRoom, databaseReferenceForAdmin;
    private EditText etRoomTitle;
    private FirebaseUser firebaseUser;
    private ImageView ivroom;
    private Uri imageUri;
    private View dialogView;
    private ArrayList<Room> rooms;
    private AdapterForAdminRooms adapterForAdminRooms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_room);
        toolbar = findViewById(R.id.toolbar_in_admin_room);
        setSupportActionBar(toolbar);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceForRoom = FirebaseDatabase.getInstance().getReference("Rooms");
        databaseReferenceForAdmin = FirebaseDatabase.getInstance().getReference("Admin");

        bnCreateRoom = findViewById(R.id.floatingActionButtonCreateRoom);
        recyclerView = findViewById(R.id.recycler_view_in_admin_room);


        dialogView = LayoutInflater.from(getBaseContext()).inflate(R.layout.custom_dialog, null, false);
        ivroom = dialogView.findViewById(R.id.room_image);
        etRoomTitle = dialogView.findViewById(R.id.editTextRoomTitle);


        rooms = new ArrayList<>();
        adapterForAdminRooms = new AdapterForAdminRooms(rooms);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapterForAdminRooms);
        recyclerView.setLayoutManager(manager);


       String adminName= firebaseUser.getDisplayName();
       String adminUid=firebaseUser.getUid();


        databaseReferenceForAdmin.getDatabase().getReference("Admin").child(adminName + "  " + adminUid);
        databaseReferenceForAdmin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Room room = dataSnapshot.getValue(Room.class);
                    rooms.add(room);
                }
                recyclerView.setAdapter(adapterForAdminRooms);

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        ivroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_REQEST_CODE);
            }
        });

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this).setTitle("Create New Room")
                .setIcon(R.drawable.ic_alert_blue_24).setView(dialogView);
        bnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReferenceForAdmin.getDatabase().getReference("Admin");
                        databaseReferenceForRoom.getDatabase().getReference("Rooms");

                        String roomTitle = etRoomTitle.getText().toString();
                        String adminName = firebaseUser.getDisplayName();
                        String roomId = databaseReferenceForRoom.push().getKey();
                        String adminUid = firebaseUser.getUid();

                        Room room = new Room(roomTitle, imageUri, null, adminName, roomId);

                        databaseReferenceForRoom.child(roomTitle + "  " + roomId).setValue(room);

                        databaseReferenceForAdmin.child(adminName + "  " + adminUid).child(roomId).setValue(room);


                    }
                }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQEST_CODE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            ivroom.setImageURI(imageUri);

        }
    }
}