package y2w.Account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import y2w.service.AotoLoginService;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String ACCOUNT_TYPE = "com.yun2win.demo";
    private static final String TAG = "SyncAdapter";
    private static final String SYNC_MARKER_KEY = "com.yun2win.demo.marker";
    private final AccountManager mAccountManager;
    private final Context mContext;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        try {
            long lastSyncMarker = getServerSyncMarker(account);
            if (lastSyncMarker == 0) {
                setAccountContactsVisibility(getContext(), account, true);
            }
            setServerSyncMarker(account, ++lastSyncMarker);
            mContext.startService(new Intent(mContext, AotoLoginService.class));
        } catch (final Exception e) {
            Log.e(TAG, "JSONException", e);
            syncResult.stats.numParseExceptions++;
        }
    }

    /**
     * 得到标识
     */
    private long getServerSyncMarker(Account account) {
        String markerString = mAccountManager.getUserData(account, SYNC_MARKER_KEY);
        if (!TextUtils.isEmpty(markerString)) {
            return Long.parseLong(markerString);
        }
        return 0;
    }

    /**
     * 设置标识
     */
    private void setServerSyncMarker(Account account, long marker) {
        mAccountManager.setUserData(account, SYNC_MARKER_KEY, Long.toString(marker));
    }

    /**
     * 设置对此帐号可见
     */
    public void setAccountContactsVisibility(Context context, Account account, boolean visible) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.RawContacts.ACCOUNT_NAME, account.name);
        values.put(ContactsContract.RawContacts.ACCOUNT_TYPE, ACCOUNT_TYPE);
        values.put(ContactsContract.Settings.UNGROUPED_VISIBLE, visible ? 1 : 0);

        context.getContentResolver().insert(ContactsContract.Settings.CONTENT_URI, values);
    }
}

