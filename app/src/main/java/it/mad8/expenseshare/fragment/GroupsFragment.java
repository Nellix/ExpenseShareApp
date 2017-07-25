package it.mad8.expenseshare.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import it.mad8.expenseshare.ExpenseShareApplication;
import it.mad8.expenseshare.R;
import it.mad8.expenseshare.activity.GroupActivity;
import it.mad8.expenseshare.activity.NewGroupActivity;
//import it.mad8.expenseshare.adapter.GroupsAdapter;
import it.mad8.expenseshare.model.GroupModel;
import it.mad8.expenseshare.model.UserGroupModel;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.model.datamapper.UserGroupDataMapper;
import it.mad8.expenseshare.viewholder.GroupViewHolder;


public class GroupsFragment extends Fragment implements GroupViewHolder.OnClickListener {
    private static final String TAG = "GroupsFragment";
    private static final String USER_MODEL = "USER_MODEL";
    private OnFragmentInteractionListener mListener;

    private FirebaseRecyclerAdapter myAdapter;
    private DatabaseReference groupsDbRef;
    private UserModel currentUserModel;

    private RecyclerView recycler;
    private ProgressBar pb_loadGroups;
    private TextView tv_noGroups;

    private GroupsFragment currentInstance;


    public GroupsFragment() {
        // Required empty public constructor
        currentInstance = this;
    }

    // TODO: Rename and change types and number of parameters
    public static GroupsFragment newInstance(UserModel currentUserModel) {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        args.putSerializable(USER_MODEL, currentUserModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ExpenseShareApplication app = (ExpenseShareApplication) getActivity().getApplication();
        if (savedInstanceState == null) {
            if (app.getUserModel() == null) {
                currentUserModel = app.getUserModel();
            } else {
                Bundle extras = getArguments();
                if (extras != null) {
                    currentUserModel = (UserModel) extras.getSerializable(USER_MODEL);
                    app.setUserModel(currentUserModel);
                }
            }
        } else {
            currentUserModel = (UserModel) savedInstanceState.getSerializable(USER_MODEL);
            app.setUserModel(currentUserModel);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(USER_MODEL, currentUserModel);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);

        tv_noGroups = (TextView) rootView.findViewById(R.id.no_groups);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_groups);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewGroupActivity.class);
                intent.putExtra(NewGroupActivity.USER_MODEL, currentUserModel);
                startActivity(intent);

            }
        });

        groupsDbRef = FirebaseDatabase.getInstance().getReference().child(UserGroupDataMapper.USERS_GROUPS).child(currentUserModel.getUid());

        Query mQuery = groupsDbRef.orderByChild("lastModificationDate");
        mQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "data changed");
                if (!dataSnapshot.exists()) {
                    pb_loadGroups.setVisibility(View.GONE);
                    tv_noGroups.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "cancelled");
                //TODO: show error
                pb_loadGroups.setVisibility(View.GONE);
            }
        });


        recycler = (RecyclerView) rootView.findViewById(R.id.group_list);
        myAdapter = new FirebaseRecyclerAdapter<UserGroupDataMapper, GroupViewHolder>(
                UserGroupDataMapper.class, R.layout.list_item_group, GroupViewHolder.class, mQuery) {

            @Override
            protected void populateViewHolder(GroupViewHolder viewHolder, UserGroupDataMapper mapper, int position) {
                UserGroupModel model = mapper.toModel();
                model.setGroupId(getRef(position).getKey());
                viewHolder.setData(model);
                viewHolder.setOnClickListener(currentInstance);
                pb_loadGroups.setVisibility(View.GONE);
                tv_noGroups.setVisibility(View.GONE);
            }
        };


        recycler.setAdapter(myAdapter);
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        layout.getOrientation();
        layout.setReverseLayout(true);
        layout.setStackFromEnd(true);
        recycler.setLayoutManager(layout);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recycler.getContext(),
                layout.getOrientation());
        //dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        recycler.addItemDecoration(dividerItemDecoration);

        pb_loadGroups = (ProgressBar) rootView.findViewById(R.id.pb_loadGroups);
        pb_loadGroups.setVisibility(View.VISIBLE);

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
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

    @Override
    public void onClick(View v, UserGroupModel model) {

        Intent launchExpenseList = new Intent(this.getContext(), GroupActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("GROUP_NAME", model.getName());
        bundle.putString("GROUP_ID", model.getGroupId());
        bundle.putSerializable(GroupActivity.USER_MODEL, currentUserModel);

        launchExpenseList.putExtras(bundle);
        getContext().startActivity(launchExpenseList);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
