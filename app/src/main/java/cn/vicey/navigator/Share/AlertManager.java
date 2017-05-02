package cn.vicey.navigator.Share;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.Toast;
import cn.vicey.navigator.Activities.MainActivity;
import cn.vicey.navigator.Utils.Logger;

/**
 * Alert manager, provides a set of methods to help alert message
 */
public final class AlertManager
{
    //region Constants

    private static final String LOGGER_TAG = "AlertManager";

    private static final Object SYNC_LOCK = new Object();  // Sync lock

    /**
     * Long toast duration (3500 ms) constant
     */
    public static final long LONG_TOAST_DURATION   = 3500;
    /**
     * Middle toast duration (2000 ms) constant
     */
    public static final long MIDDLE_TOAST_DURATION = 2000;
    /**
     * Short toast duration (1000 ms) constant
     */
    public static final long SHORT_TOAST_DURATION  = 1000;

    //endregion

    //region Static fields

    private static Toast        mCurrentToast;
    private static MainActivity mMainActivity;

    //endregion

    //region Static methods

    /**
     * Alert a message
     *
     * @param redId Message string resource id
     */
    public static void alert(int redId)
    {
        alert(mMainActivity.getString(redId), MIDDLE_TOAST_DURATION);
    }

    /**
     * Alert a message with specified duration
     *
     * @param redId    Message string resource id
     * @param duration Duration
     */
    public static void alert(int redId, final long duration)
    {
        mMainActivity.getString(redId, duration);
    }

    /**
     * Alert a message
     *
     * @param message Message string
     */
    public static void alert(final @NonNull String message)
    {
        alert(message, MIDDLE_TOAST_DURATION);
    }

    /**
     * Alert a message with specified duration
     *
     * @param message  Message string
     * @param duration Duration
     */
    public static void alert(final @NonNull String message, final long duration)
    {
        try
        {
            synchronized (SYNC_LOCK)
            {
                mMainActivity.invoke(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mCurrentToast != null) mCurrentToast.cancel();
                        mCurrentToast = Toast.makeText(mMainActivity, message, Toast.LENGTH_LONG);
                        mCurrentToast.show();
                        final Toast toastToCancel = mCurrentToast;
                        new Handler().postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                toastToCancel.cancel();
                            }
                        }, duration);
                    }
                });
            }
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to alert message.", t);
        }
    }

    /**
     * Initialize alert manager
     *
     * @param mainActivity Related main activity
     * @return Whether the initialization is succeed or not
     */
    public static boolean init(final @NonNull MainActivity mainActivity)
    {
        mMainActivity = mainActivity;
        return true;
    }

    //endregion

    //region Constructor

    /**
     * Hidden for static class design pattern
     */
    private AlertManager()
    {
        // no-op
    }

    //endregion
}
