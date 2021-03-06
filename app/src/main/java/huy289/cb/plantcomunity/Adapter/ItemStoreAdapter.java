package huy289.cb.plantcomunity.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import huy289.cb.plantcomunity.DetailPlantActivity;
import huy289.cb.plantcomunity.Model.Cart;
import huy289.cb.plantcomunity.Model.Plant;
import huy289.cb.plantcomunity.Model.User;
import huy289.cb.plantcomunity.R;

public class ItemStoreAdapter extends RecyclerView.Adapter<ItemStoreAdapter.ViewHolder> {

    private Context mContext;
    private List<Plant> mPlants;
    private FirebaseUser fUser;

    public ItemStoreAdapter(Context mContext, List<Plant> mPlants) {
        this.mContext = mContext;
        this.mPlants = mPlants;
    }
    @NonNull
    @Override
    public ItemStoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.store_item,parent,false);
        return new ItemStoreAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Plant plant = mPlants.get(position);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        Picasso.get().load(plant.getImageUrl())
                .error(R.drawable.ic_error)
                .placeholder(R.drawable.ic_leaf)
                .resize(400, 400)
                .centerCrop()
                .onlyScaleDown()
                .into(holder.imagePlant);
        holder.name.setText("T??n c??y: " + plant.getName());
        holder.address.setText("?????a ch???: " + plant.getAddress());
        holder.quantity.setText("S??? l?????ng: " + plant.getQuantity());
        Locale vn = new Locale("vi", "VN");
        NumberFormat vndFormat = NumberFormat.getCurrencyInstance(vn);
        holder.price.setText("????n gi??: " + vndFormat.format(Float.parseFloat(plant.getPrice())));
        //Get publisher name
        FirebaseDatabase.getInstance().getReference("Users").child(plant.getPublisher())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                holder.publisher.setText("Ng?????i b??n: " + user.getUsername());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailPlantActivity.class);
                intent.putExtra("plantId", plant.getId());
                mContext.startActivity(intent);
            }

        });

        // n???u l?? s???n ph???m c???a m??nh b??n th?? k th??? th??m v??o gi???;
        if(plant.getPublisher().equals(fUser.getUid())) {
            holder.addcart.setVisibility(View.GONE);
        }

        holder.addcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // dialog ??i???u ch???nh s??? l?????ng khi th??m
                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                View add_cart_quantity_view = layoutInflater.inflate(R.layout.add_cart_quantity_dialog, null);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                alertDialog.setView(add_cart_quantity_view);
                final EditText quantity = add_cart_quantity_view.findViewById(R.id.quantity);
                ImageButton add = add_cart_quantity_view.findViewById(R.id.add);
                ImageButton minus = add_cart_quantity_view.findViewById(R.id.minus);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quantityValue = Integer.parseInt(quantity.getText().toString());
                        if (quantityValue >= Integer.parseInt(plant.getQuantity())) {
                            Toast.makeText(mContext, "Kh??ng th??? th??m nhi???u h??n s??? l?????ng t???i ??a c???a s???n ph???m", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(mContext, "Kh??ng th??? gi???m ??t h??n 1", Toast.LENGTH_SHORT).show();
                        } else {
                            quantity.setText((quantityValue - 1) + "");
                        }
                    }
                });
                alertDialog.setTitle("Ch???n s??? l?????ng");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Th??m", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO th??m v??o gi???
                        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Carts").child(fUser.getUid());
                        final HashMap<String, Object> map = new HashMap<>();
                        map.put("plantId", plant.getId());
                        // addListenerForSingleValueEvent ch??? l???y d??? li???u 1 l???n (ko realtime nh?? addValueEventListener)
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
                                                Toast.makeText(mContext, "???? th??m v??o gi??? h??ng", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    // TODO ki???m tra trong gi??? h??ng c?? ch??a th?? c???ng d???n s??? l?????ng
                                    Cart cart = snapshot.child(plant.getId()).getValue(Cart.class);
                                    int bQuantity = Integer.parseInt(cart.getQuantity());
                                    int aQuantity = Integer.parseInt(quantity.getText().toString());
                                    int total = (bQuantity + aQuantity);
                                    if (total > Integer.parseInt(plant.getQuantity())) {
                                        Toast.makeText(mContext, "S??? l?????ng trong gi??? h??ng kh??ng th??? l???n h??n t???ng s??? s???n ph???m", Toast.LENGTH_SHORT).show();
                                    } else {
                                        map.put("quantity", total + "");
                                        ref.child(plant.getId()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(mContext, "???? c???p nh???t s???n ph???m trong gi??? h??ng", Toast.LENGTH_SHORT).show();
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
                alertDialog.setNegativeButton("H???y", new DialogInterface.OnClickListener() {
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
    public int getItemCount() {
        return mPlants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imagePlant;
        public TextView name;
        public TextView publisher;
        public TextView address;
        public TextView quantity;
        public TextView price;
        public Button addcart;
        public Button detail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imagePlant = itemView.findViewById(R.id.image_plant);
            name = itemView.findViewById(R.id.name);
            publisher = itemView.findViewById(R.id.publisher);
            address = itemView.findViewById(R.id.address);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.price);
            addcart = itemView.findViewById(R.id.addcart);
            detail = itemView.findViewById(R.id.detail);
        }
    }
}
