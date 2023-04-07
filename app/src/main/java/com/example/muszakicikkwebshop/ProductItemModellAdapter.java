package com.example.muszakicikkwebshop;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;

public class ProductItemModellAdapter extends RecyclerView.Adapter<ProductItemModellAdapter.ViewHolder> implements Filterable {
    private static final String LOG_TAG = ProductItemModellAdapter.class.getName();
    private ArrayList<ProductItemModell> mProductData = new ArrayList<>();

    private ArrayList<ProductItemModell> mProductDataAll = new ArrayList<>();

    private Context mContext;

    private int lastPosition = -1;

    ProductItemModellAdapter(Context context, ArrayList<ProductItemModell> productData){
        this.mProductData = productData;
        this.mProductDataAll = productData;
        this.mContext = context;
    }


    @Override
    public ProductItemModellAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductItemModell currentProduct = mProductData.get(position);
        holder.bindTo(currentProduct);

        if(holder.getAdapterPosition() > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_animation);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mProductData.size();
    }

    @Override
    public Filter getFilter() {

        return searchFilter;
    }
    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<ProductItemModell> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0){
                results.count = mProductDataAll.size();
                results.values = mProductDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(ProductItemModell product : mProductDataAll){
                    if(product.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(product);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mProductData = (ArrayList)filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mTitleText;
        private TextView mInfoText;
        private TextView mPriceText;
        private ImageView mItemImage;
        private RatingBar mRatingBar;

        ViewHolder(View itemView){
            super(itemView);

            mTitleText = itemView.findViewById(R.id.productTitle);
            mInfoText = itemView.findViewById(R.id.leiras);
            mItemImage = itemView.findViewById(R.id.productImage);
            mRatingBar = itemView.findViewById(R.id.starBar);
            mPriceText = itemView.findViewById(R.id.ar);

            itemView.findViewById(R.id.leiras).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(LOG_TAG, "Animation: blink");
                    Animation buttonMoveAnimation = AnimationUtils.loadAnimation(mContext, R.anim.blink_anim);
                    TextView b = (TextView)itemView.findViewById(R.id.leiras);
                    b.startAnimation(buttonMoveAnimation);
                }
            });

            itemView.findViewById(R.id.Kosarba).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(LOG_TAG, "Animation: bounce");
                    Animation buttonMoveAnimation = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
                    Button b = (Button)itemView.findViewById(R.id.Kosarba);
                    b.startAnimation(buttonMoveAnimation);
                    ((MainActivity)mContext).updateAlertIcon();}
            });

        }

        void bindTo(ProductItemModell currentProduct){
            mTitleText.setText(currentProduct.getName());
            mInfoText.setText(currentProduct.getInfo());
            mPriceText.setText(currentProduct.getPrice());
            mRatingBar.setRating(currentProduct.getStarNumber());

            Glide.with(mContext).load(currentProduct.getImageResource()).into(mItemImage);
        }
    }
}
