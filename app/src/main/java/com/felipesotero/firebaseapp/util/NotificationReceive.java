package com.felipesotero.firebaseapp.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("Toast");
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        Intent intent1 = new Intent();
        intent1.setClassName("com.felipesotero.firebaseapp",
                "com.felipesotero.firebaseapp.NavigationActivity");

        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);

        /*PendingIntent intentfragment = new NavDeepLinkBuilder(context)
                                .setComponentName(NavigationActivity.class).setGraph(R.navigation.nav_graph)
                                .setDestination(R.id.nav_menu_cadastro_imagens).createPendingIntent();
                                */

    }

}
