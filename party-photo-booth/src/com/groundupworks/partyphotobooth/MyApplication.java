/*
 * This file is part of Flying PhotoBooth.
 * 
 * Flying PhotoBooth is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Flying PhotoBooth is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Flying PhotoBooth.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.groundupworks.partyphotobooth;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Handler;

import com.groundupworks.lib.photobooth.framework.BaseApplication;

import java.util.List;

/**
 * A concrete {@link BaseApplication} class.
 *
 * @author Benedict Lau
 */
public class MyApplication extends BaseApplication {

    /**
     * Bitmap cache with application scope.
     */
    private static PersistedBitmapCache mCache;

    @Override
    public void onCreate() {
        super.onCreate();
        mCache = new PersistedBitmapCache(this, new Handler(getWorkerLooper()), new Handler(getMainLooper()));
        boolean tStartService = true;
        ActivityManager am = (ActivityManager)this.getSystemService(this.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(500);
        for (int  j = 0;j < rs.size(); j++) {
            ActivityManager.RunningServiceInfo rsi = rs.get(j);
            if(SendMailService.class.getName().equalsIgnoreCase(rsi.service.getClassName())){
                tStartService = false;
                break;
            }
        }
        if(tStartService){
            startService(new Intent(this, SendMailService.class));
        }
    }

    //
    // Public methods.
    //

    /**
     * Gets the bitmap cache with application scope.
     *
     * @return the bitmap cache.
     */
    public static PersistedBitmapCache getBitmapCache() {
        return mCache;
    }
}
