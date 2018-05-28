package com.andbase.library.view.tabs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.andbase.library.R;

/**
 * Copyright upu173.com
 * Author 还如一梦中
 * Date 2018/5/17 17:54
 * Email 396196516@qq.com
 * Info CollapsingToolbarLayout + TabLayout.
 */

public class AbCoordinatorTabLayout extends CoordinatorLayout {
    private int[] imageArray, colorArray;

    private Context context;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private ActionBar actionbar;
    private TabLayout tabLayout;
    private ImageView imageView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private LoadHeaderImagesListener loadHeaderImagesListener;
    private OnTabSelectedListener onTabSelectedListener;
    private FrameLayout headerView;

    public static int EXPANDED = 0;
    public static int COLLAPSED = 1;
    public static int IDLE = 2;


    public AbCoordinatorTabLayout(Context context) {
        super(context);
        this.context = context;
    }

    public AbCoordinatorTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (!isInEditMode()) {
            initView(context);
            initWidget(context, attrs);
        }
    }

    public AbCoordinatorTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        if (!isInEditMode()) {
            initView(context);
            initWidget(context, attrs);
        }
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_coordinator_tab_layout, this, true);
        initToolbar();
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        headerView = (FrameLayout) findViewById(R.id.header_view);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        imageView = (ImageView) findViewById(R.id.header_image_view);
        appBarLayout = (AppBarLayout)findViewById(R.id.app_bar_layout);
    }

    private void initWidget(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs
                , R.styleable.AbCoordinatorTabLayout);

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int contentScrimColor = typedArray.getColor(
                R.styleable.AbCoordinatorTabLayout_contentScrim, typedValue.data);
        collapsingToolbarLayout.setContentScrimColor(contentScrimColor);

        int tabIndicatorColor = typedArray.getColor(R.styleable.AbCoordinatorTabLayout_tabIndicatorColor, Color.WHITE);
        tabLayout.setSelectedTabIndicatorColor(tabIndicatorColor);

        int tabTextColor = typedArray.getColor(R.styleable.AbCoordinatorTabLayout_tabTextColor, Color.WHITE);
        tabLayout.setTabTextColors(ColorStateList.valueOf(tabTextColor));
        typedArray.recycle();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((AppCompatActivity) context).setSupportActionBar(toolbar);
        actionbar = ((AppCompatActivity) context).getSupportActionBar();
    }

    /**
     * 设置Toolbar标题
     * @param title 标题
     * @return
     */
    public AbCoordinatorTabLayout setTitle(String title) {
        if (actionbar != null) {
            actionbar.setTitle(title);
        }
        return this;
    }

    /**
     * 设置Toolbar显示返回按钮及标题
     * @param canBack 是否返回
     * @return
     */
    public AbCoordinatorTabLayout setBackEnable(Boolean canBack) {
        if (canBack && actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_action_back);
        }
        return this;
    }

    /**
     * 设置每个tab对应的头部图片
     * @param imageArray 图片数组
     * @return
     */
    public AbCoordinatorTabLayout setImageArray(@NonNull int[] imageArray) {
        this.imageArray = imageArray;
        return this;
    }

    /**
     * 设置每个tab对应的头部照片和ContentScrimColor
     * @param imageArray 图片数组
     * @param colorArray ContentScrimColor数组
     * @return
     */
    public AbCoordinatorTabLayout setImageArray(@NonNull int[] imageArray, @NonNull int[] colorArray) {
        this.imageArray = imageArray;
        this.colorArray = colorArray;
        return this;
    }


    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                imageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_dismiss));
                if (loadHeaderImagesListener == null) {
                    if (imageArray != null) {
                        imageView.setImageResource(imageArray[tab.getPosition()]);
                    }
                } else {
                    loadHeaderImagesListener.loadHeaderImages(imageView, tab);
                }
                if (colorArray != null) {
                    collapsingToolbarLayout.setContentScrimColor(
                            ContextCompat.getColor(
                                    context, colorArray[tab.getPosition()]));
                }
                imageView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_show));

                if (onTabSelectedListener != null) {
                    onTabSelectedListener.onTabSelected(tab);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (onTabSelectedListener != null) {
                    onTabSelectedListener.onTabUnselected(tab);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (onTabSelectedListener != null) {
                    onTabSelectedListener.onTabReselected(tab);
                }
            }
        });
    }

    /**
     * 设置TabLayout TabMode
     * @param mode
     * @return
     */
    public AbCoordinatorTabLayout setTabMode(@TabLayout.Mode int mode) {
        tabLayout.setTabMode(mode);
        return this;
    }

    /**
     * 设置与该组件搭配的ViewPager
     * @param viewPager 与TabLayout结合的ViewPager
     * @return
     */
    public AbCoordinatorTabLayout setupWithViewPager(ViewPager viewPager) {
        setupTabLayout();
        tabLayout.setupWithViewPager(viewPager);
        return this;
    }

    /**
     * 获取该组件中的ActionBar
     */
    public ActionBar getActionBar() {
        return actionbar;
    }

    /**
     * 获取该组件中的TabLayout
     */
    public TabLayout getTabLayout() {
        return tabLayout;
    }

    /**
     * 获取该组件中的ImageView
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * 设置LoadHeaderImagesListener
     * @param loadHeaderImagesListener 设置LoadHeaderImagesListener
     * @return
     */
    public AbCoordinatorTabLayout setLoadHeaderImagesListener(LoadHeaderImagesListener loadHeaderImagesListener) {
        this.loadHeaderImagesListener = loadHeaderImagesListener;
        return this;
    }

    /**
     * 设置onTabSelectedListener
     * @param onTabSelectedListener 设置onTabSelectedListener
     * @return
     */
    public AbCoordinatorTabLayout addOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        this.onTabSelectedListener = onTabSelectedListener;
        return this;
    }

    public void setTabLayout(TabLayout tabLayout) {
        this.tabLayout = tabLayout;
    }

    public AppBarLayout getAppBarLayout() {
        return appBarLayout;
    }

    public void setAppBarLayout(AppBarLayout appBarLayout) {
        this.appBarLayout = appBarLayout;
    }

    public CollapsingToolbarLayout getCollapsingToolbarLayout() {
        return collapsingToolbarLayout;
    }

    public void setCollapsingToolbarLayout(CollapsingToolbarLayout collapsingToolbarLayout) {
        this.collapsingToolbarLayout = collapsingToolbarLayout;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    public FrameLayout getHeaderView() {
        return headerView;
    }

    public void setHeaderView(FrameLayout headerView) {
        this.headerView = headerView;
    }

    public interface OnTabSelectedListener {

        public void onTabSelected(TabLayout.Tab tab);

        public void onTabUnselected(TabLayout.Tab tab);

        public void onTabReselected(TabLayout.Tab tab);
    }

    public interface LoadHeaderImagesListener {
        void loadHeaderImages(ImageView imageView, TabLayout.Tab tab);
    }

}