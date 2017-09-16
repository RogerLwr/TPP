package com.tianpingpai.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.foundation.R;

import java.lang.reflect.Field;

public class BaseFragment extends Fragment implements ActivityEventListener {

    public static final String KEY_PARENT_FRAGMENT = "parentFragment";

    private static final String KEY_LAYOUT_ID = "layoutId";
    private int layoutRes;
    private Class<? extends BaseFragment> parentFragmentClass;
    private Binder binder = new Binder();

    private View rootView;
    private View overlayContainer;
    private View contentBlockerView;
    private ViewGroup actionBarContainer;
    private Activity containerActivity;

    public void setIndicatorCancelable(boolean indicatorCancelable) {
        this.indicatorCancelable = indicatorCancelable;
    }

    private boolean indicatorCancelable = true;

    public Binder getBinder(){
        return binder;
    }

    protected void configureActionBar(View view) {
        View backButton = view.findViewById(R.id.ab_back_button);
        if (backButton != null) {
            backButton.setOnClickListener(backButtonListener);
        }
        View closeButton = view.findViewById(R.id.ab_close_button);
        if (closeButton != null) {
            closeButton.setOnClickListener(backButtonListener);
        }
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if(toolbar != null){
            toolbar.setNavigationIcon(R.drawable.ic_back_green);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backButtonListener.onClick(v);
                }
            });
        }
    }

    public View findViewById(int id) {
        if (rootView != null) {
            return rootView.findViewById(id);
        }
        return null;
    }

    protected View getRootView() {
        return rootView;
    }

    protected void setContentView(int layoutId) {
        this.layoutRes = layoutId;
    }

    protected void onConfigureView(View rootView) {
        binder.bindView(this,rootView);
    }

    public void showLoading() {
        if (overlayContainer != null) {
            overlayContainer.setVisibility(View.VISIBLE);
        }
    }

    public void hideLoading() {
        if (overlayContainer != null) {
            overlayContainer.setVisibility(View.INVISIBLE);
        }
    }

    public void showContent() {
        if (contentBlockerView != null) {
            contentBlockerView.setVisibility(View.INVISIBLE);
        }
    }

    public int getContentHeight(){
        return rootView.getHeight();
    }

    public void hideContent() {
        if (contentBlockerView != null) {
            contentBlockerView.setVisibility(View.VISIBLE);
        }
    }

    public void hideKeyboard(){
        View view = getRootView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) ContextProvider.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public boolean handleBackKey() {
        if(parentFragmentClass != null){
            Intent intent = new Intent(getContainerActivity(),ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, parentFragmentClass);
            startActivity(intent);
            //TODO animation;
        }
        return false;
    }

    public View setActionBarLayout(int layoutId) {
        View view = containerActivity.getLayoutInflater().inflate(layoutId,
                null);
        actionBarContainer.addView(view);
        configureActionBar(view);
        return view;
    }

    public void setTitle(CharSequence title) {
        TextView titleTextView = (TextView) actionBarContainer
                .findViewById(R.id.ab_title_text_view);
        if(titleTextView != null){
            titleTextView.setText(title);
        }
    }

    public Activity getContainerActivity(){
        return containerActivity;
    }

    public void setTitle(int titleId) {
        String title = ContextProvider.getContext().getResources().getString(titleId);
        setTitle(title);
    }

    // -------- activity life cycles ----------
    @Override
    public void onActivityCreated(Activity a) {
        containerActivity = a;
        parentFragmentClass = (Class<? extends BaseFragment>) a.getIntent().getSerializableExtra(KEY_PARENT_FRAGMENT);
        Layout layout = getClass().getAnnotation(Layout.class);
        if (layout != null) {
            layoutRes = layout.id();
        }
    }

    @Override
    public void willSetContentView(Activity a) {
    }

    public void hideActionBar() {
        actionBarContainer.setVisibility(View.GONE);
    }

    @Override
    public void didSetContentView(Activity a) {
        overlayContainer = a.findViewById(R.id.overlay_container);
        overlayContainer.setOnClickListener(overlayOnClickListener);
        contentBlockerView = a.findViewById(R.id.content_blocker_view);
        actionBarContainer = (ViewGroup) a
                .findViewById(R.id.action_bar_container);
        hideContent();

        Layout layout = getClass().getAnnotation(Layout.class);
        if (layout != null) {
            layoutRes = layout.id();
            if(layout.actionBarLayout() != -1){
                setActionBarLayout(layout.actionBarLayout());
            }
        }
    }

    @Override
    public void onActivityResumed(Activity a) {

    }

    @Override
    public void onActivityPaused(Activity a) {

    }

    private OnClickListener overlayOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(indicatorCancelable) {
                hideLoading();
            }
        }
    };

    @Override
    public void onActivityDestroyed(Activity a) {
        containerActivity = null;
    }

    @Override
    public boolean onBackKeyDown(Activity a) {
        return handleBackKey();
    }

    @Override
    public void onActivityResult(Activity a, int requestCode, int resultCode, Intent data) {
        onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rootView = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_LAYOUT_ID, layoutRes);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        onActivityResult(getActivity(),requestCode,resultCode,data);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            layoutRes = savedInstanceState.getInt(KEY_LAYOUT_ID, 0);
        }
    }

    // -------------------overrides super------

    private void removeRootViewFromParent() {
        if (rootView == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (layoutRes != 0) {
            if (rootView == null) {
                Log.e("xx", "onCreateView new");
                rootView = inflater.inflate(layoutRes, container, false);
                onConfigureView(rootView);
            } else {
                Log.e("xx", "onCreateView reuse");
                removeRootViewFromParent();

            }
            onRestoreState(savedInstanceState);
            return rootView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void onRestoreState(@Nullable Bundle savedInstanceState){

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeRootViewFromParent();
    }

    // --------------------- hack---------


    @Override
    public void onDetach() {
        super.onDetach();
        if (sChildFragmentManagerField != null) {
            try {
                sChildFragmentManagerField.set(this, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // a hack refer to
    // http://stackoverflow.com/questions/15207305/getting-the-error-java-lang-illegalstateexception-activity-has-been-destroyed
    private static final Field sChildFragmentManagerField;

    static {
        Field f = null;
        try {
            f = Fragment.class.getDeclaredField("mChildFragmentManager");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        sChildFragmentManagerField = f;
    }

    private OnClickListener backButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(handleBackKey()){
                return;
            }
            getActivity().finish();
        }
    };
}
