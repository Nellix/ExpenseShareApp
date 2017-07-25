package it.mad8.expenseshare.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import it.mad8.expenseshare.ExpenseShareApplication;
import it.mad8.expenseshare.R;
import it.mad8.expenseshare.adapter.ChatAdapter;
import it.mad8.expenseshare.model.MessageChatModel;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.model.datamapper.MessageChatDataMapper;
import it.mad8.expenseshare.model.datamapper.UserDataMapper;
import it.mad8.expenseshare.utils.CalendarUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ChatFragment extends Fragment {
    private static final String USER_MODEL = "USER_MODEL";
    private static final String GROUP_ID = "GROUP_ID";
    private String mGroupId;
    private UserModel mCurrentUserModel;
    private RecyclerView rv_chat;
    private EditText et_messagechat;
    private ImageButton btn_sendMessage;
    private TextView tv_date;
    private LinearLayoutManager mLayoutManager;
    private OnFragmentInteractionListener mListener;
    private List<MessageChatModel> list_message;
    private RecyclerView.Adapter adapter;
    private ChatAdapter chatAdapter;
    private DatabaseReference root;
    private Query query;
    static boolean stateDate;

    public ChatFragment() {
        // Required empty public constructor
    }


    public static ChatFragment newInstance(String groupId, UserModel userModel) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(GROUP_ID, groupId);
        args.putSerializable(USER_MODEL, userModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGroupId = getArguments().getString(GROUP_ID);
            mCurrentUserModel = (UserModel) getArguments().getSerializable(USER_MODEL);
        }
        if (mCurrentUserModel == null) {
            mCurrentUserModel = ((ExpenseShareApplication) getActivity().getApplication()).getUserModel();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        btn_sendMessage = (ImageButton) view.findViewById(R.id.btn_sendMessage);
        rv_chat = (RecyclerView) view.findViewById(R.id.rv_chat);
        et_messagechat = (EditText) view.findViewById(R.id.et_message_chat);
        tv_date = (TextView) view.findViewById(R.id.tv_showDate);

        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setStackFromEnd(true);
        rv_chat.setHasFixedSize(true);
        rv_chat.setLayoutManager(mLayoutManager);

        list_message = new ArrayList<>();

        stateDate = true;

        chatAdapter = new ChatAdapter(getContext(), list_message, tv_date, mGroupId);
        adapter = chatAdapter;
        rv_chat.setAdapter(adapter);


        root = FirebaseDatabase.getInstance().getReference("chat").child(mGroupId);


        btn_sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Task<Void> db = root.push().setValue(new MessageChatDataMapper(et_messagechat.getText().toString(), CalendarUtils.mapCalendarToISOString(Calendar.getInstance()),new UserDataMapper(mCurrentUserModel), mCurrentUserModel.getUid()));
                et_messagechat.setText("");
            }
        });


        query = root.limitToLast(40);
/*
        rv_chat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
              //  super.onScrolled(recyclerView, dx, dy);
                stateDate = false;
                adapter.notifyDataSetChanged();
            }
        });
*/

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                HashMap<String,Object> messageChatDataMapper = (HashMap<String, Object>) dataSnapshot.getValue();
                String message = messageChatDataMapper.get("message").toString();
                Calendar time = CalendarUtils.mapISOStringToCalendar(messageChatDataMapper.get("timestamp").toString());
                String creatorId = messageChatDataMapper.get("creatorId").toString();
                HashMap<String,Object> userDataMapper = (HashMap<String, Object>) messageChatDataMapper.get("creator");
                String username = userDataMapper.get("username").toString();
                String email = userDataMapper.get("email").toString();

                boolean hasImage = (boolean) userDataMapper.get("hasImage");
                UserModel user = new UserModel(username, email,creatorId,hasImage);

                list_message.add(new MessageChatModel(message,time,user,creatorId));



                adapter.notifyDataSetChanged();
                rv_chat.scrollToPosition(adapter.getItemCount() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    static public boolean isStateDate() {
        return stateDate;
    }

    public static void setStateDate(boolean stateDate) {
        ChatFragment.stateDate = stateDate;
    }
}
