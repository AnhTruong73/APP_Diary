package com.example.finalproject_diaryapp.fragment;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject_diaryapp.Diary;
import com.example.finalproject_diaryapp.DiaryAdapter;
import com.example.finalproject_diaryapp.R;
import com.example.finalproject_diaryapp.my_interface.IClickDeleteItemListener;
import com.example.finalproject_diaryapp.my_interface.IClickItemDiaryListener;
import com.example.finalproject_diaryapp.my_interface.ILongClickItemDiaryListener;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {
    private View mView;
    private RecyclerView rcvDiary;
    private DiaryAdapter mDiaryAdapter;
    private List<Diary> mListDiary;
    private SearchView searchView;
    private LinearLayout bottomAdd;
    private String nameUser;
    private LinearLayout rootView;




    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView= inflater.inflate(R.layout.activity_home_fragment,container,false);
        initUi();

        getListDiaryFromRealtimeDatabase();



        return mView;
    }
    private void initUi(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        nameUser = user.getEmail();
        nameUser = nameUser.replace('.',' ');

        rcvDiary = mView.findViewById(R.id.rcv_diary);
        rootView = mView.findViewById(R.id.root_view);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvDiary.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        rcvDiary.addItemDecoration(dividerItemDecoration);

        mListDiary = new ArrayList<>();
        mDiaryAdapter= new DiaryAdapter(mListDiary, new IClickItemDiaryListener() {
            @Override
            public void onClickItemDiary(Diary diary) {
                openDialogUpdateItem(diary);
            }
        }, new ILongClickItemDiaryListener() {
            @Override
            public void onLongClickItemDiary(Diary diary) {
                openDialogCheckLockItem(diary);
            }
        }, new IClickDeleteItemListener() {
            @Override
            public void onClickDeleteItemDiary(Diary diary) {
                openDialogDeleteItem(diary);
            }
        });

        rcvDiary.setAdapter(mDiaryAdapter);


        bottomAdd =mView.findViewById(R.id.bottom_add);
        bottomAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogAddItem();
            }
        });
    }

    private void openDialogCheckLockItem(Diary diary) {
        if (diary.getTypeDisplay().equals("empty")){
            openDialogLockItem(diary);
        }
        else{
            openDialogUnLockItem(diary);
        }
    }
    private void openDialogLockItem(Diary diary){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_lock);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        EditText edtLockPass = dialog.findViewById(R.id.edt_lock_pass);
        EditText edtConfirmPass = dialog.findViewById(R.id.edt_confirm_pass);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnLock = dialog.findViewById(R.id.btn_lock);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database= FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(nameUser);

                String lockPass= edtLockPass.getText().toString().trim();
                String confirmPass = edtConfirmPass.getText().toString().trim();

                if (lockPass.equals(confirmPass)){
                    diary.setTypeDisplay(lockPass);
                    myRef.child(String.valueOf(diary.getId())).updateChildren(diary.toMap(), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            Toast.makeText(getActivity(), "ĐÃ KHÓA THÀNH CÔNG", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }
                else if (lockPass.equals("")){
                    Toast.makeText(getActivity(), "Không được bỏ trống", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "MẬT KHẨU KHÔNG TRÙNG KHỚP", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.show();
    }

    private void openDialogUnLockItem(Diary diary) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_unlock);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        EditText edtUnLockPass = dialog.findViewById(R.id.edt_unlock_pass);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnUnLock = dialog.findViewById(R.id.btn_unlock);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnUnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database= FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(nameUser);

                String unLockPass= edtUnLockPass.getText().toString().trim();

                if (unLockPass.equals(diary.getTypeDisplay())){
                    diary.setTypeDisplay("empty");
                    myRef.child(String.valueOf(diary.getId())).updateChildren(diary.toMap(), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            Toast.makeText(getActivity(), "ĐÃ MỞ KHÓA THÀNH CÔNG", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }
                else {
                    Toast.makeText(getActivity(), "SAI MẬT KHẨU UNLOCK", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.show();
    }


    private void openDialogAddItem() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_add);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        EditText edtAddTitle = dialog.findViewById(R.id.edt_add_title);
        EditText edtAddContent = dialog.findViewById(R.id.edt_add_content);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnAdd = dialog.findViewById(R.id.btn_add);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database= FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(nameUser);

                String id = myRef.push().getKey();
                String titleAdd=edtAddTitle.getText().toString().trim();
                String contentAdd=edtAddContent.getText().toString().trim();
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String dateAdd = simpleDateFormat.format(date);
                String typeDisplay = "empty";
                Diary diary = new Diary(id,titleAdd,dateAdd,contentAdd,typeDisplay);

                String partObject = String.valueOf(diary.getId());
                myRef.child(partObject).setValue(diary, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(getActivity(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }



    public void openDialogDeleteItem(Diary diary) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_delete);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnDelete = dialog.findViewById(R.id.btn_delete);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(nameUser);
                String idDiaryDelete = diary.getId();
                int indexDelete = mListDiary.indexOf(diary);
                myRef.child(String.valueOf(diary.getId())).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Snackbar snackbar = Snackbar.make(rootView, " Nội dung đã bị xóa !",Snackbar.LENGTH_SHORT);
                        snackbar.setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDiaryAdapter.undoItem(nameUser ,diary,idDiaryDelete);
                                if (indexDelete==0 || indexDelete == mListDiary.size()-1){
                                    rcvDiary.scrollToPosition(indexDelete);
                                }
                            }
                        });
                        snackbar.setActionTextColor(Color.YELLOW);
                        snackbar.show();
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }

    private void openDialogUpdateItem(Diary diary) {
        if (diary.getTypeDisplay().equals("empty")){
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_dialog_update);
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);

            TextView tvDate = dialog.findViewById(R.id.tv_date);
            EditText edtUpdateTitle = dialog.findViewById(R.id.edt_update_title);
            EditText edtUpdateContent = dialog.findViewById(R.id.edt_update_content);
            Button btnCancel = dialog.findViewById(R.id.btn_cancel);
            Button btnUpdate = dialog.findViewById(R.id.btn_update);

            tvDate.setText(diary.getDate());
            edtUpdateTitle.setText(diary.getTitle());
            edtUpdateContent.setText(diary.getContent());

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference(nameUser);

                    String newTitle = edtUpdateTitle.getText().toString().trim();
                    String newContent = edtUpdateContent.getText().toString().trim();
                    Date date = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String newDate = simpleDateFormat.format(date);
                    diary.setTitle(newTitle);
                    diary.setContent(newContent);
                    diary.setDate(newDate);
                    myRef.child(String.valueOf(diary.getId())).updateChildren(diary.toMap(), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            Toast.makeText(getActivity(), "ĐÃ THAY ĐỔI THÀNH CÔNG", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }
            });

            dialog.show();
        }
        else{
            Toast.makeText(getActivity(), "Vui lòng mở khóa", Toast.LENGTH_SHORT).show();
        }

    }

    private void getListDiaryFromRealtimeDatabase(){

        FirebaseDatabase database= FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(nameUser);

        Query query = myRef.orderByChild("date");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Diary d = snapshot.getValue(Diary.class);
                if (d!=null){
                    mListDiary.add(0,d);
                    mDiaryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Diary d = snapshot.getValue(Diary.class);
                if(d==null ||mListDiary==null ||mListDiary.isEmpty()){
                    return;
                }
                for(int i =0;i< mListDiary.size();i++){
                    if (d.getId()==mListDiary.get(i).getId()){
                        mListDiary.set(i,d);
                    }
                }
                mDiaryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Diary d = snapshot.getValue(Diary.class);
                if(d==null ||mListDiary==null ||mListDiary.isEmpty()){
                    return;
                }
                for(int i =0;i< mListDiary.size();i++){
                    if (d.getId()==mListDiary.get(i).getId()){
                        mListDiary.remove(mListDiary.get(i));
                        break;
                    }
                }
                mDiaryAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState ){
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mDiaryAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mDiaryAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

}