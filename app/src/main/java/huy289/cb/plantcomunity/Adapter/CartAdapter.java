package huy289.cb.plantcomunity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import huy289.cb.plantcomunity.Model.Carts;
import huy289.cb.plantcomunity.Model.Plant;
import huy289.cb.plantcomunity.Model.User;
import huy289.cb.plantcomunity.R;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context mContext;
    private List<Carts> mCarts;

    public CartAdapter(Context mContext, List<Carts> mCarts) {
        this.mContext = mContext;
        this.mCarts = mCarts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_cart, parent, false);
        return new CartAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Carts carts = mCarts.get(position);
        Picasso.get().load(carts.getImageUrl())
                .error(R.drawable.ic_error)
                .placeholder(R.drawable.ic_leaf)
                .into(holder.imageviewCart);
        holder.name.setText("Tên cây: " + carts.getName());
        holder.price.setText("Đơn giá: " + carts.getPrice());
        holder.quantity.setText("Số lượng: " + carts.getQuantity());
        FirebaseDatabase.getInstance().getReference("Users").child(carts.getPublisherId())
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
    public int getItemCount() {
        return mCarts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageviewCart;
        public TextView name;
        public TextView price;
        public TextView quantity;
        public TextView address;
        public TextView publisher;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageviewCart = itemView.findViewById(R.id.imageviewCart);
            name = itemView.findViewById(R.id.twName);
            price = itemView.findViewById(R.id.twPrice);
            quantity = itemView.findViewById(R.id.twQuantity);
            address = itemView.findViewById(R.id.twAddress);
            publisher = itemView.findViewById(R.id.twPublisher);
        }
    }

}
