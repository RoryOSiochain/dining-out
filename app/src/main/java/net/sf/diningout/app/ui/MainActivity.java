/*
 * Copyright 2013-2015 pushbit <pushbit@gmail.com>
 *
 * This file is part of Dining Out.
 *
 * Dining Out is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Dining Out is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Dining Out. If not,
 * see <http://www.gnu.org/licenses/>.
 */

package net.sf.diningout.app.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.GoogleApiAvailability;

import net.sf.sprockets.preference.Prefs;

import static com.google.android.gms.common.ConnectionResult.SUCCESS;
import static net.sf.diningout.preference.Keys.App.APP;
import static net.sf.diningout.preference.Keys.App.ONBOARDED;
import static net.sf.sprockets.gms.analytics.Trackers.event;

/**
 * Verifies that the required version of Google Play services is available and then forwards to
 * {@link InitActivity} for onboarding or {@link RestaurantsActivity} for normal use.
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            tryToContinue();
        }
    }

    /**
     * If Google Play services is available, continue to next Activity.
     */
    private void tryToContinue() {
        GoogleApiAvailability avail = GoogleApiAvailability.getInstance();
        int status = avail.isGooglePlayServicesAvailable(this);
        if (status == SUCCESS) {
            startActivity(new Intent(this, !Prefs.getBoolean(this, APP, ONBOARDED)
                    ? InitActivity.class : RestaurantsActivity.class));
            finish();
            overridePendingTransition(0, 0);
        } else {
            avail.showErrorDialogFragment(this, status, 0, new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
            event("gms", "not available", avail.getErrorString(status));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        tryToContinue();
    }
}
