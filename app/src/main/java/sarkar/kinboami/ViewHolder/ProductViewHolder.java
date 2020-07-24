package sarkar.kinboami.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import sarkar.kinboami.Interface.ItemClickListener;
import sarkar.kinboami.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView product_name,product_price;
    public ImageView product_image;
    ItemClickListener listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        product_name = itemView.findViewById(R.id.product_name);
        product_price = itemView.findViewById(R.id.product_price);
        product_image = itemView.findViewById(R.id.product_image);
    }
    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
          listener.onClick(view, getAdapterPosition(), false);
    }
}
