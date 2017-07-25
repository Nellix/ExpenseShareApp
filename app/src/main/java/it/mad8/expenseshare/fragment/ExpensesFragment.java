package it.mad8.expenseshare.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import it.mad8.expenseshare.ExpenseShareApplication;
import it.mad8.expenseshare.R;
import it.mad8.expenseshare.activity.ExpenseActivity;
import it.mad8.expenseshare.activity.NewExpenseActivity;
//import it.mad8.expenseshare.adapter.ExpensesAdapter;
import it.mad8.expenseshare.model.UserGroupExpenseModel;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.model.datamapper.UserGroupExpenseMapper;
import it.mad8.expenseshare.viewholder.ExpenseViewHolder;

/**
 * A simple {@link Fragment} subclass.
 * <p>
 * Activities that contain this fragment must implement the
 * <p>
 * {@link ExpensesFragment.OnFragmentInteractionListener} interface
 * <p>
 * to handle interaction events.
 * <p>
 * Use the {@link ExpensesFragment#newInstance} factory method to
 * <p>
 * create an instance of this fragment.
 */
public class ExpensesFragment extends Fragment implements ExpenseViewHolder.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String GROUP_ID = "GROUP_ID";
    private static final String USER_MODEL = "USER_MODEL";
    private DatabaseReference groupExpensesDbRef;

    // TODO: Rename and change types of parameters
    private String mGroupId;
    private UserModel mCurrentUserModel;

    private ExpensesFragment currentInstance;
    private FirebaseRecyclerAdapter myAdapter;
    private OnFragmentInteractionListener mListener;

    public ExpensesFragment() {
        // Required empty public constructor
        currentInstance = this;
    }


    /**
     * Use this factory method to create a new instance of
     * <p>
     * this fragment using the provided parameters.
     *
     * @param groupId groupId
     * @param user    userModel.
     * @return A new instance of fragment ExpensesFragment.
     */
    public static ExpensesFragment newInstance(String groupId, UserModel user) {
        ExpensesFragment fragment = new ExpensesFragment();
        Bundle args = new Bundle();
        args.putString(GROUP_ID, groupId);
        args.putSerializable(USER_MODEL, user);
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
        View rootView = inflater.inflate(R.layout.fragment_expenses, container, false);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_expenses);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                Intent i = new Intent(getActivity(), NewExpenseActivity.class);
                i.putExtra(NewExpenseActivity.GROUP_ID, mGroupId);
                i.putExtra(NewExpenseActivity.USER_MODEL, mCurrentUserModel);
                startActivity(i);
            }

        });

        //List<ExpenseModel> myList = DataProvider.getInstance().getExpenses();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        groupExpensesDbRef = FirebaseDatabase.getInstance().getReference()
                .child("users-groups-expenses")
                .child(mCurrentUserModel.getUid())
                .child(mGroupId);
        Query mQuery = groupExpensesDbRef.orderByChild("lastModificationDate");

        RecyclerView recycler = (RecyclerView) rootView.findViewById(R.id.expense_list);
        //recycler.setAdapter(myAdapter);

        myAdapter = new FirebaseRecyclerAdapter<UserGroupExpenseMapper, ExpenseViewHolder>(
                UserGroupExpenseMapper.class, R.layout.list_item_expense, ExpenseViewHolder.class, mQuery) {

            @Override
            protected void populateViewHolder(ExpenseViewHolder viewHolder, UserGroupExpenseMapper data, int position) {
                UserGroupExpenseModel model = data.toModel();
                model.setExpenseId(getRef(position).getKey());
                viewHolder.setData(model);
                viewHolder.setOnClickListener(currentInstance);
            }
        };
        recycler.setAdapter(myAdapter);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layout);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recycler.getContext(),
                layout.getOrientation());
        //dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        recycler.addItemDecoration(dividerItemDecoration);
        return rootView;
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
    public void onClickListener(View v, UserGroupExpenseModel expense) {
        Intent launchExpense = new Intent(getActivity(), ExpenseActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ExpenseActivity.EXPENSE_ID, expense.getExpenseId());
        bundle.putSerializable(ExpenseActivity.USER_MODEL, mCurrentUserModel);
        launchExpense.putExtra("Bundle", bundle);
        getActivity().startActivity(launchExpense);
    }


    /**
     * This interface must be implemented by activities that contain this
     * <p>
     * fragment to allow an interaction in this fragment to be communicated
     * <p>
     * to the activity and potentially other fragments contained in that
     * <p>
     * activity.
     * <p>
     * <p>
     * <p>
     * See the Android Training lesson <a href=
     * <p>
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * <p>
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();
        //myAdapter.notifyDataSetChanged();
    }
}