package huy289.cb.plantcomunity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import huy289.cb.plantcomunity.MainActivity;
import huy289.cb.plantcomunity.Model.User;
import huy289.cb.plantcomunity.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isFragment;
    // limit
    private final int limit = 10;

    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);
        holder.btnFollow.setVisibility(View.VISIBLE);

        holder.userName.setText(user.getUsername());
        holder.fullName.setText(user.getFullname());

        Picasso.get().load(user.getImageUrl())
                .placeholder(R.mipmap.ic_launcher).into(holder.imageProfile);

        if(user.getId().equals(firebaseUser.getUid())){
            holder.btnFollow.setVisibility(View.GONE);
        }

        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherId", user.getId());
                mContext.startActivity(intent);
            }
        });

        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherId", user.getId());
                mContext.startActivity(intent);
//                if(holder.btnFollow.getText().toString().equals("theo d??i")){
//                    FirebaseDatabase.getInstance().getReference("Follow")
//                            .child(firebaseUser.getUid()).child("following").child(user.getId())
//                            .setValue(true);
//
//                    FirebaseDatabase.getInstance().getReference("Follow")
//                            .child(user.getId()).child("followers").child(firebaseUser.getUid())
//                            .setValue(true);
//                } else {
//                    FirebaseDatabase.getInstance().getReference("Follow")
//                            .child(firebaseUser.getUid()).child("following").child(user.getId())
//                            .removeValue();
//
//                    FirebaseDatabase.getInstance().getReference("Follow")
//                            .child(user.getId()).child("followers").child(firebaseUser.getUid())
//                            .removeValue();
//                }
            }
        });
    }

//    private void isFollowed(final String id, final Button btnFollow) {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
//                .child(firebaseUser.getUid()).child("following");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.child(id).exists()){
//                    btnFollow.setText("??ang theo d??i");
//                } else {
//                    btnFollow.setText("theo d??i");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    @Override
    public int getItemCount() {
        return mUsers.size() > limit ? limit : mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView imageProfile;
        public TextView userName;
        public TextView fullName;
        public Button btnFollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            userName = itemView.findViewById(R.id.user_name);
            fullName = itemView.findViewById(R.id.fullname);
            btnFollow = itemView.findViewById(R.id.btn_follow);
        }
    }

}
