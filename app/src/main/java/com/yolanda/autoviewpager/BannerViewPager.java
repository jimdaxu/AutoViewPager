/*
 * AUTHOR：Yolanda
 * 
 * DESCRIPTION：create the File, and add the content.
 *
 * Copyright © ZhiMore. All Rights Reserved
 *
 */
package com.yolanda.autoviewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 轮播图，需要在activity生命周期控制轮播
 */
public class BannerViewPager extends RelativeLayout {

    private LoopViewPager viewPager;

    private LinearLayout llIndecator;
    /**
     * 播放时间
     */
    private int showTime = 3 * 1000;
    /**
     * 滚动方向
     */
    private Direction direction = Direction.LEFT;
    /**
     * 播放器
     */
    private Runnable player = new Runnable() {
        @Override
        public void run() {
            play(direction);
        }
    };

    private int indicatorSelected;
    private int indicatorUnselected;
    private boolean indicatorShow;
    private boolean bannerScroll;
    private List<View> indicators;
    private int currentItem;
    public BannerViewPager(Context context) {
        super(context);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.banner_view_pager, this);
        viewPager = (LoopViewPager) view.findViewById(R.id.vp_banner);
        llIndecator = (LinearLayout) view.findViewById(R.id.ll_indicator);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.banner);
        indicatorSelected = ta.getInt(R.styleable.banner_indicator_select_drawable, 0);
        indicatorUnselected = ta.getInt(R.styleable.banner_indicator_unselect_drawable, 0);
        indicatorShow = ta.getBoolean(R.styleable.banner_indicator_show, true);
        bannerScroll = ta.getBoolean(R.styleable.banner_banner_scroll, true);
        indicators = new ArrayList<>();
    }

    private void setIndicator() {//初始化指示器
        int size = viewPager.getAdapter().getCount();
        if (!indicatorShow || size <= 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            ImageView view = new ImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = 10;
            if (indicatorSelected > 0 && indicatorUnselected > 0) {//选中和未选中要同时选才有效果，否则就为默认
                view.setBackgroundResource(i == viewPager.getCurrentItem() ? indicatorSelected : indicatorUnselected);
            } else {
                view.setBackgroundResource(i == viewPager.getCurrentItem() ? R.drawable.banner_focused : R.drawable.bannder_normal);
            }
            indicators.add(view);
            llIndecator.addView(view,params);
        }
        if(bannerScroll){
            start();
        }
    }

    private void changeIndicator() {//轮播指示器改变
        int size = viewPager.getAdapter().getCount();
        if (!indicatorShow || size <= 1 || indicators.size()==0) {
            return;
        }
        for (int i = 0; i < size; i++) {
            if (indicatorSelected > 0 && indicatorUnselected > 0) {//选中和未选中要同时选才有效果，否则就为默认
                indicators.get(i).setBackgroundResource(i == viewPager.getCurrentItem() ? indicatorSelected : indicatorUnselected);
            } else {
                indicators.get(i).setBackgroundResource(i == viewPager.getCurrentItem() ? R.drawable.banner_focused : R.drawable.bannder_normal);
            }
        }
    }

    /**
     * 设置播放时间，默认3秒
     *
     * @param showTimeMillis 毫秒
     */
    public void setShowTime(int showTimeMillis) {
        this.showTime = showTime;
    }

    /**
     * 设置滚动方向，默认向左滚动
     *
     * @param direction 方向
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * 开始
     */
    public void start() {
        stop();
        postDelayed(player, showTime);
    }

    /**
     * 停止
     */
    public void stop() {
        removeCallbacks(player);
    }

    /**
     * 播放上一个
     */
    public void previous() {
        if (direction == Direction.RIGHT) {
            play(Direction.LEFT);
        } else if (direction == Direction.LEFT) {
            play(Direction.RIGHT);
        }
    }

    /**
     * 播放下一个
     */
    public void next() {
        play(direction);
    }

    /**
     * 执行播放
     *
     * @param direction 播放方向
     */
    private synchronized void play(Direction direction) {
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        int count = pagerAdapter.getCount();
        viewPager.setCurrentItem(currentItem);
        pagerAdapter.notifyDataSetChanged();
        changeIndicator();
        if (pagerAdapter != null) {
//            int max = Integer.MAX_VALUE;
            switch (direction) {
                case LEFT:
                    currentItem++;
                    if (currentItem > count -1)
                        currentItem = 0;
                    break;
                case RIGHT:
                    currentItem--;
                    if (currentItem < 0)
                        currentItem = count -1;
                    break;
            }
        }
        start();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                changeIndicator();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    int count = viewPager.getAdapter().getCount();
                    if (currentItem > count - 1) {
                        currentItem = 0;
                    } else if (currentItem <0){
                        currentItem = count -1;
                    }
                    start();
                }
                else if (state == ViewPager.SCROLL_STATE_DRAGGING)
                    stop();
            }
        });
    }

    public enum Direction {
        /**
         * 向左行动，播放的应该是右边的
         */
        LEFT,

        /**
         * 向右行动，播放的应该是左边的
         */
        RIGHT
    }

    public void setBannerAdapter(PagerAdapter bannerAdapter){//设置适配器并且开始轮播
        viewPager.setAdapter(bannerAdapter);
        setIndicator();
    }
}