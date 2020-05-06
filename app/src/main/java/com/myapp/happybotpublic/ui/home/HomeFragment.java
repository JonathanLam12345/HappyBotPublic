package com.myapp.happybotpublic.ui.home;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.myapp.happybotpublic.MyCustomAdapter;

import com.myapp.happybotpublic.R;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment
{

    private HomeViewModel homeViewModel;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private ListView mList;
    private ArrayList<String> arrayList;
    private MyCustomAdapter mAdapter;
    private ImageButton mbtSpeak;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        arrayList = new ArrayList<String>();
        arrayList.add("Bot: Welcome to the Motivation Bot App!!!");
        final EditText editText = root.findViewById(R.id.editText);
        ImageButton send = root.findViewById(R.id.send_button);

        // relate the listView from java to the one created in xml
        mList = root.findViewById(R.id.list);
        mbtSpeak = root.findViewById(R.id.btSpeak);
        checkVoiceRecognition();

        mAdapter = new MyCustomAdapter(getContext(), arrayList);
        mList.setAdapter(mAdapter);

        send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String message = editText.getText().toString();

                // add the text in the arrayList
                arrayList.add("You: " + message);

                mAdapter.notifyDataSetChanged();
                editText.setText("");


            }
        });

        mbtSpeak.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                speak();

            }
        });

        return root;
    }




    public void checkVoiceRecognition()
    {
        // Check if voice recognition is present
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            mbtSpeak.setEnabled(false);
            showToastMessage("Voice recognizer not present");
        }
    }

    public void speak()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)

            // If Voice recognition is successful then it returns RESULT_OK
            if (resultCode == RESULT_OK)
            {
                ArrayList<String> textMatchList = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (!textMatchList.isEmpty())
                {
                    String searchQuery = textMatchList.get(0);

                    // add the text in the arrayList
                    arrayList.add("You: " + searchQuery);

                    mAdapter.notifyDataSetChanged();

                }
                // Result code for various error.
            }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    void showToastMessage(String message)
    {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
