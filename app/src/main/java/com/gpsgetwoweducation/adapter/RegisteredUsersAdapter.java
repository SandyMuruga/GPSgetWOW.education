package com.gpsgetwoweducation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gpsgetwoweducation.R;
import com.gpsgetwoweducation.pojo.registredmobilenumber.RegisteredMobileNumberData;
import com.gpsgetwoweducation.utilities.SharedPreferenceClass;

import java.util.List;

public class RegisteredUsersAdapter extends RecyclerView.Adapter<RegisteredUsersAdapter.ViewHolder> {
    private List<RegisteredMobileNumberData> registeredMobileNumberUsers;
    private Context context;
    SharedPreferenceClass sharedPreferenceClass;
    private int selectedPosition = -1;

    public RegisteredUsersAdapter(Context context, List<RegisteredMobileNumberData> registeredMobileNumberUsers) {
        this.registeredMobileNumberUsers = registeredMobileNumberUsers;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registered_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (registeredMobileNumberUsers != null && registeredMobileNumberUsers.size() > 0) {
            return registeredMobileNumberUsers.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RadioButton radioButton;
        private AppCompatImageView iv_registered_user_image;
        private AppCompatTextView tv_first_name, tv_last_name;
        private RadioGroup radio_group;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radio_button1);
            tv_first_name = itemView.findViewById(R.id.tv_first_name);
            tv_last_name = itemView.findViewById(R.id.tv_last_name);
            iv_registered_user_image = itemView.findViewById(R.id.iv_registered_user_image);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegisteredMobileNumberData user = registeredMobileNumberUsers.get(position);
        sharedPreferenceClass = new SharedPreferenceClass(context);
        String user_first_name = registeredMobileNumberUsers.get(position).getFirst_name();
        String user_last_name = registeredMobileNumberUsers.get(position).getLast_name();
        String choose_user_id = registeredMobileNumberUsers.get(position).getUser_id();

        sharedPreferenceClass.set("choose_user_id", choose_user_id);

        holder.tv_first_name.setText(user_first_name);
        holder.tv_last_name.setText(user_last_name);

        sharedPreferenceClass.set("user_first_name", user_first_name);
        sharedPreferenceClass.set("user_last_name", user_last_name);

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_launcher_foreground);

        String country_code = sharedPreferenceClass.get("country_code1");
        String customer_id = sharedPreferenceClass.get("customer_id");
        String sub_domain_name = sharedPreferenceClass.get("sub_domain_name");

        if (country_code != null) {
            country_code = country_code.toLowerCase();
        }
        //String imageUrl = "https://cephapi.getster.tech/api/storage/in-1/1.png";
        String imageUrl = "https://cephapi.getster.tech/api/storage/" +
                country_code + "-" +
                customer_id + "/" +
                choose_user_id + ".png";

        Glide.with(context)
                .load(imageUrl)
                .apply(requestOptions)
                .into(holder.iv_registered_user_image);

        holder.radioButton.setChecked(position == selectedPosition);

        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedPosition(holder.getAdapterPosition());
                if (radioButtonClickListener != null) {
                    radioButtonClickListener.onRadioButtonClick(position);
                }
            }
        });
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    public interface OnRadioButtonClickListener {
        void onRadioButtonClick(int position);
    }

    private OnRadioButtonClickListener radioButtonClickListener;

    public void setOnRadioButtonClickListener(OnRadioButtonClickListener listener) {
        radioButtonClickListener = listener;
    }
}
