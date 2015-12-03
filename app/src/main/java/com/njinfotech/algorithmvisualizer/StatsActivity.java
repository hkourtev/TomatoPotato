package com.njinfotech.algorithmvisualizer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.List;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // get all the resources
        TextView statsTotalNumTrials = (TextView)findViewById(R.id.statsTotalNumTrials);
        TextView statsAvgNumErr = (TextView)findViewById(R.id.statsAvgNumErr);
        TextView statsWorstTrial = (TextView)findViewById(R.id.statsWorstTrial);
        GridView gridErrorFrequency = (GridView)findViewById(R.id.gridErrorFrequency);

        DBHandler db = new DBHandler(this);
        statsTotalNumTrials.setText(Integer.toString(db.getTotalNumErrors()));
        statsAvgNumErr.setText(Double.toString(db.getAvgNumErrors()));
        statsWorstTrial.setText(Integer.toString(db.getMaxNumErrPerSession()));

        String[] freqData = formatFreqData(db.getErrorFrequency());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, freqData);
        gridErrorFrequency.setAdapter(adapter);
    }

    public String[] formatFreqData(List<String[]>data) {
        String[] formatted = new String[data.size()*2 + 2];

        formatted[0] = "Step";
        formatted[1] = "Frequency";
        for (int b=0; b<data.size(); b++) {
            formatted[2+b*2] = data.get(b)[1];
            formatted[2+b*2+1] = data.get(b)[0];
        }

        return formatted;
    }
}
