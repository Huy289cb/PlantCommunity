package huy289.cb.plantcomunity.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import huy289.cb.plantcomunity.DetailPlantActivity;
import huy289.cb.plantcomunity.Model.Cart;
import huy289.cb.plantcomunity.Model.Plant;
import huy289.cb.plantcomunity.Model.User;
import huy289.cb.plantcomunity.PreventionActivity;
import huy289.cb.plantcomunity.R;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context mContext;
    private List<Cart> mCarts;
    private Plant plant;
    private FirebaseUser fUser;

    public CartAdapter(Context mContext, List<Cart> mCarts) {
        this.mContext = mContext;
        this.mCarts = mCarts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_item, parent, false);
        return new CartAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        final Cart cart = mCarts.get(position);
        Locale vn = new Locale("vi", "VN");
        NumberFormat vndFormat = NumberFormat.getCurrencyInstance(vn);
        holder.quantity.setText("S??? l?????ng: " + cart.getQuantity());
        holder.price.setText("????n gi??: " + vndFormat.format(Float.parseFloat(cart.getPrice())));
        float totalp = (float) (Float.parseFloat(cart.getQuantity()) * Float.parseFloat(cart.getPrice()));
        holder.totalPrice.setText( "T???ng ti???n: " + vndFormat.format(totalp));
        FirebaseDatabase.getInstance().getReference("Plants").child(cart.getPlantId())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                plant = snapshot.getValue(Plant.class);
                Picasso.get().load(plant.getImageUrl())
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.ic_leaf)
                        .resize(400, 400)
                        .centerCrop()
                        .onlyScaleDown()
                        .into(holder.imagePlant);
                holder.name.setText("T??n c??y: " + plant.getName());
                holder.address.setText("?????a ch???: " + plant.getAddress());
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                View add_cart_quantity_view = layoutInflater.inflate(R.layout.add_cart_quantity_dialog, null);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                alertDialog.setView(add_cart_quantity_view);
                final EditText quantity = add_cart_quantity_view.findViewById(R.id.quantity);
                quantity.setText(cart.getQuantity());
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
                alertDialog.setTitle("Thay ?????i s??? l?????ng");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("L??u", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final DatabaseReference ref = FirebaseDatabase.getInstance()
                                .getReference("Carts").child(fUser.getUid()).child(cart.getPlantId());
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                HashMap<String, Object> map = new HashMap<>();
                                if (snapshot.hasChildren()) {
                                    int aQuantity = Integer.parseInt(quantity.getText().toString());
                                    if (aQuantity > Integer.parseInt(plant.getQuantity())) {
                                        Toast.makeText(mContext, "S??? l?????ng kh??ng th??? l???n h??n t???ng s??? s???n ph???m", Toast.LENGTH_SHORT).show();
                                    } else {
                                        map.put("quantity", aQuantity + "");
                                        ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(mContext, "???? c???p nh???t gi??? h??ng", Toast.LENGTH_SHORT).show();
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

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCartItem();
            }

            private void deleteCartItem() {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("B???n c?? mu???n x??a?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Kh??ng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "X??a", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference("Carts")
                                .child(fUser.getUid()).child(cart.getPlantId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(mContext, "???? x??a!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                });
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCarts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imagePlant;
        public TextView name;
        public TextView price;
        public TextView quantity;
        public TextView address;
        public TextView publisher;
        public TextView totalPrice;
        public Button update;
        public Button delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePlant = itemView.findViewById(R.id.image_plant);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.quantity);
            address = itemView.findViewById(R.id.address);
            publisher = itemView.findViewById(R.id.publisher);
            totalPrice = itemView.findViewById(R.id.total_price);
            update = itemView.findViewById(R.id.update);
            delete = itemView.findViewById(R.id.delete);
        }
    }

}
