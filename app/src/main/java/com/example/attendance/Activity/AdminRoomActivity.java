package com.example.attendance.Activity;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;


import com.example.attendance.Adapter.AdapterForRooms;
import com.example.attendance.Domin.Room;
import com.example.attendance.Fragments.FragmentDialogForAddRoom;
import com.example.attendance.Fragments.FragmentDialoge;
import com.example.attendance.R;
import com.example.attendance.Listener.RecyclerViewOnClickListener;
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
    public static final String ROOM_TITLE = "roomTitle";
    public static final String ROOM_ID = "roomId";
    private FloatingActionButton bnCreateRoom;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private EditText etRoomTitle;
    private FirebaseUser firebaseUser;
    private ImageView ivroom;
    private Uri imageUri;
    private ViewGroup dialogView;
    private ArrayList<Room> rooms;
    private AdapterForRooms adapterForAdminRooms;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_room);


        toolbar = findViewById(R.id.toolbar_in_admin_room);
        setSupportActionBar(toolbar);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        bnCreateRoom = findViewById(R.id.floatingActionButtonCreateRoom);
        recyclerView = findViewById(R.id.recycler_view_in_admin_room);


        dialogView = (ViewGroup) LayoutInflater.from(getBaseContext()).inflate(R.layout.custom_dialog_add_room, null, false);
        ivroom = dialogView.findViewById(R.id.room_image);
        etRoomTitle = dialogView.findViewById(R.id.editTextRoomTitle);


        rooms = new ArrayList<>();
        adapterForAdminRooms = new AdapterForRooms(rooms, new RecyclerViewOnClickListener() {
            @Override
            public void onClick(Room room) {

                String roomTitle, roomId;
                roomTitle = room.getRoomTitle();
                roomId = room.getId();
                Intent intent = new Intent(getBaseContext(), AdminActivity.class);
                intent.putExtra(ROOM_TITLE, roomTitle);
                intent.putExtra(ROOM_ID, roomId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
            }
        });
        RecyclerView.LayoutManager manager = new GridLayoutManager(this, 2);
        recyclerView.setAdapter(adapterForAdminRooms);
        recyclerView.setLayoutManager(manager);


        String adminUid = firebaseUser.getUid();

        databaseReference.getDatabase().getReference("Admin").child(adminUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                adapterForAdminRooms.updateData(rooms);
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

        bnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDiloge();



            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_in_rooms_activity,menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_in_rooms).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;

    }




    public void openDiloge(){
        FragmentDialogForAddRoom fragmentDialogForAddRoom=new FragmentDialogForAddRoom();;
        fragmentDialogForAddRoom.show(getSupportFragmentManager(),"Fragment Dialoge");
    }
}