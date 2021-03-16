package huy289.cb.plantcomunity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.BreakIterator;
import java.util.HashMap;
import java.util.List;

import huy289.cb.plantcomunity.AddPlantActivity;
import huy289.cb.plantcomunity.CommentActivity;
import huy289.cb.plantcomunity.DetailPlantActivity;
import huy289.cb.plantcomunity.MainActivity;
import huy289.cb.plantcomunity.Model.Plant;
import huy289.cb.plantcomunity.PostActivity;
import huy289.cb.plantcomunity.R;
import huy289.cb.plantcomunity.StartActivity;

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

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_store,parent,false);
        return new ItemStoreAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Plant plant = mPlants.get(position);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        Picasso.get().load(plant.getImageUrl()).into(holder.imagePlant);
        holder.name.setText("Tên: " + plant.getName());
        holder.age.setText("Tuổi: " + plant.getAge());
        holder.address.setText("Địa chỉ: " + plant.getAddress());
        holder.quantity.setText("Số lượng: " + plant.getQuantity());
        holder.price.setText("Giá: " + plant.getPrice() + " vnđ");

        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO chuyển đến DetailPlantActivity
                Intent intent = new Intent(mContext, DetailPlantActivity.class);
                mContext.startActivity(intent);
            }

        });
        holder.btngiohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("plantId", plant.getId());

                FirebaseDatabase.getInstance().getReference("Carts").child(fUser.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

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
        public TextView age;
        public TextView address;
        public TextView quantity;
        public TextView price;
        public Button btnDetail;
        public Button btngiohang;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imagePlant = itemView.findViewById(R.id.image_plant);
            name = itemView.findViewById(R.id.name);
            age = itemView.findViewById(R.id.age);
            address = itemView.findViewById(R.id.address);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.price);
            btnDetail = itemView.findViewById(R.id.btn_detailPlant);
            btngiohang = itemView.findViewById(R.id.btn_giohang);
    }
}
}
