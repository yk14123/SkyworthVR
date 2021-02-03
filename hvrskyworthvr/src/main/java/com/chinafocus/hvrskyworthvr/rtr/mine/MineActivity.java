package com.chinafocus.hvrskyworthvr.rtr.mine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.blankj.utilcode.util.BarUtils;
import com.chinafocus.huaweimdm.MdmMainActivity;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.global.ConfigManager;
import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.model.DeviceInfoManager;
import com.chinafocus.hvrskyworthvr.net.ApiMultiService;
import com.chinafocus.hvrskyworthvr.service.event.VrAboutConnect;
import com.chinafocus.hvrskyworthvr.ui.main.about.WebAboutActivity;
import com.chinafocus.hvrskyworthvr.ui.setting.SettingActivity;
import com.chinafocus.hvrskyworthvr.util.TimeOutClickUtil;
import com.chinafocus.hvrskyworthvr.util.ViewClickUtil;
import com.chinafocus.hvrskyworthvr.util.statusbar.StatusBarCompatFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.chinafocus.hvrskyworthvr.global.Constants.ACTIVITY_ABOUT;
import static com.chinafocus.hvrskyworthvr.global.Constants.RESULT_CODE_MINE_FINISH;

public class MineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBarCompatFactory.getInstance().setStatusBarImmerse(this, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);

        Constants.ACTIVITY_TAG = ACTIVITY_ABOUT;

        findViewById(R.id.ctl_mine_root).setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);

        AppCompatTextView account = findViewById(R.id.tv_mine_about_account);
        account.setText(DeviceInfoManager.getInstance().getDeviceAccountName());

        AppCompatTextView uuid = findViewById(R.id.tv_mine_about_uuid);
        uuid.setText(String.format("UUID：%s", DeviceInfoManager.getInstance().getDeviceUUID()));

        findViewById(R.id.tv_back_door).setOnClickListener(v -> TimeOutClickUtil.getDefault().startTimeOutClick(this::startSettingActivity));
        findViewById(R.id.iv_setting_mdm).setOnClickListener(v -> TimeOutClickUtil.getMDM().startTimeOutClick(this::startMDMActivity));
        findViewById(R.id.ib_mine_back).setOnClickListener(v -> finish());

        ViewClickUtil.click(
                findViewById(R.id.tv_about_user_protocol),
                () -> WebAboutActivity.startWebAboutActivity(
                        this,
                        getString(R.string.about_user_protocol),
                        ConfigManager.getInstance().getDefaultUrl() + ApiMultiService.ABOUT_USER_PROTOCOL)
        );

        ViewClickUtil.click(
                findViewById(R.id.tv_about_privacy_protocol),
                () -> WebAboutActivity.startWebAboutActivity(
                        this,
                        getString(R.string.about_privacy_protocol),
                        ConfigManager.getInstance().getDefaultUrl() + ApiMultiService.ABOUT_PRIVACY_PROTOCOL)
        );

        ViewClickUtil.click(
                findViewById(R.id.tv_about_us_protocol),
                () -> WebAboutActivity.startWebAboutActivity(
                        this,
                        getString(R.string.about_us_protocol),
                        ConfigManager.getInstance().getDefaultUrl() + ApiMultiService.ABOUT_US_PROTOCOL)
        );

    }

    private void startSettingActivity() {
        startActivity(new Intent(this, SettingActivity.class));
        finish();
    }

    private void startMDMActivity() {
        startActivity(new Intent(this, MdmMainActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_MINE_FINISH) {
            finish();
        }
    }

    /**
     * 在首页戴上VR眼镜
     *
     * @param event 戴上VR眼镜事件
     */
    @Subscribe()
    @SuppressWarnings("unused")
    public void connectToVR(VrAboutConnect event) {
        Log.d("MyLog", "-----在[关于]页面戴上VR眼镜-----");
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}