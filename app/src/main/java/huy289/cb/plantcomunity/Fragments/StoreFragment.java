package huy289.cb.plantcomunity.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import huy289.cb.plantcomunity.Adapter.ItemStoreAdapter;
import huy289.cb.plantcomunity.Adapter.MyPlantAdapter;
import huy289.cb.plantcomunity.AddPlantActivity;
import huy289.cb.plantcomunity.CartActivity;
import huy289.cb.plantcomunity.Model.Plant;
import huy289.cb.plantcomunity.Model.Post;
import huy289.cb.plantcomunity.R;

public class StoreFragment extends Fragment {

    private ImageView cart;
    private RecyclerView recyclerViewProduct;
    private ItemStoreAdapter ItemStoreAdapter;
    private List<Plant> mPlants;
    private FirebaseUser fUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);

        cart = view.findViewById(R.id.cart);
        recyclerViewProduct = view.findViewById(R.id.recycle_view_product);
        recyclerViewProduct.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewProduct.setLayoutManager(linearLayoutManager);
        mPlants = new ArrayList<>();

        ItemStoreAdapter = new ItemStoreAdapter(getContext(), mPlants);
        recyclerViewProduct.setAdapter(ItemStoreAdapter);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CartActivity.class));
            }
        });
        getMyPlants();

        // nếu có hàng trong giỏ thì đổi icon giỏ hàng -> xanh;
        isCart();

        return view;
    }

    private void isCart() {
        FirebaseDatabase.getInstance().getReference("Carts").child(fUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    cart.setImageResource(R.drawable.ic_has_cart);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getMyPlants() {
        FirebaseDatabase.getInstance().getReference("Plants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPlants.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Plant plant = dataSnapshot.getValue(Plant.class);
                        mPlants.add(plant);
                }
                if(mPlants.size()>0) {
                    ItemStoreAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}