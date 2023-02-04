package com.walker.poly.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.walker.poly.Model.Post;
import com.walker.poly.Model.User;
import com.walker.poly.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter  extends  RecyclerView.Adapter<PostAdapter.ViewHolder>{
    public Context mContext;
    public List<Post> mPostList;

    public PostAdapter(Context mContext, List<Post> mPostList) {
        this.mContext = mContext;
        this.mPostList = mPostList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.questions_retrived_layout,parent,false);

        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final  Post post = mPostList.get(position);
        if (post.getQuestionimage() == null){
        holder.qsimage.setVisibility(View.GONE);

        }
        holder.qsimage.setVisibility(View.VISIBLE);

        Glide.with(mContext).load(post.getQuestionimage()).into(holder.qsimage);
        holder.expandable_text.setText(post.getQuestion());
        holder.topicTextview.setText(post.getTopic());
        holder.askedonTextview.setText(post.getDate());
        publisherInformation(holder.publisher_profile,holder.asked_by_textview,post.getPublisher());



    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    public  class  ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView publisher_profile;
        public TextView asked_by_textview,Likes,Disikes,comments;
        public ImageView qsimage ,more,like,dislike,comment,save;
        public  TextView topicTextview,askedonTextview;
        private ExpandableTextView expandable_text ;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            publisher_profile = itemView.findViewById(R.id.publisher_profile);
            asked_by_textview = itemView.findViewById(R.id.asked_by_textview);
            Likes = itemView.findViewById(R.id.likes);
            Disikes = itemView.findViewById(R.id.dislikes);
            comments=itemView.findViewById(R.id.comments);
            more = itemView.findViewById(R.id.more);
            like = itemView.findViewById(R.id.like);
            dislike=itemView.findViewById(R.id.dislike);
            comment=itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            qsimage = itemView.findViewById(R.id.qsimage);
            topicTextview = itemView.findViewById(R.id.topicTextview);
            askedonTextview= itemView.findViewById(R.id.askedonTextview);
            expandable_text = itemView.findViewById(R.id.expand_text_view);









        }
    }

    private  void  publisherInformation(CircleImageView publisher_profile , TextView askedBy , String userid ){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(userid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getProfileimageurl()).into(publisher_profile);
                askedBy.setText(user.getFullname());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });




    }


}
