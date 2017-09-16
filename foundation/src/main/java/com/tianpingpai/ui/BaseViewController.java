package com.tianpingpai.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.foundation.R;

public class BaseViewController implements ViewController, ActivityEventListener{

    private int layoutId;
    private View mView;

    private ViewGroup actionBarContainer;
    private View loadingContainer;
    private View submittingContainer;
    private FrameLayout emptyContainer;
    private View mEmptyView;
    private ViewStub networkErrorStub;
    private View networkErrorViewContainer;

    private ViewTransitionManager mViewTransitionManager;
    private FragmentActivity mActivity;
    private Binder binder = new Binder();

    private ActionSheet actionSheet;

    public ActionSheet getActionSheet(boolean create){
        if(actionSheet == null && create){
            actionSheet = new ActionSheet();
            actionSheet.setActivity(getActivity());
        }
        return actionSheet;
    }

    protected Binder getBinder(){
        return binder;
    }

    {
        Layout layout = getClass().getAnnotation(Layout.class);
        if(layout != null){
            setLayoutId(layout.id());
        }
    }

    public View setActionBarLayout(int layoutId) {
        actionBarLayout = layoutId;
        View view = getLayoutInflater().inflate(layoutId,
                null);
        actionBarContainer.addView(view);
        configureActionBar(view);
        if(layoutId == R.layout.ab_title_green){
            setStatusBarColor(getActivity().getResources().getColor(R.color.green));
        }else{
            setStatusBarColor(Color.WHITE);
        }
        return view;
    }

    public void setStatusBarColor(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getActivity().getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.setNavigationBarColor(Color.BLACK);
//            window.setStatusBarColor(color);
        }
    }

    public void hideActionBar(){
        if(actionBarContainer != null){
            actionBarContainer.setVisibility(View.GONE);
        }
    }

    public void showActionBar(){
        if(actionBarContainer != null){
            actionBarContainer.setVisibility(View.VISIBLE);
        }
    }

    public void showLoading(){
        loadingContainer.setVisibility(View.VISIBLE);
        ImageView iv = (ImageView) loadingContainer.findViewById(R.id.loading_image_view);
        ((AnimationDrawable) iv.getBackground()).start();
    }

    public void hideLoading(){
        loadingContainer.setVisibility(View.INVISIBLE);
    }

    public void showSubmitting(){
        hideKeyboard();
        submittingContainer.setVisibility(View.VISIBLE);
    }

    public void hideSubmitting(){
        submittingContainer.setVisibility(View.INVISIBLE);
    }

    public void hideKeyboard(){
        View view = getView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) ContextProvider.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void setActivity(FragmentActivity activity) {
        this.mActivity = activity;
    }

    @Override
    public ViewTransitionManager getViewTransitionManager() {
        return mViewTransitionManager;
    }

    @Override
    public void setViewTransitionManager(ViewTransitionManager viewTransitionManager) {
        this.mViewTransitionManager = viewTransitionManager;
    }

    @Override
    public int getLayoutId() {
        return layoutId;
    }

    @Override
    public void setLayoutId(int id) {
        layoutId = id;
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void setView(View view) {
        mView = view;
    }

    @Override
    public void createView(LayoutInflater inflater, ViewGroup container) {
        onCreateView(inflater, container);
    }

    @Override
    public void animationFinished() {
        onAnimationFinished();
    }

    private int actionBarLayout;

    @SuppressWarnings("unused")
    public void onCreateView(LayoutInflater inflater, ViewGroup container) {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.view_controller_base, null, false);
        FrameLayout contentContainer = (FrameLayout) view.findViewById(R.id.content_container);
        View contentView = getLayoutInflater().inflate(getLayoutId(), contentContainer, false);

        ViewGroup.LayoutParams lp = contentContainer.getLayoutParams();
        ViewGroup parent = (ViewGroup) contentContainer.getParent();
        int index = parent.indexOfChild(contentContainer);
        parent.removeView(contentContainer);
        parent.addView(contentView, index, lp);

        actionBarContainer = (ViewGroup) view.findViewById(R.id.action_bar_container);
        emptyContainer = (FrameLayout) view.findViewById(R.id.empty_container);
        loadingContainer = view.findViewById(R.id.loading_container);
        submittingContainer = view.findViewById(R.id.submitting_container);
        networkErrorStub = (ViewStub) view.findViewById(R.id.network_error_stub);
        setView(view);

        configureActionBarFromAnnotation();
        onConfigureView();
    }

    private void configureActionBarFromAnnotation(){
        ActionBar actionBar = getClass().getAnnotation(ActionBar.class);
        if(actionBar != null){
            int actionBarLayoutId = R.layout.ab_title_white;
            if(actionBar.layout() != -1){
                actionBarLayoutId = actionBar.layout();
            }
            View view = setActionBarLayout(actionBarLayoutId);
            if(actionBar.titleRes() != -1){
                setTitle(actionBar.titleRes());
            }else{
                setTitle(actionBar.title());
            }
            if(actionBar.hidden()){
                hideActionBar();
            }
            if(!TextUtils.isEmpty(actionBar.rightText())){
                TextView rightButton = (TextView) view.findViewById(R.id.ab_right_button);
                rightButton.setText(actionBar.rightText());
            }

        }
    }

    public void onAnimationFinished() {

    }

    @Override
    public void destroyView() {
        onDestroyView();
    }

    protected void onDestroyView(){

    }

    @Override
    public void resume() {
        onResume();
    }

    @Override
    public void pause(){
        onPause();
    }

    protected void onResume(){

    }

    protected void onPause(){

    }

    @Override
    public FragmentActivity getActivity() {
        return mActivity;
    }

    protected void configureActionBar(View view) {
        View backButton = view.findViewById(R.id.ab_back_button);
        if (backButton != null) {
            backButton.setOnClickListener(getBackButtonListener());
        }
        View closeButton = view.findViewById(R.id.ab_close_button);
        if (closeButton != null) {
            closeButton.setOnClickListener(getBackButtonListener());
        }
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if(toolbar != null){
            if(actionBarLayout == R.layout.ab_title_green) {
                toolbar.setNavigationIcon(R.drawable.ic_back_white);
            }else{
                toolbar.setNavigationIcon(R.drawable.ic_back_green);
            }
            toolbar.setNavigationOnClickListener(getBackButtonListener());
        }
    }

    @SuppressLint("InflateParams")
    public void showEmptyView(){
        if(getEmptyContainer() == null){
            return;
        }
        if(mEmptyView == null){
            EmptyView emptyView = getClass().getAnnotation(EmptyView.class);
            if(emptyView != null){
                int emptyId = emptyView.emptyLayout();
                if(emptyId != -1){
                    mEmptyView = getActivity().getLayoutInflater().inflate(emptyId,null);
                    ImageView imageView = (ImageView) mEmptyView.findViewById(R.id.image_view);
                    imageView.setImageResource(emptyView.imageResource());
                    TextView textView = (TextView) mEmptyView.findViewById(R.id.text_view);
                    textView.setText(emptyView.text());
                }else {
                    mEmptyView = getActivity().getLayoutInflater().inflate(R.layout.foundation_empty_view,null);
                    ImageView imageView = (ImageView) mEmptyView.findViewById(R.id.image_view);
                    imageView.setImageResource(emptyView.imageResource());
                    TextView textView = (TextView) mEmptyView.findViewById(R.id.text_view);
                    textView.setText(emptyView.text());
                }
            }
            if(getEmptyContainer() != null && mEmptyView != null){
                getEmptyContainer().addView(mEmptyView);
            }
        }
        getEmptyContainer().setVisibility(View.VISIBLE);
    }

    protected ViewGroup getEmptyContainer(){
        return emptyContainer;
    }

    public void setEmpyText(int res){
        TextView textView = (TextView) mEmptyView.findViewById(R.id.text_view);
        textView.setText(res);
    }

    public void setEmpyText(CharSequence res){
        TextView textView = (TextView) mEmptyView.findViewById(R.id.text_view);
        textView.setText(res);
    }


    public void hideEmptyView(){
        if(emptyContainer != null){
            emptyContainer.setVisibility(View.INVISIBLE);
        }
    }

    public void showContent(){
//        getActivity().findViewById(R.id.content_blocker_view).setVisibility(View.GONE);
    }

    public void showNetworkError(){
        if(networkErrorViewContainer == null) {
            networkErrorViewContainer= networkErrorStub.inflate();
            networkErrorViewContainer.findViewById(R.id.reload_button).setOnClickListener(reloadButtonListener);
        }else{
            networkErrorViewContainer.setVisibility(View.VISIBLE);
        }
    }

    public void setTitle(int titleRes){
        String title = getActivity().getString(titleRes);
        setTitle(title);
    }

    public void setTitle(CharSequence title) {
        TextView titleTextView = (TextView) actionBarContainer
                .findViewById(R.id.ab_title_text_view);
        if(titleTextView != null){
            titleTextView.setText(title);
            return;
        }
        Toolbar toolbar = (Toolbar) actionBarContainer.findViewById(R.id.toolbar);
        if(toolbar != null){
            toolbar.setTitle(title);
        }
    }

    private View.OnClickListener reloadButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onReloadData();
        }
    };

    protected void onReloadData(){
        networkErrorStub.setVisibility(View.INVISIBLE);
    }

    private LayoutInflater getLayoutInflater(){
        return getActivity().getLayoutInflater();
    }

    protected void onConfigureView() {
        onConfigureView(getView());
    }

    protected void onConfigureView(View rootView){
        binder.bindView(this,rootView);
    }

    protected View.OnClickListener getBackButtonListener(){
        return backButtonListener;
    }

    private View.OnClickListener backButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ActionSheet as = (ActionSheet) getViewTransitionManager();
            if(as != null ){
                hideKeyboard();
                as.handleBack(getActivity());
                return;
            }
            if(!onBackKeyDown(getActivity())) {
                getActivity().finish();
            }
        }
    };

    @Override
    public void onActivityCreated(Activity a) {

    }

    @Override
    public void willSetContentView(Activity a) {

    }

    @Override
    public void didSetContentView(Activity a) {

    }

    @Override
    public void onActivityResumed(Activity a) {

    }

    @Override
    public void onActivityPaused(Activity a) {

    }

    public void onActivityDestroyed(Activity a) {
        destroyView();
    }

    @Override
    public boolean onBackKeyDown(Activity a) {
        if(actionSheet != null){
            return actionSheet.handleBack(a);
        }
        return false;
    }

    @Override
    public void onActivityResult(Activity a, int requestCode, int resultCode, Intent data) {

    }
}
