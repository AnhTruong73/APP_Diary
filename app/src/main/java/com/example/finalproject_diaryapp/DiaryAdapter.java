package com.example.finalproject_diaryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.finalproject_diaryapp.fragment.HomeFragment;
import com.example.finalproject_diaryapp.my_interface.IClickDeleteItemListener;
import com.example.finalproject_diaryapp.my_interface.IClickItemDiaryListener;
import com.example.finalproject_diaryapp.my_interface.ILongClickItemDiaryListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> implements Filterable {

    private List<Diary> mListDiary;
    private List<Diary> mListDiaryOld;
    private IClickItemDiaryListener iClickItemDiaryListener;
    private IClickDeleteItemListener iClickDeleteItemListener;
    private ILongClickItemDiaryListener iLongClickItemDiaryListener;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();


    public DiaryAdapter(List<Diary> mListDiary,IClickItemDiaryListener listener,ILongClickItemDiaryListener longlistener,IClickDeleteItemListener deletelistener) {
        this.mListDiary = mListDiary;
        this.mListDiaryOld=mListDiary;
        this.iClickItemDiaryListener = listener;
        this.iLongClickItemDiaryListener = longlistener;
        this.iClickDeleteItemListener = deletelistener;
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diary,parent,false);
        return new DiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        Diary diary = mListDiary.get(position);
        if (diary ==null){
            return;
        }
        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(holder.swipeRevealLayout,diary.getId());
        viewBinderHelper.closeLayout(diary.getId());


        holder.tvTitle.setText(diary.getTitle());
        holder.tvDate.setText(diary.getDate());
        holder.tvContent.setText(diary.getContent());
        if (!diary.getTypeDisplay().equals("empty")){
            holder.tvTitle.setText("Nội dung đã bị ẩn về "+ diary.getTitle());
            holder.tvDate.setText(diary.getDate());
            holder.tvContent.setText("Nội dung đã bị ẩn");
        }
        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickItemDiaryListener.onClickItemDiary(diary);
            }
        });
        holder.layoutItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                iLongClickItemDiaryListener.onLongClickItemDiary(diary);
                return true;
            }
        });
        holder.layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickDeleteItemListener.onClickDeleteItemDiary(diary);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListDiary!=null){
            return mListDiary.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                if (strSearch.isEmpty()){
                    mListDiary=mListDiaryOld;
                }
                else {
                    List<Diary>list = new ArrayList<>();
                    for (Diary diary: mListDiaryOld){
                        if (diary.getTitle().toLowerCase().contains(strSearch.toLowerCase())){
                            list.add(diary);
                        }
                    }
                    mListDiary =list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values=mListDiary;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mListDiary = (List<Diary>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class DiaryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate,tvTitle,tvContent;
        LinearLayout layoutItem;
        private SwipeRevealLayout swipeRevealLayout;
        LinearLayout layoutDelete;
        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            layoutItem = itemView.findViewById(R.id.item_layout);

            layoutDelete = itemView.findViewById(R.id.layout_delete);
            swipeRevealLayout = itemView.findViewById(R.id.swipeRevealLayout);
        }
    }

    public void undoItem(String nameUser, Diary diary, String idDiary){
        FirebaseDatabase database= FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(nameUser);

        myRef.child(idDiary).setValue(diary, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
            }
        });
    }
}
