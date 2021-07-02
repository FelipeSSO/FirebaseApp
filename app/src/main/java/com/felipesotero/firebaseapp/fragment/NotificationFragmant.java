package com.felipesotero.firebaseapp.fragment;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDeepLinkBuilder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.felipesotero.firebaseapp.NavigationActivity;
import com.felipesotero.firebaseapp.R;
import com.felipesotero.firebaseapp.UpdateActivity;
import com.felipesotero.firebaseapp.util.App;

import static com.felipesotero.firebaseapp.util.App.CHANNEL_1;

public class NotificationFragmant extends Fragment {
    private NotificationManagerCompat notificationManager;
    private EditText editTitle, editMsg;
    private Button btnSend;

    public NotificationFragmant() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_notification, container, false);
        notificationManager = NotificationManagerCompat.from(getContext());
        editTitle = layout.findViewById(R.id.frag_notification_title);
        editMsg = layout.findViewById(R.id.frag_notification_msg);
        btnSend = layout.findViewById(R.id.frag_notification_bnt_send);

        btnSend.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String msg = editMsg.getText().toString();
            Intent intent = new Intent(getContext(), NavigationActivity.class);
            //PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

            PendingIntent contentIntent = new NavDeepLinkBuilder(getContext())
                                         .setComponentName(NavigationActivity.class)
                                         .setGraph(R.navigation.nav_graph)
                                         .setDestination(R.id.nav_menu_lista_imagens)
                                         .createPendingIntent();

            // Criar a Notificação
            Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_1)
                                        .setSmallIcon(R.drawable.ic_account_circle_black_24dp)
                                        .setContentTitle(title).setContentText(msg)
                                        .setContentIntent(contentIntent)
                                        //.addAction(R.drawable.ic_account_circle_black_24dp, "Toast", actionIntent)
                                        .setPriority(Notification.PRIORITY_HIGH).build();

            notificationManager.notify(1, notification);
        });

        return layout;
    }
}
