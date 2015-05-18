package com.skinrun.trunk.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.beabox.hjy.tt.R;

/**
 *
 */
public class SplashPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    public static interface SplashPagerAdapterListener {
        void startExperience();
    }

    private List<View> pagerViews;
    private SplashPagerAdapterListener splashPagerAdapterListener;
    protected ViewGroup pointGroup;
    int banner_point_size = 8;
    int banner_point_marginLeft = 5;

    public SplashPagerAdapter(Context context,ViewGroup pointGroup, SplashPagerAdapterListener splashPagerAdapterListener) {
        this.splashPagerAdapterListener = splashPagerAdapterListener;
        this.pointGroup = pointGroup;
        pagerViews = new ArrayList<View>();

        pointGroup.removeAllViews();
        banner_point_size = context.getResources().getDimensionPixelSize(R.dimen.splash_point_size);
        banner_point_marginLeft = context.getResources().getDimensionPixelSize(R.dimen.splash_point_marginLeft);
        ViewGroup.MarginLayoutParams pointParam = new ViewGroup.MarginLayoutParams(banner_point_size, banner_point_size);

        LayoutInflater inflater = LayoutInflater.from(context);
        for (int i = 1; i <= 4; i++) {
            View view = inflater.inflate(R.layout.splash_pager_imageview, null);
            assert view != null;
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            String drawable = "drawable/splash_" + i;
            int resId = context.getResources().getIdentifier(drawable, null, context.getPackageName());
            if (resId != -1) {
                imageView.setImageDrawable(context.getResources().getDrawable(resId));
                pagerViews.add(view);

                //指示器
                View view1 = new View(context);
                view1.setLayoutParams(pointParam);
                if(i == 1){
                    view1.setEnabled(true);
                }else {
                    view1.setEnabled(false);
                }
                view1.setBackgroundResource(R.drawable.circle_point_selector);
                pointGroup.addView(view1);
                ((ViewGroup.MarginLayoutParams) view1.getLayoutParams()).setMargins(banner_point_marginLeft, 0, 0, 0);
            }
        }
    }

    @Override
    public int getCount() {
        return pagerViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = pagerViews.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(pagerViews.get(position));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == (pagerViews.size() - 1)) {
            View view = pagerViews.get(position);
            Button startExperienceBtn = (Button) view.findViewById(R.id.startExperienceBtn);
            startExperienceBtn.setVisibility(View.VISIBLE);
            startExperienceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (splashPagerAdapterListener != null) {
                        splashPagerAdapterListener.startExperience();
                    }
                }
            });
        }

        int size = pointGroup.getChildCount();
        if (size == 0 || position >= size) {
            return;
        }

        for (int j = 0; j < size; j++) {
            if (j == position) {
                pointGroup.getChildAt(j).setEnabled(true);
            } else {
                pointGroup.getChildAt(j).setEnabled(false);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
