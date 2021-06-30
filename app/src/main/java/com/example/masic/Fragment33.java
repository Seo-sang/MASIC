package com.example.masic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment33#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment33 extends Fragment {

    Button listButton; //playlist로 전환하는 버튼
    static Button playBtn;
    TextView playTitle;
    TextView playNickname;
    ImageView playImage;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    String node []=new String[Fragment22.gridRowSize * Fragment22.gridColumnSize];
    String title;
    String producer;
    String image;
    String uid;

    public Fragment33() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param node Parameter 1.
     * @param title Parameter 2.
     * @return A new instance of fragment Fragment33.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment33 newInstance(String node[], String title, String producer, String image) {
        Fragment33 fragment = new Fragment33();
        Bundle args = new Bundle();
        args.putStringArray("nodeData", node);
        args.putString("titlePlay", title);
        args.putString("producer", producer);
        args.putString("image", image);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            node = getArguments().getStringArray("node");
            title = getArguments().getString("title");
            producer = getArguments().getString("producer");
            image = getArguments().getString("image");
            uid = getArguments().getString("uid");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_33,container, false);
        listButton = rootView.findViewById(R.id.listButton);
        listButton.setOnClickListener(new View.OnClickListener() { //playlist로 넘어가는 버튼 listener
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PlayList.class);
                intent.putExtra("uid", uid);
                startActivityForResult(intent,301);
            }
        });
        playTitle=rootView.findViewById(R.id.play_title);
        playTitle.setText(title);

        playNickname=rootView.findViewById(R.id.producer);
        playNickname.setText(producer);

        playImage=rootView.findViewById(R.id.play_image);
        Picasso.get().load(image).into(playImage);

        playBtn=rootView.findViewById(R.id.playBtn3);
        if(ServicePlay.play){
            playBtn.setText("■");
        }
        else {
            playBtn.setText("▶");
        }
        //playbutton ServicePlay 클릭 이벤트
        View.OnClickListener backgroundPlay = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(node==null)return;
                if(MainActivity.serviceIntent == null){MainActivity.serviceIntent = new Intent(getActivity().getApplicationContext(), ServicePlay.class);}
                if (!ServicePlay.play) {
                    Bundle data = new Bundle();
                    data.putStringArray("node", node);
                    data.putString("uid", uid);
                    data.putInt("Fragment", 3);
                    data.putBoolean("new", true);
                    MainActivity.serviceIntent.putExtras(data);
                    getActivity().startService(MainActivity.serviceIntent); // 재생 시작
                }
                else{
                    Bundle data = new Bundle();
                    data.putInt("Fragment", 3);
                    data.putBoolean("new", false);
                    MainActivity.serviceIntent.putExtras(data);
                    getActivity().stopService(MainActivity.serviceIntent); // 재생 중지
                    MainActivity.serviceIntent = null;
                }
            }
        };
        playBtn.setOnClickListener(backgroundPlay);


        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==301&&resultCode==RESULT_OK){
            title=data.getStringExtra("title");
            node=data.getStringArrayExtra("node");
            producer=data.getStringExtra("producer");
            image=data.getStringExtra("image");

            playTitle.setText(title);
            playNickname.setText(producer);
            Picasso.get().load(image).into(playImage);
            if(ServicePlay.play){
                Intent intent = new Intent(getActivity().getApplicationContext(), ServicePlay.class);
                Bundle bData = new Bundle();
                bData.putInt("Fragment", 3);
                intent.putExtras(bData);
                getActivity().stopService(intent);
            }
        }
    }
}