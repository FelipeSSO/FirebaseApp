package com.felipesotero.firebaseapp.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.felipesotero.firebaseapp.R;
import com.felipesotero.firebaseapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.felipesotero.firebaseapp.util.App.CHANNEL_1;

public class NotificationService extends Service {
    private ValueEventListener listener;
    private DatabaseReference receiveRef;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void showNotify(User user){
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1)
                                    .setSmallIcon(R.drawable.ic_account_circle_black_24dp)
                                    .setContentTitle("Alteração!").setContentText(user.getNome())
                                    .setPriority(Notification.PRIORITY_HIGH).build();
        // Enviando para Channel
        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        receiveRef = FirebaseDatabase.getInstance().getReference("users")
                  .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // É executado quando o servico é criado -> uma vez
        listener = receiveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                showNotify(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // É executado quando o serviço é chamado
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        receiveRef.removeEventListener(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
