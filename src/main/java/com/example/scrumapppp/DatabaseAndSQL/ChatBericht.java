package com.example.scrumapppp.DatabaseAndSQL;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatBericht {
    private String groep;
    private String gebruiker;
    private String bericht;
    private LocalDateTime tijd;

    public ChatBericht(String groep, String gebruiker, String bericht, LocalDateTime tijd) {
        // Rond tijd af op hele minuten
        this.groep = groep;
        this.gebruiker = gebruiker;
        this.bericht = bericht;
        this.tijd = tijd.withSecond(0).withNano(0);
    }

    public String getGroep() { return groep; }
    public String getGebruiker() { return gebruiker; }
    public String getBericht() { return bericht; }
    public LocalDateTime getTijd() { return tijd; }

    public String getFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return "[" + tijd.format(formatter) + "] " + gebruiker + ": " + bericht;
    }
}
