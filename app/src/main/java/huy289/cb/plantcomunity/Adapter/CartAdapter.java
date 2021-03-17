package huy289.cb.plantcomunity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import huy289.cb.plantcomunity.Model.Cart;
import huy289.cb.plantcomunity.Model.Plant;
import huy289.cb.plantcomunity.Model.User;
import huy289.cb.plantcomunity.R;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context mContext;
    private List<Cart> mCarts;

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
        Cart cart = mCarts.get(position);
        Locale vn = new Locale("vi", "VN");
        NumberFormat vndFormat = NumberFormat.getCurrencyInstance(vn);
        holder.quantity.setText("Số lượng: " + cart.getQuantity());
        holder.price.setText("Đơn giá: " + vndFormat.format(Float.parseFloat(cart.getPrice())));
        float totalp = (float) (Float.parseFloat(cart.getQuantity()) * Float.parseFloat(cart.getPrice()));
        holder.totalPrice.setText( "Tổng tiền: " + vndFormat.format(totalp));
        FirebaseDatabase.getInstance().getReference("Plants").child(cart.getPlantId())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Plant plant = snapshot.getValue(Plant.class);
                Picasso.get().load(plant.getImageUrl())
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.ic_leaf)
                        .resize(400, 400)
                        .centerCrop()
                        .onlyScaleDown()
                        .into(holder.imagePlant);
                holder.name.setText("Tên cây: " + plant.getName());
                holder.address.setText("Địa chỉ: " + plant.getAddress());
                FirebaseDatabase.getInstance().getReference("Users").child(plant.getPublisher())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                holder.publisher.setText("Người bán: " + user.getUsername());
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
