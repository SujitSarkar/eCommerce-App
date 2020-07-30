package sarkar.kinboami.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import sarkar.kinboami.Interface.ItemClickListener;
import sarkar.kinboami.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
     public TextView cart_product_name,cart_product_qtty,cart_product_price;
     public ImageView cart_product_image;

     private ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        cart_product_name = itemView.findViewById(R.id.cart_product_name);
        cart_product_qtty = itemView.findViewById(R.id.cart_product_qtty);
        cart_product_price = itemView.findViewById(R.id.cart_product_price);
        cart_product_image = itemView.findViewById(R.id.cart_product_image);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
