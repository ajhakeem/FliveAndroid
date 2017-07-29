package co.fanstories.android.pageScrollView;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Jaseem on 7/29/17.
 */

public class IconItemDecoration extends RecyclerView.ItemDecoration {

    private int gapSpace;

    public IconItemDecoration(int space) {
        this.gapSpace = space;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = gapSpace;
        outRect.right = gapSpace;
    }
}
