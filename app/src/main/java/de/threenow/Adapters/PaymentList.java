//package com.quickrideja.user.Adapters;
//
//
//import android.annotation.SuppressLint;
//        import android.content.Context;
//
//        import android.util.Log;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.ViewGroup;
//        import android.widget.ImageView;
//        import android.widget.LinearLayout;
//        import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.quickrideja.user.Models.CardDetails;
//import com.quickrideja.user.R;
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//
//
//public class PaymentList extends RecyclerView.Adapter<PaymentList.ViewHolder> {
//
//    private ArrayList<CardDetails> mData;
//    private LayoutInflater mInflater;
//
//    private Context context;
//    private myCartPage myCartPage;
//
//
//    // data is passed into the constructor
//    public PaymentList(Context context, ArrayList<CardDetails> mData) {
//        this.mInflater = LayoutInflater.from(context);
//        this.mData = mData;
//        this.context = context;
//
//    }
//
//    public void setOnItemClick(myCartPage myCartPage) {
//        this.myCartPage = myCartPage;
//    }
//
//    // inflates the cell layout from xml when needed
//    @Override
//    public PaymentList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        View view = mInflater.inflate(R.layout.layout_strpi_card_layout, parent, false);
//        PaymentList.ViewHolder viewHolder = new PaymentList.ViewHolder(view);
//
//        return viewHolder;
//    }
//
//    // binds the data to the textview in each cell
//    @SuppressLint("SetTextI18n")
//    @Override
//    public void onBindViewHolder(PaymentList.ViewHolder holder, int position) {
//        CardDetails cardDetails = cardDetailsList.get(position);
//        try {
//            if(cardDetails.getBrand().equalsIgnoreCase("MASTERCARD")){
//                holder.paymentTypeImg.setImageResource(R.drawable.credit_card);
//            }else if(cardDetails.getBrand().equalsIgnoreCase("MASTRO")){
//                holder.paymentTypeImg.setImageResource(R.drawable.visa_payment_icon);
//            }else if(cardDetails.getBrand().equalsIgnoreCase("Visa")){
//                holder.paymentTypeImg.setImageResource(R.drawable.visa);
//            }
//            holder.cardNumber.setText("xxxx - xxxx - xxxx - "+cardDetailsList.get(position).getLast_four());
//
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        holder.mAddItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                myCartPage.mAddCart(id);
//            }
//        });
//
//        holder.remove_Item.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                myCartPage.mRemoveCart(id);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return mData == null ? 0 : mData.size();
//    }
//
//
//    public interface myCartPage {
//
//        void mAddCart(String id);
//
//        void mRemoveCart(String id);
//
//    }
//
//
//    // stores and recycles views as they are scrolled off screen
//    public class ViewHolder extends RecyclerView.ViewHolder{
//        public TextView mName, price, mOldprice, mAddItem, remove_Item;
//        public ImageView mImage;
//        public LinearLayout mylist;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            mName = (TextView) itemView.findViewById(R.id.productName);
//            mOldprice = (TextView) itemView.findViewById(R.id.oldprice);
//            price = (TextView) itemView.findViewById(R.id.price);
//            mylist = itemView.findViewById(R.id.listlayout);
//            mImage = (ImageView) itemView.findViewById(R.id.productImg);
//            mAddItem = itemView.findViewById(R.id.addItem);
//            remove_Item = itemView.findViewById(R.id.removeItem);
//            itemView.setOnClickListener(this);
//        }
//    }
//
//}