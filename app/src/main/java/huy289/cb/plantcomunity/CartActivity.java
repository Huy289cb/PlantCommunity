package huy289.cb.plantcomunity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import huy289.cb.plantcomunity.Adapter.CartAdapter;
import huy289.cb.plantcomunity.Adapter.PreventionAdapter;
import huy289.cb.plantcomunity.Model.Carts;
import huy289.cb.plantcomunity.Model.Prevention;


public class CartActivity extends AppCompatActivity{
    private RecyclerView listCart;
    private TextView textviewTB;
    private List<Carts> cartsList;
    private CartAdapter cartAdapter;

    private FirebaseUser fUser;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_activity);
        listCart = findViewById(R.id.listCart);
        textviewTB = findViewById(R.id.textviewTB);
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        listCart.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        listCart.setLayoutManager(linearLayoutManager);
        cartsList = new ArrayList<>();

        cartAdapter = new CartAdapter(this, cartsList);
        listCart.setAdapter(cartAdapter);
        getCarts();
    }

    private void getCarts() {
        FirebaseDatabase.getInstance().getReference("Preventions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartsList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Carts carts = dataSnapshot.getValue(Carts.class);
                    if(carts.getId().equals(fUser.getUid())) {
                        cartsList.add(carts);
                    }
                }
                cartAdapter.notifyDataSetChanged();
                if (cartsList.size() == 0) {
                    listCart.setVisibility(View.GONE);
                    textviewTB.setVisibility(View.VISIBLE);
                } else {

                    listCart.setVisibility(View.VISIBLE);
                    textviewTB.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
