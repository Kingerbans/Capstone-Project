package com.example.webrtcdemo.Fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.webrtcdemo.CallActivity;
import com.example.webrtcdemo.Handler.SocketHandler;
import com.example.webrtcdemo.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    Button btnCall, btnCopy;
    EditText edtTxtName;
    TextView txtID;
    private static final String CHECKCALLER = "checkCaller";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnCall = view.findViewById(R.id.btnCall);
        btnCopy = view.findViewById(R.id.btnCopy);
        edtTxtName = view.findViewById(R.id.edtTxtName);
        txtID = view.findViewById(R.id.txtID);

        txtID.setText("ID: " + SocketHandler.getSocketId());

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CallActivity.class);
                intent.putExtra(CHECKCALLER, true);
                startActivity(intent);
            }
        });

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Copied!", Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(view.getContext().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("id", SocketHandler.getSocketId());
                clipboard.setPrimaryClip(clip);
            }
        });

        return view;
    }
}