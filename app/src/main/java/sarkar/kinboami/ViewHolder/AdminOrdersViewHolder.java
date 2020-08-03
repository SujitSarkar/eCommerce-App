package sarkar.kinboami.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import sarkar.kinboami.Interface.ItemClickListener;
import sarkar.kinboami.R;

public class AdminOrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView order_user_name,order_user_phone,total_price,user_city,user_dist,user_region,user_house,user_postal,order_time_date;
    public Button show_product_btn,confirm_btn;
    private ItemClickListener listener;

    public AdminOrdersViewHolder(@NonNull View itemView) {
        super(itemView);

        order_user_name = itemView.findViewById(R.id.order_user_name);
        order_user_phone = itemView.findViewById(R.id.order_user_phone);
        total_price = itemView.findViewById(R.id.total_price);
        user_city = itemView.findViewById(R.id.user_city);
        user_dist = itemView.findViewById(R.id.user_dist);
        user_region = itemView.findViewById(R.id.user_region);
        user_house = itemView.findViewById(R.id.user_house);
        user_postal = itemView.findViewById(R.id.user_postal);
        order_time_date = itemView.findViewById(R.id.order_date_time);

        show_product_btn = itemView.findViewById(R.id.show_product_btn);
        confirm_btn = itemView.findViewById(R.id.confirm_btn);
    }
    @Override
    public void onClick(View view) {
        listener.onClick(view, getAdapterPosition(), false);
    }
    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

}
