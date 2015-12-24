package com.socks.jiandan.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.socks.jiandan.JDApplication;
import com.socks.jiandan.R;
import com.socks.jiandan.base.BaseActivity;
import com.socks.jiandan.base.ConstantString;
import com.socks.jiandan.net.JDApi;
import com.socks.jiandan.net.parser.Push4FreshCommentParser;
import com.socks.jiandan.net.parser.PushCommentParser;
import com.socks.jiandan.utils.EditTextShakeHelper;
import com.socks.jiandan.utils.SPHelper;
import com.socks.jiandan.utils.TextUtil;
import com.socks.jiandan.utils.ToastHelper;
import com.socks.jiandan.view.InputWatcher;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PushCommentActivity extends BaseActivity {

    @Bind(R.id.tv_title)
    TextView tv_title;
    @Bind(R.id.et_content)
    EditText et_content;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private String thread_id;
    private String parent_id;
    private String parent_name;
    private String author_name;
    private String author_email;
    private String message;

    private EditText et_name;
    private EditText et_email;

    private MaterialDialog dialog;

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_push_comment);
        ButterKnife.bind(this);
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("回复");
        mToolbar.setNavigationIcon(R.drawable.ic_actionbar_back);
    }

    @Override
    protected void loadData() {
        parent_name = getIntent().getStringExtra("parent_name");
        tv_title.setText(TextUtil.isNull(parent_name) ? "回复:" : "回复:" + parent_name);
        //新鲜事中 文章id=当前的thread_id=接口参数中的post_id
        thread_id = getIntent().getStringExtra("thread_id");
        parent_id = getIntent().getStringExtra("parent_id");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_push_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_push:

                message = et_content.getText().toString();

                if (TextUtil.isNull(message)) {
                    ToastHelper.Short(ConstantString.INPUT_TOO_SHORT);
                    new EditTextShakeHelper(this).shake(et_content);
                    return true;
                }


                dialog = new MaterialDialog.Builder(this)
                        .title("作为游客留言")
                        .backgroundColor(getResources().getColor(JDApplication.COLOR_OF_DIALOG))
                        .contentColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                        .positiveColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                        .negativeColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                        .titleColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                        .customView(R.layout.dialog_commentotar_info, true)
                        .positiveText("确定")
                        .negativeText(android.R.string.cancel)
                        .onPositive((dialog1, which) -> {
                            author_name = et_name.getText().toString();
                            author_email = et_email.getText().toString();

                            SPHelper.setString("author_name", author_name);
                            SPHelper.setString("author_email", author_email);

                            //新鲜事评论get
                            if (thread_id.length() == 5) {
                                String url;
                                //回复别人 和首次评论
                                if (!TextUtil.isNull(parent_id) && !TextUtil.isNull
                                        (parent_name)) {
                                    url = Push4FreshCommentParser.getRequestURL(thread_id, parent_id, parent_name, author_name, author_email, message);
                                } else {
                                    url = Push4FreshCommentParser.getRequestURLNoParent(thread_id, author_name, author_email, message);
                                }

                                JDApi.pushComment4FreshNews(url).subscribe(aBoolean -> {
                                    dialog1.dismiss();
                                    if (aBoolean) {
                                        setResult(RESULT_OK);
                                        finish();
                                    } else {
                                        ToastHelper.Short(ConstantString.COMMENT_FAILED);
                                    }
                                }, e -> {
                                    ToastHelper.Short(ConstantString.COMMENT_FAILED);
                                    dialog1.dismiss();
                                });
                            } else {
                                //多说的评论post
                                HashMap<String, String> requestParams;
                                //回复别人 和首次评论
                                if (!TextUtil.isNull(parent_id)) {
                                    requestParams = PushCommentParser.getRequestParams(thread_id, parent_id,
                                            author_name, author_email, message);
                                } else {
                                    requestParams = PushCommentParser.getRequestParamsNoParent(thread_id, author_name, author_email, message);
                                }

                                JDApi.pushComment4DuoShuo(requestParams).subscribe(aBoolean -> {
                                    dialog1.dismiss();
                                    if (aBoolean) {
                                        setResult(RESULT_OK);
                                        finish();
                                    } else {
                                        ToastHelper.Short(ConstantString.COMMENT_FAILED);
                                    }
                                }, e -> {
                                    ToastHelper.Short(ConstantString.COMMENT_FAILED);
                                    dialog1.dismiss();
                                });
                            }
                        }).build();

                View customView = dialog.getCustomView();

                assert customView != null;
                et_name = (EditText) customView.findViewById(R.id.et_name);
                et_email = (EditText) customView.findViewById(R.id.et_email);

                MDButton positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
                InputWatcher watcher = new InputWatcher(et_name, et_email, positiveAction);
                et_name.addTextChangedListener(watcher);
                et_email.addTextChangedListener(watcher);

                et_name.setText(SPHelper.getString("author_name"));
                et_email.setText(SPHelper.getString("author_email"));
                dialog.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}