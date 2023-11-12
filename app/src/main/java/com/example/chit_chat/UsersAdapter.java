package com.example.chit_chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    List<String> userList;
    String userName;
    Context mContext;

    FirebaseDatabase database;
    DatabaseReference reference;



    public UsersAdapter(List<String> userList, String userName, Context mContext) {
        this.userList = userList;
        this.userName = userName;
        this.mContext = mContext;

        database=FirebaseDatabase.getInstance();
        reference= database.getReference();


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_card,parent,false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        reference.child("Users").child(userList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String otherName= String.valueOf(snapshot.child("userName").getValue());
                String imageUrl= String.valueOf(snapshot.child("image").getValue());

                holder.textViewUsers.setText(otherName);

                if(imageUrl.equals("null"))
                {
                    holder.imageViewUsers.setImageResource(R.drawable.profile_logo2);


                }
                else {
                    Picasso.get().load(imageUrl).into(holder.imageViewUsers);

                }

                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(mContext,MyChatActivity.class);
                        intent.putExtra("userName",userName);
                        intent.putExtra("otherName",otherName);
                        mContext.startActivity(intent);

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
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewUsers;
        private CircleImageView imageViewUsers;
        private CardView cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewUsers=itemView.findViewById(R.id.textviewUserName);
            imageViewUsers=itemView.findViewById(R.id.imageViewUser);
            cardView=itemView.findViewById(R.id.cardview);


        }
    }
}
