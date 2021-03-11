package huy289.cb.plantcomunity.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import huy289.cb.plantcomunity.Adapter.MyPlantAdapter;
import huy289.cb.plantcomunity.Adapter.PostAdapter;
import huy289.cb.plantcomunity.AddPlantActivity;
import huy289.cb.plantcomunity.Model.Plant;
import huy289.cb.plantcomunity.R;

public class MyPlantFragment extends Fragment {

    private ImageView addPlant;
    private RecyclerView recyclerViewMyPlants;
    private MyPlantAdapter myPlantAdapter;
    private List<Plant> mPlants;

    private FirebaseUser fUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_plant, container, false);

        addPlant = view.findViewById(R.id.add_plant);
        recyclerViewMyPlants = view.findViewById(R.id.recycle_view_my_plants);
        recyclerViewMyPlants.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerViewMyPlants.setLayoutManager(linearLayoutManager);
        mPlants = new ArrayList<>();

        myPlantAdapter = new MyPlantAdapter(getContext(), mPlants);
        recyclerViewMyPlants.setAdapter(myPlantAdapter);
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        addPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddPlantActivity.class));
            }
        });

        getMyPlants();

        return view;
    }

    private void getMyPlants() {
        FirebaseDatabase.getInstance().getReference("Plants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPlants.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Plant plant = dataSnapshot.getValue(Plant.class);
                    if(plant.getPublisher().equals(fUser.getUid())) {
                        mPlants.add(plant);
                    }
                }
                myPlantAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}