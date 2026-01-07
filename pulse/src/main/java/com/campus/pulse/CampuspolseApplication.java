/*package com.campus.pulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class CampuspolseApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampuspolseApplication.class, args);
	}

}
*/

package com.campus.pulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
public class CampuspolseApplication {

    public static void main(String[] args) {
        // This line makes sure the app can detect a screen/mouse/keyboard
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(CampuspolseApplication.class, args);
    }

    // LISTENER: Waits for the App to be "Ready", then opens Chrome/Edge
    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser() {
        String url = "http://localhost:8080/index.html";
        System.out.println("🚀 App Started! Opening Browser: " + url);

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                // Windows-specific fallback command
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}