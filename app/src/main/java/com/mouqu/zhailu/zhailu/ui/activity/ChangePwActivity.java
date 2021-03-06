package com.mouqu.zhailu.zhailu.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mouqu.zhailu.zhailu.R;
import com.mouqu.zhailu.zhailu.base.BaseActivity;
import com.mouqu.zhailu.zhailu.bean.ChangePasswordBean;
import com.mouqu.zhailu.zhailu.contract.activity.ChangePwContract;
import com.mouqu.zhailu.zhailu.modle.activity.ChangePwModel;
import com.mouqu.zhailu.zhailu.presenter.activity.ChangePwPresenter;
import com.mouqu.zhailu.zhailu.util.CodeUtil;
import com.mouqu.zhailu.zhailu.util.GetSPData;
import com.mouqu.zhailu.zhailu.util.LoginQuit;

import butterknife.BindView;

public class ChangePwActivity extends BaseActivity<ChangePwPresenter, ChangePwModel> implements ChangePwContract.View, View.OnClickListener {
    @BindView(R.id.imageButton)
    ImageButton imageButton;
    @BindView(R.id.ll_cancel)
    LinearLayout llCancel;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.editText1)
    EditText editText1;
    @BindView(R.id.view6)
    View view6;
    @BindView(R.id.editText2)
    EditText editText2;
    @BindView(R.id.view7)
    View view7;
    @BindView(R.id.button_change)
    Button buttonChange;
    private String oldPassword;
    private String newPassword;

    @Override
    protected void initViewAndEvents() {

    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_change_pw;
    }

    @Override
    protected void setUpView() {
        setListener();
    }

    @Override
    protected void setUpData() {
        oldPassword = editText1.getText().toString();
        newPassword = editText2.getText().toString();
    }

    public void setListener() {
        llCancel.setOnClickListener(this);
        buttonChange.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ll_cancel:
                finish();
                break;
            case R.id.button_change:
                setChangePassword();
                break;
        }
    }

    private void setChangePassword() {
        GetSPData getSPData = new GetSPData();
        String userId = getSPData.getSPUserID(ChangePwActivity.this);
        if (CodeUtil.isPassword(oldPassword) & CodeUtil.isPassword(newPassword)) {
            /*
            * 密码格式正确
            * 开始修改密码
            * */
            mMvpPresenter.changePassword(ChangePwActivity.this,userId,oldPassword,newPassword,mMultipleStateView);
        } else {
            Toast.makeText(ChangePwActivity.this, "请输入6-16位字母数字混合密码,首位不为数字", Toast.LENGTH_SHORT).show();
         }
    }


    @Override
    public void changePassword(ChangePasswordBean bean) {
        Toast.makeText(ChangePwActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
        new LoginQuit().loginQuit(ChangePwActivity.this, 1);
    }
}
