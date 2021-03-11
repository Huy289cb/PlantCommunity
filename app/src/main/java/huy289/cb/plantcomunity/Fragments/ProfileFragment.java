package huy289.cb.plantcomunity.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
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
import huy289.cb.plantcomunity.Adapter.PhotoAdapter;
import huy289.cb.plantcomunity.Adapter.PostAdapter;
import huy289.cb.plantcomunity.EditProfileActivity;
import huy289.cb.plantcomunity.Model.Post;
import huy289.cb.plantcomunity.Model.User;
import huy289.cb.plantcomunity.R;

public class ProfileFragment extends Fragment {

    private CircleImageView imageProfile;
    private ImageView options;
    private TextView posts;
    private TextView follower;
    private TextView following;
    private TextView fullname;
    private TextView bio;
    private TextView username;
    private ImageView myPictures;
    private ImageView savedPictures;
    private Button editProfile;

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> myPostList;

    private FirebaseUser fUser;
    String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imageProfile = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options);
        follower = view.findViewById(R.id.follower);
        following = view.findViewById(R.id.following);
        posts = view.findViewById(R.id.posts);
        bio = view.findViewById(R.id.bio);
        fullname = view.findViewById(R.id.fullname);
        username = view.findViewById(R.id.username);
        myPictures = view.findViewById(R.id.my_pictures);
        savedPictures = view.findViewById(R.id.saved_pictures);
        editProfile = view.findViewById(R.id.edit_profile);

        recyclerView = view.findViewById(R.id.recycle_view_pictures);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        myPostList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), myPostList);
        recyclerView.setAdapter(postAdapter);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                .getString("profileId", "none");

        if(data.equals("none")) {
            profileId = fUser.getUid();
        } else {
            profileId = data;
        }

        userInfo();
        getFollowerAndFollowingCount();
        getPostCount();
        myPhotos();

        if (profileId.equals(fUser.getUid())) {
            editProfile.setText("Chỉnh sửa thông tin cá nhân");
        } else {
            checkFollowingStatus();
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = editProfile.getText().toString();
                if(btnText.equals("Chỉnh sửa thông tin cá nhân")) {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                } else {
                    if(btnText.equals("Theo dõi")) {
                        FirebaseDatabase.getInstance().getReference("Follow").child(fUser.getUid())
                                .child("following").child(profileId).setValue(true);
                        FirebaseDatabase.getInstance().getReference("Follow").child(profileId)
                                .child("followers").child(fUser.getUid()).setValue(true);
                    } else {
                        FirebaseDatabase.getInstance().getReference("Follow").child(fUser.getUid())
                                .child("following").child(profileId).removeValue();
                        FirebaseDatabase.getInstance().getReference("Follow").child(profileId)
                                .child("followers").child(fUser.getUid()).removeValue();
                    }
                }
            }
        });

        return view;

    }

    private void myPhotos() {

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

                Collections.reverse(myPostList);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkFollowingStatus() {

        FirebaseDatabase.getInstance().getReference("Follow").child(fUser.getUid())
                .child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(profileId).exists()) {
                    editProfile.setText("Đang theo dõi");
                } else {
                    editProfile.setText("Theo dõi");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

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
                posts.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getFollowerAndFollowingCount() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Follow").child(profileId);
        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                follower.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfo() {

        FirebaseDatabase.getInstance().getReference("Users")
                .child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if(user.getImageUrl().equals("default")){
                    imageProfile.setImageResource(R.drawable.ic_person);
                } else {
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