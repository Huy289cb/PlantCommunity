package huy289.cb.plantcomunity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import huy289.cb.plantcomunity.Adapter.CartAdapter;
import huy289.cb.plantcomunity.Model.Cart;
import huy289.cb.plantcomunity.Model.Plant;


public class CartActivity extends AppCompatActivity{
    private RecyclerView recyclerViewCart;
    private TextView noti;
    private List<Cart> cartsList;
    private CartAdapter cartAdapter;
    private TextView total;
    private TextView price;
    private Button checkout;

    private float totalPrice = 0;

    private FirebaseUser fUser;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerViewCart = findViewById(R.id.recycle_view_cart);
        noti = findViewById(R.id.noti);
        total = findViewById(R.id.total);
        price = findViewById(R.id.price);
        checkout = findViewById(R.id.checkout);
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("GIỎ HÀNG");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerViewCart.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerViewCart.setLayoutManager(linearLayoutManager);
        cartsList = new ArrayList<>();

        cartAdapter = new CartAdapter(this, cartsList);
        recyclerViewCart.setAdapter(cartAdapter);

        getCartsItemandTotalPrice();
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO checkout activity;
            }
        });
    }

    private void getCartsItemandTotalPrice() {
        FirebaseDatabase.getInstance().getReference("Carts").child(fUser.getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartsList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Log.d("dataSnapshot", dataSnapshot.getValue().toString());
                    Cart cart = dataSnapshot.getValue(Cart.class);
                        cartsList.add(cart);
                }
                cartAdapter.notifyDataSetChanged();
                if (cartsList.size() == 0) {
                    recyclerViewCart.setVisibility(View.GONE);
                    noti.setVisibility(View.VISIBLE);
                } else {
                    recyclerViewCart.setVisibility(View.VISIBLE);
                    noti.setVisibility(View.GONE);
                }

                // get total and price
                if (cartsList.size() > 0) {
                    total.setText("Tổng số sản phẩm: " + cartsList.size());

                    for (Cart cart : cartsList) {
                        int quantity = Integer.parseInt(cart.getQuantity());
                        float price = Float.parseFloat((cart.getPrice()));
                        totalPrice = (float) (totalPrice + (quantity * price));
                    }
                    Locale vn = new Locale("vi", "VN");
//                    Currency vnd = Currency.getInstance(vn);
                    NumberFormat vndFormat = NumberFormat.getCurrencyInstance(vn);
                    price.setText("Tổng tiền: " + vndFormat.format(totalPrice));

                } else {
                    total.setVisibility(View.GONE);
                    price.setVisibility(View.GONE);
                    checkout.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
