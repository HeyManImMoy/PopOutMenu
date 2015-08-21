package com.lynn.popoutmenu;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.lynn.popoutmenu.view.PopOutMenu;
import com.lynn.popoutmenu.view.PopOutMenu.OnMenuClickListener;

/**
 * 
 * @author Lynn
 * @description 使用例子
 */
public class MainActivity extends ActionBarActivity {

	private ListView mListView;
	private PopOutMenu mPopOutMenu;
	private PopOutMenu mPopOutMenu2;
	private ArrayList<String> mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		initData();
		initView();

		mListView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, mList));

		// 当listview滚动时 让PopOutMenu收起
		mListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
					int totalItemCount) {
				if (mPopOutMenu.isOpen())
					mPopOutMenu.toggleMenu(300);
			}
		});

		// ^^^^^^^^^^^^^^^^^^^设置PopOutMenu监听器^^^^^^^^^^^^^^^^^^^
		mPopOutMenu.setOnMenuClickListener(new OnMenuClickListener() {
			@Override
			public void onClick(View view, int position) {
				Toast.makeText(getApplicationContext(), "点击了第" + position + "个 menuItem",
						Toast.LENGTH_SHORT).show();
			}
		});

		mPopOutMenu2.setOnMenuClickListener(new OnMenuClickListener() {
			@Override
			public void onClick(View view, int position) {
				Toast.makeText(getApplicationContext(), "点击了线性式第" + position + "个 menuItem",
						Toast.LENGTH_SHORT).show();
			}
		});

	}

	private void initData() {
		mList = new ArrayList<String>();
		for (int i = 'A'; i < 'Z'; i++)
			mList.add((char) i + "");
	}

	private void initView() {
		mListView = (ListView) this.findViewById(R.id.id_listview);
		mPopOutMenu = (PopOutMenu) this.findViewById(R.id.id_menu1);
		mPopOutMenu2 = (PopOutMenu) this.findViewById(R.id.id_menu2);
	}

}
