package huy289.cb.plantcomunity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import huy289.cb.plantcomunity.Adapter.PreventionAdapter;
import huy289.cb.plantcomunity.Model.Cart;
import huy289.cb.plantcomunity.Model.Plant;
import huy289.cb.plantcomunity.Model.Prevention;
import huy289.cb.plantcomunity.Model.User;

public class DetailPlantActivity extends AppCompatActivity {

    private ImageView imagePlant;
    private TextView name;
    private TextView age;
    private TextView address;
    private TextView quantity;
    private TextView price;
    private TextView sowingDate;
    private TextView plantDate;
    private Button addToCart;
    private Toolbar toolbar;
    private CircleImageView publisherImage;
    private TextView publisher;

    private RecyclerView recyclerViewPrevention;
    private List<Prevention> mPreventions;
    private PreventionAdapter preventionAdapter;

    private String plantId;
    private Plant plant;

    private FirebaseUser fUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_plant);


        imagePlant = findViewById(R.id.image_plant);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        address = findViewById(R.id.address);
        quantity = findViewById(R.id.quantity);
        price = findViewById(R.id.price);
        sowingDate = findViewById(R.id.sowingDate);
        plantDate = findViewById(R.id.plantDate);
        recyclerViewPrevention = findViewById(R.id.recycle_view_prevention);
        addToCart = findViewById(R.id.addcart);
        toolbar = findViewById(R.id.toolbar);
        publisherImage = findViewById(R.id.publisher_image);
        publisher = findViewById(R.id.publisher);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        plantId = intent.getStringExtra("plantId");

        getPlantDetail();

        recyclerViewPrevention.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerViewPrevention.setLayoutManager(linearLayoutManager);
        recyclerViewPrevention
                .addItemDecoration(new DividerItemDecoration(recyclerViewPrevention.getContext(), DividerItemDecoration.VERTICAL));
        mPreventions = new ArrayList<>();

        preventionAdapter = new PreventionAdapter(this, mPreventions);
        recyclerViewPrevention.setAdapter(preventionAdapter);
        getPreventions();

    }

    private void getPublisherDetail() {
        FirebaseDatabase.getInstance().getReference("Users").child(plant.getPublisher())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        publisher.setText(user.getUsername());
                        Picasso.get().load(user.getImageUrl())
                                .error(R.drawable.ic_person)
                                .placeholder(R.drawable.ic_person)
                                .resize(50, 50)
                                .centerCrop()
                                .onlyScaleDown()
                                .into(publisherImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getPlantDetail() {
        FirebaseDatabase.getInstance().getReference("Plants").child(plantId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                plant = snapshot.getValue(Plant.class);

                Picasso.get().load(plant.getImageUrl())
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.ic_leaf)
                        .resize(900, 900)
                        .centerCrop()
                        .onlyScaleDown()
                        .into(imagePlant);
                name.setText(plant.getName());
                age.setText(plant.getAge());
                address.setText(plant.getAddress());
                quantity.setText(plant.getQuantity());
                Locale vn = new Locale("vi", "VN");
                NumberFormat vndFormat = NumberFormat.getCurrencyInstance(vn);
                price.setText( vndFormat.format(Float.parseFloat(plant.getPrice())));

                sowingDate.setText(plant.getSowingDate());
                plantDate.setText(plant.getPlantDate());

                getPublisherDetail();

                //nút thêm vào giỏ
                addToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // dialog điều chỉnh số lượng khi thêm
                        LayoutInflater layoutInflater = LayoutInflater.from(DetailPlantActivity.this);
                        View add_cart_quantity_view = layoutInflater.inflate(R.layout.add_cart_quantity_dialog, null);
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailPlantActivity.this);
                        alertDialog.setView(add_cart_quantity_view);
                        final EditText quantity = add_cart_quantity_view.findViewById(R.id.quantity);
                        ImageButton add = add_cart_quantity_view.findViewById(R.id.add);
                        ImageButton minus = add_cart_quantity_view.findViewById(R.id.minus);
                        add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int quantityValue = Integer.parseInt(quantity.getText().toString());
                                if (quantityValue >= Integer.parseInt(plant.getQuantity())) {
                                    Toast.makeText(DetailPlantActivity.this, "Không thể thêm nhiều hơn số lượng tối đa của sản phẩm", Toast.LENGTH_SHORT).show();
                                } else {
                                    quantity.setText((quantityValue + 1) + "");
                                }
                            }
                        });
                        minus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int quantityValue = Integer.parseInt(quantity.getText().toString());
                                if (quantityValue == 1) {
                                    Toast.makeText(DetailPlantActivity.this, "Không thể giảm ít hơn 1", Toast.LENGTH_SHORT).show();
                                } else {
                                    quantity.setText((quantityValue - 1) + "");
                                }
                            }
                        });
                        alertDialog.setTitle("Chọn số lượng");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //thêm vào giỏ
                                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Carts").child(fUser.getUid());
                                final HashMap<String, Object> map = new HashMap<>();
                                map.put("plantId", plant.getId());
                                // addListenerForSingleValueEvent lấy dữ liệu 1 lần (ko realtime như addValueEventListener)
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.child(plant.getId()).hasChildren()) {
                                            map.put("quantity", quantity.getText().toString());
                                            map.put("price", plant.getPrice());
                                            FirebaseDatabase.getInstance().getReference("Carts")
                                                    .child(fUser.getUid()).child(plant.getId())
                                                    .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(DetailPlantActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                                                        Log.d("Them", "Đã thêm");
                                                    }
                                                }
                                            });
                                        } else {
                                            // kiểm tra trong giỏ hàng có chưa thì cộng dồn số lượng
                                            Cart cart = snapshot.child(plant.getId()).getValue(Cart.class);
                                            int bQuantity = Integer.parseInt(cart.getQuantity());
                                            int aQuantity = Integer.parseInt(quantity.getText().toString());
                                            int total = (bQuantity + aQuantity);
                                            if (total > Integer.parseInt(plant.getQuantity())) {
                                                Toast.makeText(DetailPlantActivity.this, "Số lượng trong giỏ hàng không thể lớn hơn tổng số sản phẩm", Toast.LENGTH_SHORT).show();
                                            } else {
                                                map.put("quantity", total + "");
                                                ref.child(plant.getId()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(DetailPlantActivity.this, "Đã cập nhật sản phẩm trong giỏ hàng", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                        alertDialog.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alertDialog.show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPreventions() {
        FirebaseDatabase.getInstance().getReference("Preventions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPreventions.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Prevention prevention = dataSnapshot.getValue(Prevention.class);
                    if(prevention.getPlantId().equals(plantId)) {
                        mPreventions.add(prevention);
                    }
                }
                preventionAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
