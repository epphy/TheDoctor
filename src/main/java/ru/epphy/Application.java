package ru.epphy;

import ru.epphy.bot.TheDoctorHeart;

public class Application {

    public static void main(String[] args) {
        startHeart();
        setupShutdownHook();
    }

    private static void startHeart() {
        TheDoctorHeart.init(System.getenv("DISCORD_TOKEN"));
        TheDoctorHeart.getInstance().startHeartbeat();
    }

    private static void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(Application::stopHeart));
    }

    private static void stopHeart() {
        TheDoctorHeart.getInstance().stopHeartbeat();
        TheDoctorHeart.unload();
    }

}