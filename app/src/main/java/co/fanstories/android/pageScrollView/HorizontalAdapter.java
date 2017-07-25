package co.fanstories.android.pageScrollView;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import co.fanstories.android.R;

/**
 * Created by Jaseem on 7/21/17.
 */

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.IconViewHolder> {


    List<Icon> horizontalList = Collections.emptyList();
    Context context;
    SelectedPageInterface selectedPageInterface;
    int selectedPos;


    public HorizontalAdapter(List<Icon> horizontalList, Context context, SelectedPageInterface selectedPageInterface) {
        this.horizontalList = horizontalList;
        this.context = context;
        this.selectedPageInterface = selectedPageInterface;
    }


    public class IconViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout iconView;
        //ImageView imageView;
        TextView txtview;

        public IconViewHolder(View view) {
            super(view);
            //imageView = (ImageView) view.findViewById(R.id.imageview);
            txtview = (TextView) view.findViewById(R.id.txtview);
            iconView = (RelativeLayout) view.findViewById(R.id.iconView);
        }
    }

    @Override
    public IconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_item, parent, false);

        return new IconViewHolder(itemView);
    }

    /** Set individual icon contents, handle highlights on click **/
    @Override
    public void onBindViewHolder(final IconViewHolder holder, final int position) {
        //holder.imageView.setImageResource(horizontalList.get(position).imageId);
        holder.txtview.setText(horizontalList.get(position).txt);

        if (selectedPos == position) {
            holder.iconView.setBackgroundColor(context.getResources().getColor(R.color.channelScrollSelect));
        }

        else {
            holder.iconView.setBackgroundColor(Color.parseColor("#40000000"));
        }

        holder.iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPageInterface.setSelectedPage(holder.txtview.getText().toString());
                notifyDataSetChanged();
                selectedPos = position;
            }
        });


    }

    @Override
    public int getItemCount()
    {
        return horizontalList.size();
    }
}
