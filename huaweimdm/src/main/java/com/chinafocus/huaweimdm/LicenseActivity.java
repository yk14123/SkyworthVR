/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */

package com.chinafocus.huaweimdm;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * The LicenseActivity for this Sample
 *
 * @author huawei mdm
 * @since 2019-10-23
 */
public class LicenseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.license_layout);
        Button acceptBtn = (Button)findViewById(R.id.cancelBtn);
        acceptBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        TextView licenseText = (TextView) findViewById(R.id.license_content);
        String filename = "huawei_software_license.html";
        String content = Utils.getStringFromHtmlFile(this, filename);
        licenseText.setText(Html.fromHtml(content));
    }


}
