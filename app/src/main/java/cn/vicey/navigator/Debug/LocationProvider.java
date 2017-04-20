package cn.vicey.navigator.Debug;

import android.graphics.Point;

import java.util.Random;

public final class LocationProvider
{
    public static Point getCurrentLocation()
    {
        Random random = new Random();
        return new Point(random.nextInt(), random.nextInt());
    }

    private LocationProvider()
    {
        // no-op
    }
}
