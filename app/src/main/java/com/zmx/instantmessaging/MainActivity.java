package com.zmx.instantmessaging;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.contact.ContactsFragment;
import com.netease.nim.uikit.business.recent.RecentContactsFragment;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.zmx.instantmessaging.activity.view.FragmentTabHost;
import com.zmx.instantmessaging.fragment.ContactListFragment;
import com.zmx.instantmessaging.fragment.HomeFragment;
import com.zmx.instantmessaging.fragment.MineFragment;
import com.zmx.instantmessaging.fragment.SessionListFragment;
import com.zmx.instantmessaging.pojo.Tab;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private FragmentTabHost mTabhost;
    private LayoutInflater inFlater;
    private List<Tab> mTabs = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {

        authorityManage();
        initTab();
    }

    private void initTab() {

        Tab home = new Tab("消息", R.drawable.selector_icon_home, SessionListFragment.class);
        Tab dynamic = new Tab("通讯录", R.drawable.selector_icon_hot, ContactListFragment.class);
        Tab cart = new Tab("主页", R.drawable.selector_icon_cart, HomeFragment.class);
        Tab set = new Tab("设置", R.drawable.selector_icon_mine, MineFragment.class);

        mTabs.add(home);
        mTabs.add(dynamic);
        mTabs.add(cart);
        mTabs.add(set);

        inFlater = LayoutInflater.from(this);
        mTabhost = this.findViewById(R.id.tabhost);
        mTabhost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        for (Tab tab : mTabs) {

            TabHost.TabSpec tabSpec = mTabhost.newTabSpec(tab.getTitle());
            tabSpec.setIndicator(buildIndicator(tab));
            mTabhost.addTab(tabSpec, tab.getFragment(), null);

        }

        //去除分割线
        mTabhost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        mTabhost.setCurrentTab(0);

    }


    private View buildIndicator(Tab tab) {

        View view = inFlater.inflate(R.layout.tab_indicator, null);
        ImageView img = view.findViewById(R.id.icon_tab);
        TextView text = view.findViewById(R.id.txt_indicator);
        img.setBackgroundResource(tab.getIcon());
        text.setText("" + tab.getTitle());
        return view;
    }

}
