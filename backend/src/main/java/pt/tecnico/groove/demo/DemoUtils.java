package pt.tecnico.groove.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DemoUtils {
    @Autowired
    private DemoService demoService;

    public void resetDemoInfo() {
        demoService.resetDemoSongs();
        demoService.resetDemoOwners();
    }
}
