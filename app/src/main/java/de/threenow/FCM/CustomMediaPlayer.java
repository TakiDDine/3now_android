package de.threenow.FCM;

import android.content.Context;
import android.media.MediaPlayer;

import de.threenow.R;

public class CustomMediaPlayer {

    public static MediaPlayer mediaPlayer;
    static String typeRainingSet = "";

    public static void RunRaining(String typeRaining, Context context) {

        typeRainingSet = typeRaining;

        if (typeRaining.equalsIgnoreCase("message")) {
            mediaPlayer = MediaPlayer.create(context, R.raw.come_message);
            mediaPlayer.setLooping(false);
        }


        if (!mediaPlayer.isPlaying())
            mediaPlayer.start();

    }

    public static void PauseRaining(String typeRaining) {
        if (typeRaining.equals(typeRainingSet)) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }

    }

}
