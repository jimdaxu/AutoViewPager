package com.yolanda.autoviewpager;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class LoopViewTestPager extends ViewPager {

    private InnerPagerAdapter mAdapter;

    public LoopViewTestPager(Context context) {
        super( context);
        setOnPageChangeListener( null);
    }

    public LoopViewTestPager(Context context, AttributeSet attrs) {
        super( context, attrs);
        setOnPageChangeListener( null);
    }

    @Override
    public void setAdapter(PagerAdapter arg0) {
        mAdapter = new InnerPagerAdapter( arg0);
        super.setAdapter( mAdapter);
//        setCurrentItem( 1);
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        super.setOnPageChangeListener( new InnerOnPageChangeListener( listener));
    }

    private class InnerOnPageChangeListener implements OnPageChangeListener {

        private OnPageChangeListener listener;
        private int position;

        public InnerOnPageChangeListener(OnPageChangeListener listener) {
            this.listener = listener;
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            if(null != listener) {
                listener.onPageScrollStateChanged( arg0);
            }
            if(arg0 == ViewPager.SCROLL_STATE_IDLE) {
                if(position == mAdapter.getCount() - 1) {
                    setCurrentItem( 1, false);
                }
                else if(position == 0) {
                    setCurrentItem( mAdapter.getCount() - 2, false);
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            if(null != listener) {
                listener.onPageScrolled( arg0, arg1, arg2);
            }
        }

        @Override
        public void onPageSelected(int arg0) {
            position = arg0;
            if(null != listener) {
                listener.onPageSelected( arg0);
            }
        }
    }

    private class InnerPagerAdapter extends PagerAdapter {

        public InnerPagerAdapter(PagerAdapter adapter) {
//            this.adapter = adapter;
            update();
            adapter.registerDataSetObserver( new DataSetObserver() {

                @Override
                public void onChanged() {
                    notifyDataSetChanged();
                }

                @Override
                public void onInvalidated() {
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getCount() {
            return resIds.size() + 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if(position == 0) {
                position = resIds.size() - 1;
            }
            else if(position == resIds.size() + 1) {
                position = 0;
            }
            else {
                position -= 1;
            }
            ImageView imageView = new ImageView(getContext());
            // 如果是http://www.xx.com/xx.png这种连接，这里可以用ImageLoader之类的框架加载
            imageView.setImageResource(resIds.get(position));
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        private List<Integer> resIds = new ArrayList<>();
        public void update() {
            if (this.resIds != null)
                this.resIds.clear();
            if (resIds != null)
                this.resIds = resIds;
            resIds = new ArrayList<>();
            // 模拟几张图片
            resIds.add(R.mipmap.ic_launcher);
            resIds.add(R.mipmap.nohttp);
            resIds.add(R.mipmap.nohttp_delete);
            resIds.add(R.mipmap.nohttp_des);
            resIds.add(R.mipmap.nohttp_get);
            resIds.add(R.mipmap.nohttp_head);
            resIds.add(R.mipmap.nohttp_options);
            resIds.add(R.mipmap.nohttp_patch);
            resIds.add(R.mipmap.nohttp_post);
            resIds.add(R.mipmap.nohttp_put);
            resIds.add(R.mipmap.nohttp_trace);
        }
    }
}