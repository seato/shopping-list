package com.example.shopping_list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.*;
import com.example.database.DBConstants;
import com.example.database.SQLiteHelper;
import com.example.model.Item;
import com.example.model.ShoppingList;

import java.util.ArrayList;

public class ChooseLists extends Activity {

    protected SimpleCursorAdapter dataAdapter;
    protected SQLiteHelper dbHelper;
    protected ListView listView;
    protected ArrayList<String> itemsList;

    SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_lists);

        listView = (ListView) findViewById(R.id.choose_list_list_view);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        final Button addToListButton = (Button) findViewById(R.id.add_to_list_button);

        Intent intent = getIntent();
        itemsList = new ArrayList<String>(intent.getStringArrayListExtra(MainActivity.ITEM_INTENT));

        dbHelper = new SQLiteHelper(this);

        displayListView();
    }

    private void displayListView() {
        Cursor cursor = dbHelper.fetchAllLists();

        // data from the database
        String[] columns = new String[]{DBConstants.ShoppingListsCols.TITLE};

        // destination views for the data from the database
        int[] to = new int[]{R.id.list_title_check_box};

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.list_info,
                cursor,
                columns,
                to) {
            @Override
            public void bindView(View view, Context context, final Cursor cursor) {
                super.bindView(view, context, cursor);
                final int position = cursor.getPosition();
                final CheckBox listCheckBox = (CheckBox) view.findViewById(R.id.list_title_check_box);
                listCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        sparseBooleanArray.put(position, isChecked);
                    }
                });
            }
        };

        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
    }

    public void addToLists(View view) {
        Log.d("chooselists", "clicked the add button");
        for (int i = 0; i < sparseBooleanArray.size(); i++) {
            int key = sparseBooleanArray.keyAt(i);
            if (sparseBooleanArray.get(key)) {
                ShoppingList list = new ShoppingList(dbHelper.getList(key));

                Item item = new Item();
                item.listId = list.id;

                for (String name : itemsList) {
                    Log.d("chooselists", "adding " + name + " to the list");
                    item.name = name;
                    if (!dbHelper.itemExists(item)) {
                        dbHelper.addItem(item);
                        Log.d("chooselists", "added " + name + " successfully");
                    }
                }
            }
        }

        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
