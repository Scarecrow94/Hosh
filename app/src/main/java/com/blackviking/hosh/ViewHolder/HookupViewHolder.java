package com.blackviking.hosh.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.blackviking.hosh.Interface.ItemClickListener;
import com.blackviking.hosh.R;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;

/**
 * Created by Scarecrow on 2/5/2019.
 */

public class HookupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private ItemClickListener itemClickListener;
    public ImageView hookUpImage, onlineIndicator;
    public RoundKornerRelativeLayout theLayout, myLayout;

    public HookupViewHolder(View itemView) {
        super(itemView);

        hookUpImage = (ImageView)itemView.findViewById(R.id.hookupImage);
        onlineIndicator = (ImageView)itemView.findViewById(R.id.onlineIndicator);
        theLayout = (RoundKornerRelativeLayout) itemView.findViewById(R.id.hookupsLayout);
        myLayout = (RoundKornerRelativeLayout) itemView.findViewById(R.id.myLayout);

        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);

    }

}
