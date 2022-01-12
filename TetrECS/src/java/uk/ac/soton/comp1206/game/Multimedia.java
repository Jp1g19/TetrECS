package uk.ac.soton.comp1206.game;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Multimedia is used to provide audio players to play sounds and music
 */
public class Multimedia {
    private static final Logger logger = LogManager.getLogger(Multimedia.class);
    static boolean playing = false;
    public static MediaPlayer musicPlayer;
    public static double musicVolume = 0.1;
    public static MediaPlayer audioPlayer;
    public static double SFXVolume = 0.2;


    /**
     * Sets background music to be played
     *
     * @param song
     */
    public static void setMusicPlayer(String song) {
        logger.info("Playing Music " + song);
        if (playing) {
            musicPlayer.stop();
        }
        Media media = new Media(Multimedia.class.getResource("/music/" + song).toExternalForm());
        musicPlayer = new MediaPlayer(media);
        musicPlayer.setAutoPlay(true);
        musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        playing = true;
        musicPlayer.setVolume(musicVolume);
        musicPlayer.play();
    }

    /**
     * Sets SFX to be played
     *
     * @param sound
     */
    public static void setAudioPlayer(String sound) {
        logger.info("Playing Sound " + sound);
        Media media = new Media(Multimedia.class.getResource("/sounds/" + sound).toExternalForm());
        audioPlayer = new MediaPlayer(media);
        playing = true;
        audioPlayer.setVolume(SFXVolume);
        audioPlayer.play();
    }
}
