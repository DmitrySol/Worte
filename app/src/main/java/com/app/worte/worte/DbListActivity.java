package com.app.worte.worte;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private ListView worteDbList;

    private List<String> availableDb;

    private ArrayList<RawStatus> rawStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_list);

        availableDb = new FileSystemOperator().getDbNamesList();

        worteDbList = (ListView) findViewById(R.id.llChb);

        rawStatus = new ArrayList<RawStatus>();

        // TODO: Make Init from persistency
        for (int i = 0; i < availableDb.size(); i++)
        {
            rawStatus.add(new RawStatus(i, false, availableDb.get(i)));
        }

        worteDbList.invalidate();
        worteDbList.setAdapter(new WorteDbAdapter());
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
                    if (rawStatus.get(position).isSelected())
                    {
                        rawStatus.get(position).unselect();
                    }
                    else
                    {
                        rawStatus.get(position).select();
                    }
                }
            });

            if (rawStatus.get(position).isSelected())
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