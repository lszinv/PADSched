package com.lsz.padsched;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.ToggleButton;

public class AlertsFragment extends Fragment {

    private static Switch[] switches;
    private static ToggleButton[] toggles;

    private static int currentGroup = Constants.GROUP_A;
    private static AlertSwitchObject currentASO = null;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alert_fragment, container, false);
        Spinner groupSpinner = (Spinner) view.findViewById(R.id.groupSpinner);
        AlertMasterToggleDataSource amDS = new AlertMasterToggleDataSource(getActivity());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.groups, android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(adapter);
        Button enableAllButton = (Button) view.findViewById(R.id.batchEnableButton);
        Button disableAllButton = (Button) view.findViewById(R.id.batchDisableButton);

        findSwitches(view);
        findToggles(view);

        // Set up master toggles
        ToggleButton toggleA = (ToggleButton) view.findViewById(R.id.aToggle);
        ToggleButton toggleB = (ToggleButton) view.findViewById(R.id.bToggle);
        ToggleButton toggleC = (ToggleButton) view.findViewById(R.id.cToggle);
        ToggleButton toggleD = (ToggleButton) view.findViewById(R.id.dToggle);
        ToggleButton toggleE = (ToggleButton) view.findViewById(R.id.eToggle);

        toggleA.setOnClickListener(new MasterToggleListener());
        toggleB.setOnClickListener(new MasterToggleListener());
        toggleC.setOnClickListener(new MasterToggleListener());
        toggleD.setOnClickListener(new MasterToggleListener());
        toggleE.setOnClickListener(new MasterToggleListener());

        toggleA.setChecked(amDS.getMasterToggle(Constants.GROUP_A));
        toggleB.setChecked(amDS.getMasterToggle(Constants.GROUP_B));
        toggleC.setChecked(amDS.getMasterToggle(Constants.GROUP_C));
        toggleD.setChecked(amDS.getMasterToggle(Constants.GROUP_D));
        toggleE.setChecked(amDS.getMasterToggle(Constants.GROUP_E));

        // setup spinner

        groupSpinner.setOnItemSelectedListener(new SelectGroupListener());

        // Enable/Disable all

        enableAllButton.setOnClickListener(new BatchButtonListener());
        disableAllButton.setOnClickListener(new BatchButtonListener());

        return view;
    }

    private void findToggles(View view) {
        toggles = new ToggleButton[5];
        toggles[Constants.GROUP_A] = (ToggleButton) view.findViewById(R.id.aToggle);
        toggles[Constants.GROUP_B] = (ToggleButton) view.findViewById(R.id.bToggle);
        toggles[Constants.GROUP_C] = (ToggleButton) view.findViewById(R.id.cToggle);
        toggles[Constants.GROUP_D] = (ToggleButton) view.findViewById(R.id.dToggle);
        toggles[Constants.GROUP_E] = (ToggleButton) view.findViewById(R.id.eToggle);
    }

    private void findSwitches(View view) {
        switches = new Switch[Constants.DUNGEON_COUNT];
        switches[Constants.RUBY_DORA] = (Switch) view.findViewById(R.id.rubySwitch);
        switches[Constants.EMERALD_DORA] = (Switch) view.findViewById(R.id.EmeSwitch);
        switches[Constants.SAPPHIRE_DORA] = (Switch) view.findViewById(R.id.SapphireSwitch);
        switches[Constants.META_DORA] = (Switch) view.findViewById(R.id.metaSwitch);
        switches[Constants.GOLD_DORA] = (Switch) view.findViewById(R.id.goldSwitch);
        switches[Constants.META_GOLD] = (Switch) view.findViewById(R.id.metaGoldSwitch);
        switches[Constants.PENGU_DORA] = (Switch) view.findViewById(R.id.pengdraSwitch);
        switches[Constants.DRAGON_PLANTS] = (Switch) view.findViewById(R.id.dragonPlantSwitch);
        switches[Constants.EVO_RUSH] = (Switch) view.findViewById(R.id.evoSwitch);
        switches[Constants.KING_CARNIVAL] = (Switch) view.findViewById(R.id.kingSwitch);
        switches[Constants.SUPER_KING] = (Switch) view.findViewById(R.id.superKingSwitch);
        switches[Constants.STAR_VAULT] = (Switch) view.findViewById(R.id.starSwitch);
        switches[Constants.TAMADRA] = (Switch) view.findViewById(R.id.tamaSwitch);
        switches[Constants.EXTREME_METAL] = (Switch) view.findViewById(R.id.extremeSwitch);
        switches[Constants.COIN] = (Switch) view.findViewById(R.id.coinSwitch);

        for (Switch sw : switches) {
            sw.setOnCheckedChangeListener(new AlertSwitchCheckChangeListener());
        }
    }

    private void updateSwitches() {

        for (int event = 0; event < Constants.DUNGEON_COUNT; event++) {
            switches[event].setChecked(currentASO.getSwitch(event));
        }

    }

    public class AlertSwitchCheckChangeListener implements OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int dungeon = -1;
            AlertSwitchDataSource asDS = new AlertSwitchDataSource(getActivity());

            switch (buttonView.getId()) {
                case R.id.rubySwitch:
                    dungeon = Constants.RUBY_DORA;
                    break;
                case R.id.EmeSwitch:
                    dungeon = Constants.EMERALD_DORA;
                    break;
                case R.id.SapphireSwitch:
                    dungeon = Constants.SAPPHIRE_DORA;
                    break;
                case R.id.metaSwitch:
                    dungeon = Constants.META_DORA;
                    break;
                case R.id.goldSwitch:
                    dungeon = Constants.GOLD_DORA;
                    break;
                case R.id.metaGoldSwitch:
                    dungeon = Constants.META_GOLD;
                    break;
                case R.id.kingSwitch:
                    dungeon = Constants.KING_CARNIVAL;
                    break;
                case R.id.evoSwitch:
                    dungeon = Constants.EVO_RUSH;
                    break;
                case R.id.starSwitch:
                    dungeon = Constants.STAR_VAULT;
                    break;
                case R.id.dragonPlantSwitch:
                    dungeon = Constants.DRAGON_PLANTS;
                    break;
                case R.id.pengdraSwitch:
                    dungeon = Constants.PENGU_DORA;
                    break;
                case R.id.extremeSwitch:
                    dungeon = Constants.EXTREME_METAL;
                    break;
                case R.id.tamaSwitch:
                    dungeon = Constants.TAMADRA;
                    break;
                case R.id.coinSwitch:
                    dungeon = Constants.COIN;
                    break;
                case R.id.superKingSwitch:
                    dungeon = Constants.SUPER_KING;
                    break;
                default:
                    return;
            }

            asDS.setToggle(currentGroup, dungeon, isChecked);
            asDS.close();
        }
    }

    private class BatchButtonListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            boolean batchValue;
            if (v.getId() == R.id.batchEnableButton) {
                batchValue = true;
            } else {
                batchValue = false;
            }
            for (Switch sw : switches) {
                sw.setChecked(batchValue);
            }
        }

    }

    private class SelectGroupListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            AlertSwitchDataSource asDS = new AlertSwitchDataSource(getActivity());
            currentGroup = arg2;
            currentASO = asDS.getSwitches(currentGroup);
            updateSwitches();
            asDS.close();
            disableSwitchAlerts();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

    private class MasterToggleListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            int group = 0;
            AlertMasterToggleDataSource amtDS = new AlertMasterToggleDataSource(getActivity());
            switch (v.getId()) {
                case R.id.aToggle:
                    group = Constants.GROUP_A;
                    break;
                case R.id.bToggle:
                    group = Constants.GROUP_B;
                    break;
                case R.id.cToggle:
                    group = Constants.GROUP_C;
                    break;
                case R.id.dToggle:
                    group = Constants.GROUP_D;
                    break;
                case R.id.eToggle:
                    group = Constants.GROUP_E;
                    break;
            }
            disableSwitchAlerts();
            amtDS.setMasterToggle(group, ((ToggleButton) v).isChecked());
            setAlertAlarms(getActivity());

        }

    }

    private void disableSwitchAlerts() {
        boolean disable;
        if (toggles[currentGroup].isChecked()) {
            disable = true;
        } else {
            disable = false;
        }
        for (Switch sw : switches) {
            if (disable) {
                sw.setEnabled(false);
            } else {
                sw.setEnabled(true);
            }
        }
        Button enableAll = (Button) getActivity().findViewById(R.id.batchEnableButton);
        Button disableAll = (Button) getActivity().findViewById(R.id.batchDisableButton);
        TableRow tr = (TableRow) getActivity().findViewById(R.id.editSwitchWarning);
        if (disable) {
            enableAll.setEnabled(false);
            disableAll.setEnabled(false);
            tr.setVisibility(View.VISIBLE);

        } else {
            enableAll.setEnabled(true);
            disableAll.setEnabled(true);
            tr.setVisibility(View.GONE);
        }
    }

    public static void setAlertAlarms(Context thisContext) {
        AlarmManager am;
        long alarmTime;
        am = (AlarmManager) thisContext.getSystemService(Context.ALARM_SERVICE);

        NextScheduleObject nso = ScheduleAlarmReceiver.getNextScheduleObject(thisContext);

        Intent intent = new Intent(thisContext, ScheduleAlarmReceiver.class);
        Intent noNotifyIntent = new Intent(thisContext, CheckAlarmReceiver.class);
        intent.putExtra("event", nso.getEvent());
        intent.putExtra("group", nso.getGroup());
        PendingIntent pi = PendingIntent.getBroadcast(thisContext, 0, intent, 0);
        PendingIntent nnPi = PendingIntent.getBroadcast(thisContext, 0, noNotifyIntent, 0);
        am.cancel(pi);
        am.cancel(nnPi);
        alarmTime = nso.getTime();
        if (alarmTime > 0) {
            am.setExact(AlarmManager.RTC, alarmTime, pi);
        } else {
            am.setInexactRepeating(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis() + 7200000, AlarmManager.INTERVAL_HOUR, nnPi);
        }

    }

}
