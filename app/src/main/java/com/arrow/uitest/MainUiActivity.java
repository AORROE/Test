package com.arrow.uitest;

import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

import com.arrow.test.R;
import com.arrow.utils.ExAdapter;

public class MainUiActivity extends AppCompatActivity {
    public String[] groupStrings = {"西游记", "水浒传", "三国演义", "红楼梦"};
    public String[][] childStrings = {
            {"唐三藏", "孙悟空", "猪八戒", "沙和尚"},
            {"宋江", "林冲", "李逵", "鲁智深"},
            {"曹操", "刘备", "孙权", "诸葛亮", "周瑜"},
            {"贾宝玉", "林黛玉", "薛宝钗", "王熙凤"}
    };
    private ExpandableListView expandableListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("ARROW");
        setContentView(R.layout.activity_main_ui);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        final Switch s = findViewById(R.id.switch_demo);
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    Toast.makeText(MainUiActivity.this,s.getTextOn().toString(),Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainUiActivity.this,s.getTextOff().toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        expandableListView = findViewById(R.id.exlistview);
        ExAdapter adapter = new ExAdapter(groupStrings,childStrings);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                Toast.makeText(MainUiActivity.this,groupStrings[i],Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Toast.makeText(MainUiActivity.this,childStrings[i][i1],Toast.LENGTH_SHORT).show();
                return false;
            }
        });
//        expandableListView.setGroupIndicator(null);
//        Display display = getWindowManager().getDefaultDisplay();
//        DisplayMetrics metrics = new DisplayMetrics();
//        display.getMetrics(metrics);
//        int widthPixels = metrics.widthPixels;
//        Drawable drawable = getResources().getDrawable(R.drawable.arrow);
//        int width = drawable.getIntrinsicWidth();
//        expandableListView.setGroupIndicator(drawable);
//        expandableListView.setIndicatorBounds(width -40,widthPixels -40);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_actionbar,menu);
        MenuItem item = menu.findItem(R.id.user);
        SearchView searchView = (SearchView) item.getActionView();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.user:
                Toast.makeText(this,"user",Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_user:
                Toast.makeText(this,"add_user",Toast.LENGTH_SHORT).show();
                break;
            case R.id.del_user:
                Toast.makeText(this,"del_user",Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
