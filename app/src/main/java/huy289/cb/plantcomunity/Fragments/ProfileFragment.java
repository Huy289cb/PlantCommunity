package huy289.cb.plantcomunity.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import huy289.cb.plantcomunity.Adapter.MyPlantAdapter;
import huy289.cb.plantcomunity.Adapter.PhotoAdapter;
import huy289.cb.plantcomunity.Adapter.PostAdapter;
import huy289.cb.plantcomunity.EditProfileActivity;
import huy289.cb.plantcomunity.MainActivity;
import huy289.cb.plantcomunity.Model.Plant;
import huy289.cb.plantcomunity.Model.Post;
import huy289.cb.plantcomunity.Model.User;
import huy289.cb.plantcomunity.R;
import huy289.cb.plantcomunity.StartActivity;

public class ProfileFragment extends Fragment {

    private CircleImageView imageProfile;
    private TextView postCount;
    private TextView plantCount;
    private TextView fullname;
    private TextView bio;
    private TextView username;
    private ImageView myPosts;
    private ImageView myPlants;
    private Button editProfile;
    private Toolbar toolbar;

    private RecyclerView recyclerViewPost;
    private PostAdapter postAdapter;
    private List<Post> myPostList;

    private RecyclerView recyclerViewPlant;
    private MyPlantAdapter plantAdapter;
    private List<Plant> myPlantList;

    private FirebaseUser fUser;
    String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imageProfile = view.findViewById(R.id.image_profile);
        postCount = view.findViewById(R.id.post_count);
        plantCount = view.findViewById(R.id.plant_count);
        bio = view.findViewById(R.id.bio);
        fullname = view.findViewById(R.id.fullname);
        username = view.findViewById(R.id.username);
        myPosts = view.findViewById(R.id.my_posts);
        myPlants = view.findViewById(R.id.my_plants);
        editProfile = view.findViewById(R.id.edit_profile);
        toolbar = view.findViewById(R.id.toolbar);

        toolbar.inflateMenu(R.menu.user_options_menu);


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.logout: {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getContext(), StartActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        break;
                    }
                }
                return false;
            }
        });

        recyclerViewPost = view.findViewById(R.id.recycle_view_posts);
        recyclerViewPost.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerViewPost.setLayoutManager(linearLayoutManager);
        recyclerViewPost.addItemDecoration(new DividerItemDecoration(recyclerViewPost.getContext(), DividerItemDecoration.VERTICAL));

        myPostList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), myPostList);
        recyclerViewPost.setAdapter(postAdapter);

        //recycle view plant
        recyclerViewPlant = view.findViewById(R.id.recycle_view_plants);
        recyclerViewPlant.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        linearLayoutManager1.setStackFromEnd(true);
        linearLayoutManager1.setReverseLayout(true);
        recyclerViewPlant.setLayoutManager(linearLayoutManager1);
        recyclerViewPlant.addItemDecoration(new DividerItemDecoration(recyclerViewPlant.getContext(), DividerItemDecoration.VERTICAL));
        myPlantList = new ArrayList<>();
        plantAdapter = new MyPlantAdapter(getContext(), myPlantList);
        recyclerViewPlant.setAdapter(plantAdapter);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                .getString("profileId", "none");

        if(data.equals("none")) {
            profileId = fUser.getUid();
        } else {
            profileId = data;
        }

        userInfo();
        getPostCount();
        getPlantCount();
        myPosts();
        myPlants();

        if (profileId.equals(fUser.getUid())) {
            editProfile.setText("Chỉnh sửa thông tin cá nhân");
        } else {
//            checkFollowingStatus();
            editProfile.setVisibility(View.GONE);
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = editProfile.getText().toString();
                if(btnText.equals("Chỉnh sửa thông tin cá nhân")) {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                } else {
//                    if(btnText.equals("Theo dõi")) {
//                        FirebaseDatabase.getInstance().getReference("Follow").child(fUser.getUid())
//                                .child("following").child(profileId).setValue(true);
//                        FirebaseDatabase.getInstance().getReference("Follow").child(profileId)
//                                .child("followers").child(fUser.getUid()).setValue(true);
//                    } else {
//                        FirebaseDatabase.getInstance().getReference("Follow").child(fUser.getUid())
//                                .child("following").child(profileId).removeValue();
//                        FirebaseDatabase.getInstance().getReference("Follow").child(profileId)
//                                .child("followers").child(fUser.getUid()).removeValue();
//                    }
                }
            }
        });

        myPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewPost.setVisibility(View.VISIBLE);
                recyclerViewPlant.setVisibility(View.GONE);
            }
        });

        myPlants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewPost.setVisibility(View.GONE);
                recyclerViewPlant.setVisibility(View.VISIBLE);
            }
        });

        return view;

    }

    private void myPlants() {
        FirebaseDatabase.getInstance().getReference("Plants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myPlantList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Plant plant = dataSnapshot.getValue(Plant.class);

                    if(plant.getPublisher().equals(profileId)) {
                        myPlantList.add(plant);
                    }
                }
                plantAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myPosts() {

        FirebaseDatabase.getInstance().getReference("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myPostList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    if(post.getPublisher().equals(profileId)) {
                        myPostList.add(post);
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

//    private void checkFollowingStatus() {
//
//        FirebaseDatabase.getInstance().getReference("Follow").child(fUser.getUid())
//                .child("following").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.child(profileId).exists()) {
//                    editProfile.setText("Đang theo dõi");
//                } else {
//                    editProfile.setText("Theo dõi");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }

    private void getPostCount() {

        FirebaseDatabase.getInstance().getReference("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    if(post.getPublisher().equals(profileId)) {
                        counter ++;
                    }
                }
                postCount.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getPlantCount() {

        FirebaseDatabase.getInstance().getReference("Plants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Plant plant = dataSnapshot.getValue(Plant.class);

                    if(plant.getPublisher().equals(profileId)) {
                        counter ++;
                    }
                }
                plantCount.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

//    private void getFollowerAndFollowingCount() {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Follow").child(profileId);
//        ref.child("followers").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                follower.setText("" + snapshot.getChildrenCount());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        ref.child("following").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                following.setText("" + snapshot.getChildrenCount());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void userInfo() {

        FirebaseDatabase.getInstance().getReference("Users")
                .child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if(user.getImageUrl().equals("default")){
                    imageProfile.setImageResource(R.drawable.ic_person);
                } else {
                    //TODO sửa lại picasso.load
                    Picasso.get().load(user.getImageUrl()).into(imageProfile);
                }
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}