package com.aliyun.alink.devicesdk.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/*
 * Copyright (c) 2014-2016 Alibaba Group. All rights reserved.
 * License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

public abstract class BaseTemplateActivity extends BaseActivity implements View.OnClickListener{

    protected RelativeLayout funcRL1, funcRL2, funcRL3, funcRL4, funcRL5, funcRL6;
    protected TextView funcTV1, funcTV2, funcTV3, funcTV4, funcTV5, funcTV6;
    protected EditText funcET1, funcET2, funcET3, funcET4, funcET5, funcET6;
    protected Button funcBT1, funcBT2, funcBT3, funcBT4, funcBT5, funcBT6;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_template);
        initViews();
        initViewData();
    }

    protected abstract void initViewData();

    private void initViews() {
        funcRL1 = findViewById(R.id.func1);
        funcTV1 = findViewById(R.id.func1_name);
        funcET1 = findViewById(R.id.func1_param);
        funcBT1 = findViewById(R.id.func1_exec);
        funcBT1.setOnClickListener(this);

        funcRL2 = findViewById(R.id.func2);
        funcTV2 = findViewById(R.id.func2_name);
        funcET2 = findViewById(R.id.func2_param);
        funcBT2 = findViewById(R.id.func2_exec);
        funcBT2.setOnClickListener(this);

        funcRL3 = findViewById(R.id.func3);
        funcTV3 = findViewById(R.id.func3_name);
        funcET3 = findViewById(R.id.func3_param);
        funcBT3 = findViewById(R.id.func3_exec);
        funcBT3.setOnClickListener(this);

        funcRL4 = findViewById(R.id.func4);
        funcTV4 = findViewById(R.id.func4_name);
        funcET4 = findViewById(R.id.func4_param);
        funcBT4 = findViewById(R.id.func4_exec);
        funcBT4.setOnClickListener(this);

        funcRL5 = findViewById(R.id.func5);
        funcTV5 = findViewById(R.id.func5_name);
        funcET5 = findViewById(R.id.func5_param);
        funcBT5 = findViewById(R.id.func5_exec);
        funcBT5.setOnClickListener(this);

        funcRL6 = findViewById(R.id.func6);
        funcTV6 = findViewById(R.id.func6_name);
        funcET6 = findViewById(R.id.func6_param);
        funcBT6 = findViewById(R.id.func6_exec);
        funcBT6.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.func1_exec:
                onFunc1Click();
                break;
            case R.id.func2_exec:
                onFunc2Click();
                break;
            case R.id.func3_exec:
                onFunc3Click();
                break;
            case R.id.func4_exec:
                onFunc4Click();
                break;
            case R.id.func5_exec:
                onFunc5Click();
                break;
            case R.id.func6_exec:
                onFunc6Click();
                break;
        }
    }

    protected abstract void onFunc6Click();

    protected abstract void onFunc5Click();

    protected abstract void onFunc4Click();

    protected abstract void onFunc3Click();

    protected abstract void onFunc2Click();

    protected abstract void onFunc1Click();
}
