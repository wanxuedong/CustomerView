package com.example.customerview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.example.customerview.picker.PickView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private GuideLineView guideLineView;
    private SwitchButton switchOne,switchTwo;
    private ProgressView progressOne,progressTwo;
    private PickView pickerOne,pickerTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        guideLineView = findViewById(R.id.guide_line_view);
        switchOne = findViewById(R.id.switch_one);
        switchTwo = findViewById(R.id.switch_two);
        progressOne = findViewById(R.id.progress_one);
        progressTwo = findViewById(R.id.progress_two);
        pickerOne = findViewById(R.id.picker_one);
        pickerTwo = findViewById(R.id.picker_two);
        pickerOne.setSelectIndex(10);
        progressTwo.setProgress(50);
        List<String> content = new ArrayList<String>();
        content.add("第一个");
        content.add("第二个");
        content.add("第三个");
        content.add("第四个");
        guideLineView.setContents(content);
        guideLineView.setSelectIndex(0);
        switchOne.setSwitchStateListener(new SwitchButton.SwitchStateListener() {
            @Override
            public void stateChange(boolean isOpen) {
                switchTwo.switchState(isOpen,false);
            }
        });
        progressOne.setOnProgressListen(new ProgressView.OnProgressListen() {
            @Override
            public void onProgress(int progress) {
                progressTwo.setProgress(progress);
            }
        });
        pickerOne.setOnSelectLister(new PickView.OnSelectLister() {
            @Override
            public void onScroll(int index) {
                pickerTwo.setSelectIndex(index);
                Log.d("show_scroll", index + " : " + pickerOne.getContents()[index]);
            }
        });

    }


}