package com.example.notepad.adapter;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.example.notepad.R;
import com.example.notepad.bean.NoteBean;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private Context context;
    private List<NoteBean> list;
    private ItemClickListener itemClickListener;
    private SwipeRevealLayout openSwipeLayout;

    public NoteAdapter(Context context, ItemClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    public void setData(List<NoteBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public SwipeRevealLayout getOpenSwipeLayout() {
        return openSwipeLayout;
    }

    public void setOpenSwipeLayout(SwipeRevealLayout openSwipeLayout) {
        this.openSwipeLayout = openSwipeLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NoteBean note = list.get(position);
        holder.tv_title.setText(note.getTitle());
        holder.tv_content.setText(note.getContent());
        holder.tv_time.setText(note.getTime());

        holder.ll_item.setOnClickListener(v -> {
            if (openSwipeLayout != null && openSwipeLayout != holder.swipeRevealLayout) {
                openSwipeLayout.close(true);
            }
            itemClickListener.onItemClick(v, position);
        });

        holder.ll_item.setOnLongClickListener(v -> {
            if (openSwipeLayout != null && openSwipeLayout != holder.swipeRevealLayout) {
                openSwipeLayout.close(true);
            }
            itemClickListener.onItemLongClick(v, position);
            return true;
        });

        holder.btn_delete.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemDelete(position);
            }
        });

        holder.swipeRevealLayout.setSwipeListener(new SwipeRevealLayout.SwipeListener() {
            @Override
            public void onClosed(SwipeRevealLayout view) {
                if (openSwipeLayout == holder.swipeRevealLayout) {
                    openSwipeLayout = null;
                }
                holder.btn_delete.setColorFilter(Color.parseColor("#FFCDD2"));
            }

            @Override
            public void onOpened(SwipeRevealLayout view) {
                if (openSwipeLayout != null && openSwipeLayout != holder.swipeRevealLayout) {
                    openSwipeLayout.close(true);
                }
                openSwipeLayout = holder.swipeRevealLayout;
                ObjectAnimator colorAnim = ObjectAnimator.ofInt(holder.btn_delete, "colorFilter",
                        Color.parseColor("#FFCDD2"), Color.parseColor("#D32F2F"));
                colorAnim.setDuration(300);
                colorAnim.setEvaluator(new ArgbEvaluator());
                colorAnim.start();
            }

            @Override
            public void onSlide(SwipeRevealLayout view, float slideOffset) {
                // Optional: Handle slide event
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_content, tv_time;
        LinearLayout ll_item;
        ImageView btn_delete;
        SwipeRevealLayout swipeRevealLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_time = itemView.findViewById(R.id.tv_time);
            ll_item = itemView.findViewById(R.id.ll_item);
            btn_delete = itemView.findViewById(R.id.btn_delete);
            swipeRevealLayout = itemView.findViewById(R.id.swipeRevealLayout);
        }
    }

    public interface ItemClickListener {
        void onItemClick(View v, int position);

        void onItemLongClick(View v, int position);

        void onItemDelete(int position);
    }
}