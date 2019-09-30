package com.qqcs.smartHouse.utils;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.text.TextUtils;
import android.widget.ImageView;

import com.qqcs.smartHouse.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

public class ImageLoaderUtil {
    /**
     * String imageUri = "http://site.com/image.png"; // from Web
     * String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
     * String imageUri = "content://media/external/audio/albumart/13"; // from content provider
     * String imageUri = "assets://image.png"; // from assets
     * String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * *
     **/

    private DisplayImageOptions mOptions;

    private static ImageLoaderUtil mInstance;

    public static ImageLoaderUtil getInstance() {
        if (mInstance == null) {
            mInstance = new ImageLoaderUtil();
        }
        return mInstance;
    }

    /**
     * ImageLoaderUtil
     */
    private ImageLoaderUtil() {
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.color.white) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.color.white) //设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.color.white)    //设置图片加载/解码过程中错误时候显示的图片
                //设置下载的图片是否缓存在内存中,//设置下载的图片是否缓存在SD卡中
                //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT) //设置图片以如何的编码方式显示
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                //.displayer(new FadeInBitmapDisplayer(300))//设置加载图片的task（这里是渐现）
                .bitmapConfig(Config.RGB_565).build();  //设置图片的解码类型
    }

    /**
     * init ImageLoader
     */
    public static void initImageLoader(Context context) {
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(context);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(4)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCacheSize(5 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000))// connectTimeout (5 s), readTimeout (30 s)
                .build();

        ImageLoader.getInstance().init(config);
    }


    /**
     * displayImage
     *
     * @param url
     * @param fullImage
     */
    public void displayImage(final String url, final ImageView fullImage) {

        if (TextUtils.isEmpty(url) || url.equals("https:null")) {

            return;
        }

        ImageAware imageAware = new ImageViewAware(fullImage, false);

        ImageLoader.getInstance().displayImage(url, imageAware, mOptions);

    }


    /**
     * remove image by url
     */
    public static void removeFromCache(String url) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        MemoryCacheUtils.removeFromCache(url,imageLoader.getMemoryCache());
        DiskCacheUtils.removeFromCache(url,imageLoader.getDiskCache());
    }

    /**
     * clearMemryCache
     */
    public static void clearMemryCache() {
        ImageLoader.getInstance().clearMemoryCache();
    }

    /**
     * clearDiskCache
     */
    public static void clearDiskCache() {
        ImageLoader.getInstance().clearDiskCache();
    }

}
