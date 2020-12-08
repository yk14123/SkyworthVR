package com.chinafocus.hvrskyworthvr.util.widget;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class BaseFragmentStateAdapter<T extends Fragment> extends FragmentStateAdapter {
    private List<T> mFragments;

    public BaseFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity, List<T> fragments) {
        super(fragmentActivity);
        this.mFragments = fragments;
    }

    public BaseFragmentStateAdapter(@NonNull Fragment fragment, List<T> fragments) {
        super(fragment);
        this.mFragments = fragments;
    }

    @NonNull
    @Override
    public T createFragment(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragments.size();
    }

}

