package com.chinafocus.hvrskyworthvr;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.AppGlideModule;

import java.util.Objects;

@GlideModule
public final class MyAppGlideModule extends AppGlideModule {
    //可以配置Glide
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        builder.setDiskCache(new DiskLruCacheFactory(Objects.requireNonNull(context.getApplicationContext().getExternalFilesDir("Images")).getAbsolutePath(), 512 * 1024 * 1024));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}