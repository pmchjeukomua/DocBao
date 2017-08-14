package com.example.nguyentrung.docbao.view.Fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TimePicker;

import com.example.nguyentrung.docbao.control.Constant;
import com.example.nguyentrung.docbao.R;
import com.example.nguyentrung.docbao.control.service.Receiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by nguyentrung on 5/2/2017.
 */

public class FragmentSetting extends Fragment {

    private static final String TAG = "FragmentSetting";
    private static final int PENDINGINTENT_ID = 0;
    private SwitchCompat switchGetNotification;
    private TimePicker timePicker;
    private Button btnSetTime;
    private SharedPreferences sharedPreferences;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getContext().getSharedPreferences(Constant.SHAREDPREFERENCES_NAME, MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switchGetNotification = (SwitchCompat) getActivity().findViewById(R.id.switchGetNotification);
        btnSetTime = (Button) getActivity().findViewById(R.id.btnSetTimeGetNotification);
        switchGetNotification = (SwitchCompat) getActivity().findViewById(R.id.switchGetNotification);
//        Log.e(TAG, "Time -----: " + sharedPreferences.getString(Constant.SAVED_TIME_IN_SHAREPREFERENCES, ""));
        setTextButtonTimeAndStateSwitch();

        switchGetNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPreferences.edit().putBoolean(Constant.IS_SAVED_IN_SHAREPREFERENCES, true).apply();
                    sharedPreferences.edit().putString(Constant.SAVED_TIME_IN_SHAREPREFERENCES, btnSetTime.getText().toString()).apply();
                    btnSetTime.setEnabled(false);
                    String time = sharedPreferences.getString(Constant.SAVED_TIME_IN_SHAREPREFERENCES, "");
                    setTimeGetNotification(time);
                } else {
                    sharedPreferences.edit().putBoolean(Constant.IS_SAVED_IN_SHAREPREFERENCES, false).apply();
                    cancelGetNotification();
                    btnSetTime.setEnabled(true);
                }
            }
        });

        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time;
                        if (hourOfDay < 10){
                            if (minute < 10){
                                time = "0" + hourOfDay + ":" + "0" + minute;
                            }else {
                                time = "0" + hourOfDay + ":" + minute;
                            }
                        }else {
                            if (minute < 10){
                                time = hourOfDay + ":" + "0" + minute;
                            }else {
                                time = hourOfDay + ":" + minute;
                            }
                        }
                        btnSetTime.setText(time);
//                        Log.e(TAG, "onTimeSet: " + hourOfDay + ":" + minute);
                    }
                }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
                timePickerDialog.setTitle(getString(R.string.set_time));
                timePickerDialog.show();
            }
        });
    }

    public void setTextButtonTimeAndStateSwitch() {

        boolean isSaved = sharedPreferences.getBoolean(Constant.IS_SAVED_IN_SHAREPREFERENCES, false);
        if (isSaved) {
            String time = sharedPreferences.getString(Constant.SAVED_TIME_IN_SHAREPREFERENCES, "");
            btnSetTime.setText(time);
            btnSetTime.setEnabled(false);
            btnSetTime.setClickable(false);
            switchGetNotification.setChecked(true);
        } else {
            String time = sharedPreferences.getString(Constant.SAVED_TIME_IN_SHAREPREFERENCES, "");
            if (time != ""){
                btnSetTime.setText(time);
            }else {
//            Calendar calendar = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                String currentTime = df.format(Calendar.getInstance().getTime());
                btnSetTime.setText(currentTime);
                switchGetNotification.setChecked(false);
                btnSetTime.setEnabled(true);
            }
        }
    }

    public String getSavedTime() {
        return sharedPreferences.getString(Constant.SAVED_TIME, null);
    }

    private void setTimeGetNotification(String time) {
        alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
        int hour = Integer.parseInt(time.split(":")[0]);
        int minute = Integer.parseInt(time.split(":")[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        Intent intent = new Intent(getContext(), Receiver.class);
        pendingIntent = PendingIntent.getBroadcast(getContext(), PENDINGINTENT_ID, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void cancelGetNotification() {
        Intent intent = new Intent(getContext(), Receiver.class);
        pendingIntent = PendingIntent.getBroadcast(getContext(), PENDINGINTENT_ID, intent, 0);
        alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
//        sharedPreferences.edit().clear().apply();
    }

}

