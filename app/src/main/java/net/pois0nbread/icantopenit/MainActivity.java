package net.pois0nbread.icantopenit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

/**
 * <pre>
 *     author : Pois0nBread
 *     e-mail : pois0nbreads@gmail.com
 *     time   : 2019/12/02
 *     desc   : AppMain
 *     version: 1.0
 * </pre>
 */

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private Context mContext;

    private ListView listView;
    private AppAdapter adapter;

    public static SharedPreferences preferences;

    public static int apps = 0;
    private boolean SYSTEM_Tag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        preferences = getSharedPreferences("settings", Context.MODE_WORLD_READABLE);

        listView = (ListView) findViewById(R.id.main_list);
        adapter = new AppAdapter(getAllAppInfos(), mContext);
        listView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);

        Switch switchEnable = (Switch) menu.findItem(R.id.menu_switch).getActionView();
        switchEnable.setPadding(0,0,15,0);
        switchEnable.setText("启用 ");
        switchEnable.setChecked(preferences.getBoolean("Enable", false));
        switchEnable.setOnCheckedChangeListener(this);

        menu.findItem(R.id.menu_check).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                item.setChecked(!item.isChecked());
                SYSTEM_Tag = item.isChecked();
                if (item.isChecked())
                    Toast.makeText(mContext, "已显示", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(mContext, "未显示", Toast.LENGTH_SHORT).show();
                adapter = new AppAdapter(getAllAppInfos(), mContext);
                listView.setAdapter(adapter);
                return false;
            }
        });
        menu.findItem(R.id.menu_setting).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.menu_switch:
                preferences.edit().putBoolean("Enable", b).apply();
                if (b)
                    Toast.makeText(mContext, "已启用", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(mContext, "未启用", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    protected List<AppInfo> getAllAppInfos() {
        List<AppInfo> list = new ArrayList<AppInfo>();
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> ResolveInfos = packageManager.queryIntentActivities(intent, 0);
        for (ResolveInfo ri : ResolveInfos) {
            String packageName = ri.activityInfo.packageName;
            Drawable icon = ri.loadIcon(packageManager);
            String appName = ri.loadLabel(packageManager).toString();
            AppInfo appInfo = new AppInfo(icon, appName, packageName);
            if (!packageName.equals(BuildConfig.APPLICATION_ID)) list.add(appInfo);
        }
        if (SYSTEM_Tag) {
            List<ApplicationInfo> applicationInfos = packageManager.getInstalledApplications(PackageManager.MATCH_SYSTEM_ONLY);
            for (ApplicationInfo info : applicationInfos) {
                String packagename = info.packageName;
                Drawable icon = info.loadIcon(packageManager);
                String appName = info.loadLabel(packageManager).toString();
                AppInfo appInfo = new AppInfo(icon, appName, packagename);
                list.add(appInfo);
            }
        }
        return list;
    }
}
