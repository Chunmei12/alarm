package com.alarmclock.administrator.alarmclock.Adapter;

/**
 * Created by Administrator on 2017/1/4.
 */



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.alarmclock.administrator.alarmclock.Nav_drawer;
import com.alarmclock.administrator.alarmclock.R;
import com.alarmclock.administrator.alarmclock.repeatAlarm;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 作者： 巴掌 on 16/8/19 12:57
 * Github: https://github.com/JeasonWong
 */
public class CommonAdapter extends RecyclerView.Adapter<CommonAdapter.CommonViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private  int Count;
    private List<DataSnapshot> dataSnapshot = new ArrayList<DataSnapshot>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("alarm");
    private final String email;
    private Context context;

    public CommonAdapter(Context context,String email , long count, Iterable<DataSnapshot> dataSnapshot) {

        mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        Count = (int)count;
        this.email = email;
        for (DataSnapshot data:dataSnapshot) {
            this.dataSnapshot.add(data);
        }
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        return new CommonViewHolder(mLayoutInflater.inflate(R.layout.rv_items, parent, false));
    }

    @Override
    public void onBindViewHolder(final CommonViewHolder holder, int position) {
        final DataSnapshot alarmCurrent = this.dataSnapshot.get(position);
        final String path = common.EncodeString(this.email);

        holder.text_switchButton.setText(alarmCurrent.child("hour").getValue().toString()+":"+alarmCurrent.child("minute").getValue().toString());
        holder.text_explanation.setText(getrepeatString(alarmCurrent));
        if(alarmCurrent.child("onOff").getValue().toString().equals("false")){
            holder.switchButton.setChecked(false);
        }else {
            holder.switchButton.setChecked(true);
        }
        holder.switchButton.setTag(position);

        holder.switchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String key = alarmCurrent.getKey();
                if(alarmCurrent.child("onOff").getValue().toString().equals("false")){
                    myRef.child(path).child(key).child("onOff").setValue(true);
                    holder.switchButton.setChecked(true);
                }else {
                    myRef.child(path).child(key).child("onOff").setValue(false);
                    holder.switchButton.setChecked(false);
                }

            }
        });

        holder.text_switchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String key = alarmCurrent.getKey();
                Intent intent = new Intent(context,repeatAlarm.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putSerializable("email",path);
                bundle.putSerializable("key",key);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return Count;
    }
    public String getrepeatString(DataSnapshot alarmCurrent) {
        String str = "";
        boolean everyMonday = Boolean.valueOf(alarmCurrent.child("monday").getValue().toString());
        boolean everyTuesday = Boolean.valueOf(alarmCurrent.child("tuesday").getValue().toString());
        boolean everyWednesday = Boolean.valueOf(alarmCurrent.child("wednesday").getValue().toString());
        boolean everyThursday = Boolean.valueOf(alarmCurrent.child("thursday").getValue().toString());
        boolean everyFriday = Boolean.valueOf(alarmCurrent.child("friday").getValue().toString());
        boolean everySaturday = Boolean.valueOf(alarmCurrent.child("saturday").getValue().toString());
        boolean everySunday = Boolean.valueOf(alarmCurrent.child("sunday").getValue().toString());

        if(everyMonday) {
            str+="Mon |";
        }
        if(everyTuesday){
            str+="Tue |";

        }
        if(everyWednesday){
            str+="Wed |";
        }
        if(everyThursday){
            str+="Thu |";

        }
        if(everyFriday){
            str+="Fri |";
        }
        if(everySaturday){
            str+="Sau |";
        }
        if(everySunday){
            str+="Sun |";
        }
        return str;
    }

    public static class CommonViewHolder extends RecyclerView.ViewHolder {
        public Switch switchButton;
        public TextView text_switchButton;
        public  TextView text_explanation;

        public CommonViewHolder(View itemView) {
            super(itemView);
            switchButton = (Switch) itemView.findViewById(R.id.mySwitch);
            text_switchButton = (TextView) itemView.findViewById((R.id.mySwitch_Text));
            text_explanation = (TextView) itemView.findViewById(R.id.mySwitch_repeat);

        }

    }

}
