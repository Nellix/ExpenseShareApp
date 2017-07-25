package it.mad8.expenseshare.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import it.mad8.expenseshare.R;
import it.mad8.expenseshare.activity.ExpenseActivity;
import it.mad8.expenseshare.activity.NewProposalActivity;
import it.mad8.expenseshare.adapter.ProposalsAdapter;
import it.mad8.expenseshare.model.ExpenseModel;
import it.mad8.expenseshare.model.ProposalModel;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.model.WaitingProposalModel;
import it.mad8.expenseshare.model.datamapper.ExpenseDataMapper;
import it.mad8.expenseshare.model.datamapper.ProposalDataMapper;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProposalFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProposalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProposalFragment extends Fragment implements ProposalsAdapter.OnVoteClickListener, ProposalsAdapter.OnSelectionListener {

    public static final String EXPENSE = "expense";
    public static final String USER_MODEL = "USER_MODEL";
    public static final String EXPENSE_ID = "EXPENSE_ID";

    static final int NEW_PROPOSAL = 1;

    private RecyclerView recycler;
    private ProposalsAdapter adapter;
    private ImageButton ib_add;
    private ExpenseModel expense;
    private TextView tv_no_proposal;
    //private TextView tv_deadline;

    private UserModel currentUserModel;

    private OnFragmentInteractionListener mListener;

    public static ProposalFragment newInstance(ExpenseModel e, UserModel currentUserModel) {
        ProposalFragment fragment = new ProposalFragment();
        Bundle args = new Bundle();



        args.putSerializable(EXPENSE, e);
        args.putSerializable(USER_MODEL, currentUserModel);
        fragment.setArguments(args);

        return fragment;
    }

    public ProposalFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            expense = (ExpenseModel) getArguments().getSerializable(EXPENSE);
            currentUserModel = (UserModel) getArguments().getSerializable(USER_MODEL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_proposal, container, false);
        //if()
        //exp_list = (ExpandableListView) view.findViewById(R.id.expandableListView);
        recycler = (RecyclerView) view.findViewById(R.id.expandableListView);
        ib_add = (ImageButton) view.findViewById(R.id.ib_addProposta);
        //tv_deadline = (TextView) view.findViewById(R.id.tv_deadline);
        tv_no_proposal = (TextView) view.findViewById(R.id.tv_no_proposal);

        //exp_list.setAdapter(items_adapter);

        if (expense.getProposals() != null) {
            if (expense.getProposals().size() > 0) {
                tv_no_proposal.setVisibility(View.GONE);
            }
            adapter = new ProposalsAdapter(expense.getId(), expense.getProposals(), currentUserModel, expense.getStatus() != ExpenseModel.ExpenseStatus.PROPOSAL);
            if (expense.getStatus() == ExpenseModel.ExpenseStatus.PROPOSAL) {
                adapter.setVoteClickListener(this);
                adapter.setSelectionListener(this);

            }
            recycler.setAdapter(adapter);
            LinearLayoutManager layout = new LinearLayoutManager(getContext());
            recycler.setLayoutManager(layout);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recycler.getContext(),
                    layout.getOrientation());
            recycler.addItemDecoration(dividerItemDecoration);
        }

        ib_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), NewProposalActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(NewProposalActivity.MEMBERS_LIST, expense.getUsers());
                bundle.putSerializable(EXPENSE_ID, expense.getId());
                intent.putExtras(bundle);
                startActivityForResult(intent, NEW_PROPOSAL, null);

                //    adapter.notifyDataSetChanged();
            }
        });


        /*CountDownTimer timer = new CountDownTimer(( expense.getDeadline()), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_deadline.setText(String.valueOf((int) millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                tv_deadline.setText(R.string.no_deadline);
            }
        };
        timer.start();*/

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_PROPOSAL) {
            if (resultCode == RESULT_OK) {
                final ProposalModel newProposal = (ProposalModel) data.getExtras().getSerializable(NewProposalActivity.PROPOSAL);
                if (newProposal != null) {

                    final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
                            .child("expenses")
                            .child(expense.getId())
                            .child("proposals");
                    final String pid = dbRef.push().getKey();

                    if (newProposal.isHasImage()) {
                        Bitmap bitmap = data.getExtras().getParcelable(NewProposalActivity.PROPOSAL_IMAGE);
                        if (bitmap != null) {
                            StorageReference imageRef = FirebaseStorage.getInstance()
                                    .getReference(ExpenseDataMapper.PROPOSAL_PATH.replace("{eid}", expense.getId()).replace("{pid}", pid));
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte[] imageInByte = stream.toByteArray();
                            ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
                            final ProgressDialog dial = new ProgressDialog(getContext());
                            dial.show();
                            UploadTask uploadTask = imageRef.putStream(bis);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    dial.dismiss();
                                    if (!task.isSuccessful()) {
                                        newProposal.setHasImage(false);
                                    }
                                    dbRef.child(pid).setValue(new ProposalDataMapper(newProposal));
                                    newProposal.setId(pid);
                                    expense.getProposals().add(newProposal);
                                    //newProposalList.add(new ProposalDataMapper(newProposal));
                                    adapter.notifyItemInserted(expense.getProposals().size() - 1);
                                    tv_no_proposal.setVisibility(View.GONE);
                                }
                            });
                            return;
                        } else {
                            newProposal.setHasImage(false);
                        }
                    }
                    dbRef.child(pid).setValue(new ProposalDataMapper(newProposal));
                    newProposal.setId(pid);
                    expense.getProposals().add(newProposal);
                    //newProposalList.add(new ProposalDataMapper(newProposal));
                    adapter.notifyItemInserted(expense.getProposals().size() - 1);
                    tv_no_proposal.setVisibility(View.GONE);
                }
            }

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    public void updateActivity() {
        if (mListener != null) {
            mListener.onFragmentInteraction(expense);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ExpenseActivity a;

        if (context instanceof ExpenseActivity) {
            a = (ExpenseActivity) context;
            mListener = a;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onLikeClick(ProposalModel proposal, int position) {
        proposal.getUsers().put(currentUserModel.getUid(), 1);
        updateProposal(proposal);
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onDislikeClick(ProposalModel proposal, int position) {
        proposal.getUsers().put(currentUserModel.getUid(), -1);
        updateProposal(proposal);
        adapter.notifyItemChanged(position);
    }

    private void updateProposal(ProposalModel proposal) {
        FirebaseDatabase.getInstance().getReference()
                .child("expenses")
                .child(expense.getId())
                .child("proposals")
                .child(proposal.getId())
                .setValue(new ProposalDataMapper(proposal));
    }

    @Override
    public void onSelect(ProposalModel proposal, int position, Drawable image) {
        expense.setStatus(ExpenseModel.ExpenseStatus.WAITING);
        expense.setWaitingProposal(new WaitingProposalModel(proposal, currentUserModel));
        expense.setLastModificationDate(Calendar.getInstance());

        if (proposal.isHasImage()) {
            Bitmap bitmap = ((GlideBitmapDrawable) image).getBitmap();
            StorageReference imageRef = FirebaseStorage.getInstance()
                    .getReference(ExpenseDataMapper.WAITING_PROPOSAL_PATH
                            .replace("{eid}", expense.getId()));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageInByte = stream.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
            imageRef.putStream(bis);
        }

        FirebaseDatabase.getInstance().getReference()
                .child("expenses")
                .child(expense.getId())
                .setValue(new ExpenseDataMapper(expense));

        adapter.setVoteClickListener(null);
        adapter.setSelectionListener(null);
        adapter.setReadOnly(true);
        adapter.notifyDataSetChanged();
        updateActivity();
        getActivity().finish();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(ExpenseModel expense);
    }

}
