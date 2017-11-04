package com.app.worte.worte;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class RawStatus
{
    private String name;
    private boolean isDbSelected;
    private int index;

    public RawStatus(int index,boolean isSelected, String dbName)
    {
        this.name = dbName;
        this.isDbSelected = isSelected;
        this.index = index;
    }

    public int getIndex()
    {
        return this.index;
    }

    public String getName()
    {
        return this.name;
    }

    public boolean isSelected()
    {
        return this.isDbSelected;
    }

    public void select()
    {
        this.isDbSelected = true;
    }

    public void unselect()
    {
        this.isDbSelected = false;
    }
}

public class DbListActivity extends AppCompatActivity
{
    private final String LOG_TAG = "DbListActivity";

    private ListView worteDbList;
    private List<String> availableDb;
    private ArrayList<RawStatus> rawStatuses;

    WortePreferences wortePref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_list);

        availableDb = new FileSystemOperator().getDbNamesList();
        worteDbList = (ListView) findViewById(R.id.llChb);
        rawStatuses = new ArrayList<RawStatus>();

        wortePref = new WortePreferences(getApplicationContext());
        List<String> chosenDbNamesList = wortePref.getChoosenDbList();

        for (int i = 0; i < availableDb.size(); i++)
        {
            String currentDbName = availableDb.get(i);
            boolean isDbChoosen = false;

            if ((chosenDbNamesList != null) && (chosenDbNamesList.contains(currentDbName)))
            {
                isDbChoosen = true;
                Log.i(LOG_TAG, "DB: " + currentDbName + " is choosen");
            }

            rawStatuses.add(new RawStatus(i, isDbChoosen, currentDbName));
        }

        worteDbList.invalidate();
        worteDbList.setAdapter(new WorteDbAdapter());
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        String choosenDbString = "";

        for(RawStatus rawStatus : rawStatuses)
        {
            if(rawStatus.isSelected())
            {
                choosenDbString += rawStatus.getName();
                choosenDbString += "%";
            }
        }

        wortePref.saveChoosenDbList(choosenDbString);
        Log.i(LOG_TAG, "Saved WDB list: " + choosenDbString);
    }


    public class WorteDbAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return availableDb.size();
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            View row = View.inflate(getApplicationContext(), R.layout.wdb_raw, null);

            TextView tvContent=(TextView) row.findViewById(R.id.wdbName);
            CheckBox cb = (CheckBox) row.findViewById(R.id.wdbIsSelected);

            tvContent.setText(availableDb.get(position));

            cb.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (rawStatuses.get(position).isSelected())
                    {
                        rawStatuses.get(position).unselect();
                    }
                    else
                    {
                        rawStatuses.get(position).select();
                    }
                }
            });

            if (rawStatuses.get(position).isSelected())
            {
                cb.setChecked(true);
            }
            else
            {
                cb.setChecked(false);
            }
            return row;
        }
    }
}