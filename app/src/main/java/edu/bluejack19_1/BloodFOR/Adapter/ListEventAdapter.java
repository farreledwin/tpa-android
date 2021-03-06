package edu.bluejack19_1.BloodFOR.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import edu.bluejack19_1.BloodFOR.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.bluejack19_1.BloodFOR.Fragment.DetailFragment;
import edu.bluejack19_1.BloodFOR.Fragment.HomeFragment;
import edu.bluejack19_1.BloodFOR.MainActivity;
import edu.bluejack19_1.BloodFOR.Model.Event;
import edu.bluejack19_1.BloodFOR.interfacs.DataListener;

public class ListEventAdapter extends RecyclerView.Adapter<ListEventAdapter.ListViewHolder> {

    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    private ArrayList<Event> listEvent;
    private Context c;

    public ListEventAdapter(Context c, ArrayList<Event> list) {
        this.listEvent = list;
        this.c = c;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_event, viewGroup, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder holder, int position) {
        final Event event = listEvent.get(position);
        final String picture = event.getEventPicture();
        final String name = event.getEventName();
        final String desc = event.getEventDesc();
        final String location = event.getEventLocation();
        final Date date = event.getEventDate();
        final Double latitude = event.getEventLatitude();
        final Double longitude = event.getEventLongitude();

        FirebaseDatabase getDatabase = FirebaseDatabase.getInstance();
        DatabaseReference getReference = getDatabase.getReference();


        getReference.child("User").child(MainActivity.uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("role").getValue().toString().equals("Member")){
                    holder.update.setVisibility(View.GONE);
                }
              }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Glide.with(holder.itemView.getContext())
                .load(picture)
                .apply(new RequestOptions().override(400, 400))
                .into(holder.eventPhoto);

        holder.event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              DataListener listener = (DataListener) c;
              listener.gotoDetailFragment(event);
            }
        });

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataListener listener = (DataListener) c;
                listener.gotoDelete(event);
            }
        });

        final String dates = formatter.format(date) ;
        holder.eventName.setText(name);
        holder.eventDesc.setText(desc);
        holder.eventLocation.setText(location);
        holder.eventDate.setText(dates);
    }

    @Override
    public int getItemCount() {
        return listEvent.size();
    }

    class ListViewHolder extends RecyclerView.ViewHolder{
        ImageView eventPhoto;
        TextView eventName, eventDesc, eventLocation, eventDate;
        LinearLayout event;
        Button update;

        private ListViewHolder(@NonNull final View itemView) {
            super(itemView);
            event = itemView.findViewById(R.id.item_place);
            eventPhoto = itemView.findViewById(R.id.event_photo);
            eventName = itemView.findViewById(R.id.event_name);
            eventDesc = itemView.findViewById(R.id.event_desc);
            eventLocation = itemView.findViewById(R.id.event_location);
            eventDate = itemView.findViewById(R.id.event_date);
            update = itemView.findViewById(R.id.updateBtn);
        }
    }
}
